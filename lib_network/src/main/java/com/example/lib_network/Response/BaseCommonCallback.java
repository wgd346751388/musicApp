package com.example.lib_network.Response;

import android.os.Handler;

import okhttp3.Callback;


public abstract class BaseCommonCallback  implements Callback {

    protected  final String EMPTY_MSG = "";
    protected final int NETWORK_ERROR = -1;
    protected final int IO_ERROR = -2;
    protected final int JSON_ERROR = -2;
    protected final int OTHER_ERROR = -3;
    protected Handler mDeliveryHandler;



}
