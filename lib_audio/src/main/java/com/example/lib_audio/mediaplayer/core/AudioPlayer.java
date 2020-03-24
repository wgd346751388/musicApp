package com.example.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.example.lib_audio.app.AudioHelper;
import com.example.lib_audio.mediaplayer.events.AudioCompleteEvent;
import com.example.lib_audio.mediaplayer.events.AudioErrorEvent;
import com.example.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.example.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.example.lib_audio.mediaplayer.events.AudioReleaseEvent;
import com.example.lib_audio.mediaplayer.events.AudioStartEvent;
import com.example.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

/**
 * 1.播放音频
 * 2.对外发送各种类型事件
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, AudioFocusManager.AudioFocusListener {
    private static final String TAG = "AudioPlayer"; //日志
    private static final   int  TIME_MSG = 0x01;
    private static final   int  TIME_INVAL = 100;
    //真正负责音频播放
    private CustomMediaPlayer mediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private AudioFocusManager audioFocusManager;
    private boolean isPauseByFocusLossTransient = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TIME_MSG:
                    break;
            }
        }
    };

    public AudioPlayer(){
        init();
    }
    //初始化
    private void init() {
        mediaPlayer = new CustomMediaPlayer();
        mediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnErrorListener(this);
        mWifiLock = ((WifiManager) AudioHelper.getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE )).createWifiLock(WifiManager.WIFI_MODE_FULL,TAG) ;
        audioFocusManager = new AudioFocusManager(AudioHelper.getContext(),this);
    }

    //内部播放

    private void start(){
        if(!audioFocusManager.requestAudioFocus()){
            Log.e(TAG,"获取音频焦点失败");
        }else {
            mediaPlayer.start();
            mWifiLock.acquire();
            //对外发送start事件
            EventBus.getDefault().post(new AudioStartEvent());
        }
    }
    //设置音量
    private void setVolume(float leftVol, float rightVol) {
        if(mediaPlayer != null) mediaPlayer.setVolume(leftVol,rightVol);
    }


    /**
     * 对外提供的加载事件
     * @param audioBean
     */

    public  void load(AudioBean audioBean){
        try {
            //正常加载逻辑
            //清空数据
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioBean.mUrl);
            mediaPlayer.prepareAsync();
            //对ui外发送load事件
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));
        }catch (Exception e){
            //对ui外发送error事件
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }


    /**
     * 对外提供暂停
     */
    public void pause(){
        if(getStatus() == CustomMediaPlayer.Status.STARTED){
            mediaPlayer.pause();
            //释放音频焦点
            if(mWifiLock.isHeld()){
                mWifiLock.release();
            }
            //释放音频焦点
            if(audioFocusManager != null){
                audioFocusManager.abandonAudioFocus();
            }
            //发送暂停事件
            EventBus.getDefault().post(new AudioPauseEvent());

        }
    }
    /**
     * 对外提供恢复
     */
    public void resume(){
        if(getStatus() == CustomMediaPlayer.Status.PAUSED){
            //直接复用start();
            start();
        }
    }

    /**
     * 清空播放器占用资源
     */
    public void release(){
        if(mediaPlayer ==null){
            return;
        }
        mediaPlayer.release();
        mediaPlayer = null;
        if(audioFocusManager !=null){
            audioFocusManager.abandonAudioFocus();
        }
        if(mWifiLock.isHeld()){
            mWifiLock.release();
        }
        mWifiLock = null;
        audioFocusManager = null;
        //发送释放事件
        EventBus.getDefault().post(new AudioReleaseEvent());
    }



    /**
     * 获取当前播放状态
     * @return
     */
    public CustomMediaPlayer.Status getStatus() {
        if(mediaPlayer != null){
            return mediaPlayer.getStatus();
        }
        return CustomMediaPlayer.Status.STOPPTED;
    }








    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //缓存进度回调


    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放完毕回调
        EventBus.getDefault().post(new AudioCompleteEvent());

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //播放出错
        EventBus.getDefault().post(new AudioErrorEvent());
        return true;//false 会发送Completion
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //准备完毕
        start();
    }

    @Override
    public void audioFocusGrant() {
        //再次获取音频焦点
        setVolume(1.0f,1.0f);
        if(isPauseByFocusLossTransient){
            resume();
        }
        isPauseByFocusLossTransient = false;
    }



    @Override
    public void audioFocusLoss() {
        //永久失去焦点
        pause();
    }

    @Override
    public void audioFocusLossTransient() {
        //短暂性 失去焦点如电话
        pause();
        isPauseByFocusLossTransient = true;
    }

    @Override
    public void audioFocusLossDuck() {
        //瞬间失去焦点
        setVolume(0.5f,0.5f);
    }
}
