package com.github.ws.retrofitview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.ws.retrofitview.rest.RestClient;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 7/29 0029.
 */
public class SimpleGetActivity extends AppCompatActivity {

    private Button btnGet;
    private TextView tvResult;

    private RestClient mRestClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_get);

        btnGet = (Button) findViewById(R.id.btn_get);
        tvResult = (TextView) findViewById(R.id.tv_result);


        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取实例
                mRestClient = new RestClient();

                Call<ResponseBody> responseBodyCall = mRestClient.getRectService().getManagerData(49);
                //调用回调接口
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            tvResult.setText(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });

            }
        });
    }
}
