package com.github.ws.retrofitview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.animation.AnimationType;
import com.github.library.listener.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    private RestClient mClient;
//
//    public static String KEY = "Rsg+8rx5M38xinuYepD+oQiUdUE=";
//
//    public String mFileName;

    private RecyclerView mRecyclerView;

    private BaseRecyclerAdapter<String> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<String>(this, getDatas(), R.layout.rv_item) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_item_text, item);
            }
        });
        mAdapter.openLoadAnimation(AnimationType.SCALE);

        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(MainActivity.this, SimpleGetActivity.class);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, GetActivity.class);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, PostActivity.class);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, UploadSimpleFileActivity.class);
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this, UploadMultiFileActivity.class);
                        break;
                    case 5:
                        intent = new Intent(MainActivity.this, DownFileActivity.class);
                        break;
                    case 6:
                        intent = new Intent(MainActivity.this, RxJavaActivity.class);
                        break;

                }
                startActivity(intent);
            }
        });


    }

    public List<String> getDatas() {
        List<String> datas = new ArrayList<>();
        datas.add("Simple Get Activity");
        datas.add("Get Activity");
        datas.add("Post Activity");
        datas.add("Upload Simple File Activity");
        datas.add("Upload multi File Activity");
        datas.add("Down File Activity");
        datas.add("RxJava Activity");
        return datas;
    }
}
