package com.github.ws.retrofitview.rest;

import com.github.ws.retrofitview.serviece.RestService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 5/17 0017.
 */
public class RestClient {

    private Retrofit mRetrofit;

    private static final String BASE_URL = "http://plus31.366ec.net/";
    private RestService mService;

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    public RestClient() {
        initRestClint(BASE_URL);
    }


    public RestClient(String baseUrl) {
        initRestClint(baseUrl);
    }

    public void initRestClint(String baseUrl) {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient.build())
                .build();

        mService = mRetrofit.create(RestService.class);
    }


    public RestService getRectService() {
        if (mService != null) {
            return mService;
        }
        return null;
    }

}
