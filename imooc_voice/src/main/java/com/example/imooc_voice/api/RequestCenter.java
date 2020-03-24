package com.example.imooc_voice.api;

import com.example.imooc_voice.view.login.user.User;
import com.example.lib_network.Response.listener.DisposeDataHandle;
import com.example.lib_network.Response.listener.DisposeDataListener;
import com.example.lib_network.okhttp.CommonOkHttpClient;
import com.example.lib_network.request.CommonRequest;
import com.example.lib_network.request.RequestParams;

public class RequestCenter {
    static  class  HttpConstants{
//        private static final String ROOT_URL = "http://imooc.com/api";
        private static final String ROOT_URL = "http://39.97.122.129";

        /**
         * 首页请求接口
         */
        private static String HOME_RECOMMAND = ROOT_URL + "/module_voice/home_recommand";

        private static String HOME_RECOMMAND_MORE = ROOT_URL + "/module_voice/home_recommand_more";

        private static String HOME_FRIEND = ROOT_URL + "/module_voice/home_friend";

        /**
         * 登陆接口
         */
        public static String LOGIN = ROOT_URL + "/module_voice/login_phone";
    }

    public static void  postRequest(String url, RequestParams params, DisposeDataListener listener,Class<?>clazz){
        CommonOkHttpClient.post(CommonRequest.createPostRequest(url,params),new DisposeDataHandle(listener,clazz));
    }

    public static void getRequest(String url, RequestParams params, DisposeDataListener listener,
                                  Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.
                createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }

    /**
     * 网络请求登录
     */
    public static void login(DisposeDataListener listener){
        RequestParams params = new RequestParams();
        params.put("mb","18734924592");
        params.put("pwd","999999q");
        RequestCenter.getRequest(HttpConstants.LOGIN,params,listener, User.class);
    }
}
