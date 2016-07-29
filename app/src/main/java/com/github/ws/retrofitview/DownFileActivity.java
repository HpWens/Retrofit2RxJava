package com.github.ws.retrofitview;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.ws.retrofitview.rest.RestClient;

import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 7/29 0029.
 */
public class DownFileActivity extends AppCompatActivity {

    private ImageView iv;
    private Button btnDown;
    private RestClient mRestClient;

    private String fileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_file);

        iv = (ImageView) findViewById(R.id.iv);
        btnDown = (Button) findViewById(R.id.btn_down);

        mRestClient = new RestClient("http://d.hiphotos.baidu.com/");

        fileName = "b812c8fcc3cec3fd8757dcefd488d43f8794273a.jpg";

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> userCall = mRestClient.getRectService().downFile(fileName);
                userCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        iv.setImageBitmap(BitmapFactory.decodeStream(response.body().byteStream()));
                        //saveFile(response.body().byteStream());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });
    }

    public void saveFile(InputStream is){
        try {
            String fn = Environment.getExternalStorageDirectory() + "/" + fileName;
            FileOutputStream fos = new FileOutputStream(fn);
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            is.close();
            fos.close();
        } catch (Exception ex) {

        }
    }
}
