package com.github.ws.retrofitview.serviece;

import com.github.ws.retrofitview.model.BaseResponse;
import com.github.ws.retrofitview.model.Manager;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * Created by Administrator on 5/17 0017.
 */

public interface RestService {

    @GET("/Route.axd?method=vast.Store.manager.list")
    Call<ResponseBody> getManagerData(@Query("StoreId") int id);


    @GET("/Route.axd?method=vast.Store.manager.list")
    Call<BaseResponse<Manager>> getManagerDatas(@Query("StoreId") int id);

    @GET("/Route.axd?method=vast.Store.manager.list")
    Call<BaseResponse<Manager>> getManagerDatas(@QueryMap HashMap<String, String> hm);


    @FormUrlEncoded
    @POST("/Route.axd?method=vast.Store.manager.list")
    Call<BaseResponse<Manager>> postManagerDatas(@Field("StoreId") int id);


    @Multipart
    @POST("/UploadProduct.axd")
    Call<ResponseBody> uploadSimpleFile(@Part MultipartBody.Part file);

    @Multipart
    @POST("/UploadProduct.axd")
    Call<ResponseBody> uploadMultiFiles(@PartMap Map<String, RequestBody> params);


    @Streaming
    @GET("/image/h%3D360/sign=86aee1fbf1deb48fe469a7d8c01e3aef/{filename}")
    Call<ResponseBody> downFile(@Path("filename") String fileName);


    @GET("/Route.axd?method=vast.Store.manager.list")
    Observable<BaseResponse<Manager>> getManagers(@Query("StoreId") int id);
}
