package com.github.ws.retrofitview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.ws.retrofitview.body.CountingRequestBody;
import com.github.ws.retrofitview.rest.RestClient;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 7/29 0029.
 */
public class UploadSimpleFileActivity extends AppCompatActivity {

    private TextView tvFile;
    private Button btnUpload;
    private RestClient mRestClient;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_file);

        tvFile = (TextView) findViewById(R.id.tv_file);
        btnUpload = (Button) findViewById(R.id.btn_upload);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        File file = new File("/sdcard/", "a.xlxs");
                        //file
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                        //监听上传进度
                        CountingRequestBody countingRequestBody = new CountingRequestBody(requestFile, new CountingRequestBody.Listener() {
                            @Override
                            public void onRequestProgress(long bytesWritten, long contentLength) {
                                tvFile.setText("上传进度:" + contentLength + ":" + bytesWritten);
                            }
                        });

                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("file", file.getName(),countingRequestBody);

                        mRestClient = new RestClient("http://192.168.4.111:686/");

                        Call<ResponseBody> responseBodyCall = mRestClient.getRectService().uploadSimpleFile(body);

                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                tvFile.setText("上传成功");
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                tvFile.setText(t.toString());
                            }
                        });
                        return null;
                    }
                }.execute();

            }
        });
    }
}
