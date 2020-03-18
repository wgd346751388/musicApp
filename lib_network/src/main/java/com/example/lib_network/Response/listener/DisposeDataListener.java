package com.example.lib_network.Response.listener;

public interface DisposeDataListener {
    /**
     *请求成功回调事件
     */
    void onSuccess(Object responseObj);
    /**
     *请求失败回调事件
     */
    void onFailure(Object reasonObj);
}
