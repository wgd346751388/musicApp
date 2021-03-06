package com.example.lib_audio.app;

import android.content.Context;


import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.core.MusicService;

import com.example.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.example.lib_audio.mediaplayer.model.AudioBean;

import java.util.ArrayList;


public final class AudioHelper {
    private static Context mContext;
    public static void init(Context context){
        mContext=context;
        GreenDaoHelper.initDatabase();
    }
    public static Context getContext(){
        return mContext;
    }

    //外部启动MusicService方法
    public static void startMusicService(ArrayList<AudioBean> audios) {

        MusicService.startMusicService(audios);
    }

    public static void pauseAudio() {
        AudioController.getInstance().pause();
    }

    public static void resumeAudio() {
        AudioController.getInstance().resume();
    }
}

