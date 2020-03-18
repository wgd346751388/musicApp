package com.example.lib_network.Response;

import android.os.Handler;

public class CommonFileCallback {
    protected  final String EMPTY_MSG = "";
    protected final int NETWORK_ERROR = -1;
    protected final int IO_ERROR = -2;

    private static final int PROGRESS_MESSAGE = 0x01;
    private Handler mDeliveryHandler;

}
