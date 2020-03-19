package com.example.lib_network.okhttp;

import com.example.lib_network.Response.CommonFileCallback;
import com.example.lib_network.Response.CommonJsonCallbcak;
import com.example.lib_network.Response.listener.DisposeDataHandle;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommonOkHttpClient {
    private  static  final int TIME_OUT = 30;
    private static OkHttpClient mokHttpClient;
    static {
        OkHttpClient.Builder ok = new OkHttpClient.Builder();
        ok.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        /**
         * 公共请求头
         */
        ok.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("User-Agent","Imooc-Moblie").build();
                return chain.proceed(request);
            }
        });
        ok.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        ok.readTimeout(TIME_OUT,TimeUnit.SECONDS);
        ok.writeTimeout(TIME_OUT,TimeUnit.SECONDS);
        ok.followRedirects(true);
        mokHttpClient = ok.build();
    }

    public static Call get(Request request, DisposeDataHandle handle){
        Call call = mokHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallbcak(handle));
        return  call;
    }

    public static Call post(Request request, DisposeDataHandle handle){
        Call call = mokHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallbcak(handle));
        return  call;
    }
    public static Call downloadFile(Request request, DisposeDataHandle handle){
        Call call =  mokHttpClient.newCall(request);
        call.enqueue(new CommonFileCallback(handle));
        return  call;
    }
}
