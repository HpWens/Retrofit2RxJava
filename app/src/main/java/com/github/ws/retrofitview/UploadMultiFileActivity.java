package com.github.ws.retrofitview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.ws.retrofitview.body.CountingRequestBody;
import com.github.ws.retrofitview.rest.RestClient;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 7/29 0029.
 */
public class UploadMultiFileActivity extends AppCompatActivity {

    //保证文件按顺序上传 使用LinkedHashMap
    Map<String, RequestBody> params;

    private TextView tvFile1;
    private TextView tvFile2;
    private Button btnUpload;

    private RestClient mRestClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_file);

        tvFile1 = (TextView) findViewById(R.id.tv_file1);
        tvFile2 = (TextView) findViewById(R.id.tv_file2);
        btnUpload = (Button) findViewById(R.id.btn_upload);

        initData();

    }

    private void initData() {

        params = new LinkedHashMap<>();

        File file1 = new File("/sdcard/", "a.xlxs");
        final RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
        //监听上传进度
        CountingRequestBody countingRequestBody1 = new CountingRequestBody(requestBody1, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength) {
                tvFile1.setText("上传进度:" + contentLength + ":" + bytesWritten);
            }
        });

        params.put("file\";filename=\"" + file1.getName(), countingRequestBody1);


        File file2 = new File("/sdcard/", "a.xlxs");
        RequestBody requestBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);
        //监听上传进度
        CountingRequestBody countingRequestBody2 = new CountingRequestBody(requestBody2, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength) {
                tvFile2.setText("上传进度:" + contentLength + ":" + bytesWritten);
            }
        });

        params.put("file\";filename=\"" + file2.getName(), countingRequestBody2);


        mRestClient = new RestClient("http://192.168.4.111:686/");

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> responseBodyCall = mRestClient.getRectService().uploadMultiFiles(params);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        tvFile1.setText("上传成功");
                        tvFile2.setText("上传成功");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });

    }
}
