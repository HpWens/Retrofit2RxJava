package com.github.ws.retrofitview.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 */
public class BaseResponse<T> {

    @SerializedName("data")
    public List<T> managerList;

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;
}
