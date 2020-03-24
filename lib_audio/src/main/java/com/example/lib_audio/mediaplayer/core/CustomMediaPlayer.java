package com.example.lib_audio.mediaplayer.core;

import android.media.MediaPlayer;
import android.media.MediaPlayer;

import java.io.IOException;

public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {
    public  enum Status{
         IDEL,INITALIZED,STARTED,PAUSED,STOPPTED,COMPLETED;
    }

    private Status mStatus;
    private OnCompletionListener mCompletionListener;

    public CustomMediaPlayer(){
        super();
        mStatus=Status.IDEL;
        super.setOnCompletionListener(this);
    }


    @Override
    public void reset() {
        super.reset();
        mStatus = Status.IDEL;
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        mStatus = Status.INITALIZED;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mStatus = Status.STARTED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mStatus = Status.PAUSED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mStatus = Status.STOPPTED;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mStatus = Status.COMPLETED;

    }
    public Status getStatus(){
        return mStatus;
    }
    public boolean isComplete(){
        return mStatus == Status.COMPLETED;
    }
    public void setCompleteListener(OnCompletionListener listener ){
        mCompletionListener=listener;
    }
}
