package com.github.ws.retrofitview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.ws.retrofitview.model.BaseResponse;
import com.github.ws.retrofitview.model.Manager;
import com.github.ws.retrofitview.rest.RestClient;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 7/29 0029.
 */
public class RxJavaActivity extends AppCompatActivity {
    private Button btnGet;

    private RestClient mRestClient;

    private RecyclerView mRecyclerView;

    private BaseRecyclerAdapter<Manager> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get);

        btnGet = (Button) findViewById(R.id.btn_get);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取实例
                mRestClient = new RestClient();

                mRestClient.getRectService().getManagers(49)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<BaseResponse<Manager>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(BaseResponse<Manager> managerBaseResponse) {
                                //获取返回的集合数据
                                mAdapter = new BaseRecyclerAdapter<Manager>(RxJavaActivity.this, managerBaseResponse.managerList, R.layout.rv_item) {
                                    @Override
                                    protected void convert(BaseViewHolder helper, Manager item) {
                                        helper.setText(R.id.tv_item_text, item.UserName);
                                    }
                                };
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        });

            }
        });
    }
}
