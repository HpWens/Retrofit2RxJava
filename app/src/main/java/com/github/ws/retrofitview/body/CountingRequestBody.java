package com.github.ws.retrofitview.body;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by Administrator on 5/18 0018.
 */
public class CountingRequestBody extends RequestBody {

    protected RequestBody delegate;
    protected Listener listener;

    protected CountingSink countingSink;

    public CountingRequestBody(RequestBody delegate, Listener listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) {
        countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = null;
        try {
            bufferedSink = Okio.buffer(countingSink);
            delegate.writeTo(bufferedSink);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                bufferedSink.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    protected final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) {
            try {
                super.write(source, byteCount);
                if (listener != null) {
                    bytesWritten += byteCount;
                    listener.onRequestProgress(bytesWritten, contentLength());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface Listener {
        void onRequestProgress(long bytesWritten, long contentLength);
    }
}
