package com.example.lib_network.Response;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.lib_network.Response.listener.DisposeDataHandle;
import com.example.lib_network.Response.listener.DisposeDownloadListener;
import com.example.lib_network.exception.OkHttpException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;

import okhttp3.Response;

/**
 * 处理文件类型响应
 */
public class CommonFileCallback extends BaseCommonCallback {


    private static final int PROGRESS_MESSAGE = 0x01;
    private DisposeDownloadListener mListener;
    private String mFilePath;
    private int   mProgress;

    public  CommonFileCallback(DisposeDataHandle handle){
        mListener=(DisposeDownloadListener)handle.mListener;
        this.mFilePath = handle.mSource;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case PROGRESS_MESSAGE:
                        mListener.onProgress((int)msg.obj);
                        break;
                }
            }
        };

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
        final File file = handleResponse(response);
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                if(file !=null){
                    mListener.onSuccess(file);
                }else {
                    mListener.onFailure(new OkHttpException(IO_ERROR,EMPTY_MSG));
                }
            }
        });
    }

    private File handleResponse(Response response) {
        if(response==null){
            return  null;
        }
        InputStream inputStream = null;
        File file ;
        FileOutputStream fos = null;
        byte[] buffer = new byte[2048];
        int length;
        double currentLength = 0;
        double sumLenght = 0;
        try {
            checkLocalFilePath(mFilePath);
            file = new File(mFilePath);
            fos = new FileOutputStream(file);
            inputStream = response.body().byteStream();
            sumLenght = response.body().contentLength();
            while ((length=inputStream.read(buffer) )!= -1){
                fos.write(buffer,0,buffer.length);
                currentLength += length;
                mProgress = (int)(currentLength /sumLenght *100);
                mDeliveryHandler.obtainMessage(PROGRESS_MESSAGE,mProgress);
            }
            fos.flush();

        }catch (Exception e ){
            file = null;

        }finally {
            try {
                if(fos !=null){
                    fos.close();
                }
                if(inputStream !=null){
                    inputStream.close();
                }
            }catch (Exception e ){
                file = null;
            }
        }

        return  file;
    }

    private void checkLocalFilePath(String localFilePath) {
        File path = new File(localFilePath.substring(0,
                localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
