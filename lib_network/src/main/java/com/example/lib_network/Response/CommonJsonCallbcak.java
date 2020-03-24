package com.example.lib_network.Response;



import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.lib_network.Response.listener.DisposeDataHandle;
import com.example.lib_network.Response.listener.DisposeDataListener;
import com.example.lib_network.exception.OkHttpException;
import com.example.lib_network.utils.ResponseEntityToModule;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *      处理json类型响应
 */

public class CommonJsonCallbcak extends BaseCommonCallback {


    private Class<?> mClass;
    private DisposeDataListener mListener;

    public CommonJsonCallbcak(DisposeDataHandle handle){
        mClass=handle.mClass;
        mListener=handle.mListener;
        mDeliveryHandler=new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR,e));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final  String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e("返回数据",result);
                handleResponse(result);
            }

        
        });
    }

    private void handleResponse(String result) {
        if(result == null ||result.trim().equals("")){
            mListener.onFailure(new OkHttpException(NETWORK_ERROR,EMPTY_MSG));
            return;
        }
        try {
            if(mClass == null){
                mListener.onSuccess(result);
            }else {
                Object obj = ResponseEntityToModule.parseJsonToModule(result,mClass);//转换实体类
                if(obj != null ){
                    mListener.onSuccess(obj);
                }else {
                    mListener.onFailure(new OkHttpException(JSON_ERROR,EMPTY_MSG));
                }
            }
        }catch (Exception e){
            mListener.onFailure(new OkHttpException(JSON_ERROR,EMPTY_MSG));
        }
    }
}
