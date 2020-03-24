package com.example.lib_audio.mediaplayer.core;


import com.example.lib_audio.exception.AudioQueueEmptyException;
import com.example.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.example.lib_audio.mediaplayer.events.AudioFavouriteEvent;
import com.example.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

/**
 * 控制播放逻辑
 */

public class AudioController {


    /**
     * 播放方式
     */
    public enum PlayMode{
        //循环
        LOOP,
        //随机
        RANDOM,
        //单曲循环
        REPEAT
    }

    public static AudioController getInstance(){
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static AudioController instance = new AudioController();
    }

    private AudioPlayer audioPlayer;
    private ArrayList<AudioBean> mQueue;
    private PlayMode playMode;
    private int mQueueIndex;

    private AudioController(){
        audioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
        mQueueIndex=0;
        playMode = PlayMode.LOOP;
    }

    public void changeFavouriteStatus() {
        if(GreenDaoHelper.selectFavourite(getNowPlaying())!= null){
            //取消
            GreenDaoHelper.removeFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(false));
        }else {
            GreenDaoHelper.addFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(true));
        }
    }

    public AudioBean getNowPlaying() {
        return getPlaying();
    }

    private AudioBean getPlaying() {
        if(mQueue != null && !mQueue.isEmpty() && mQueueIndex>=0 && mQueueIndex < mQueue.size()){
            return mQueue.get(mQueueIndex);
        }else {
            throw new AudioQueueEmptyException("当前播放列表为空，请先设置列表");
        }
    }

    private AudioBean getPreviousPlaying() {
        switch (playMode){
            case LOOP:
                mQueueIndex = (mQueueIndex - 1) % mQueue.size() ;

                break;
            case RANDOM:
                mQueueIndex =  new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:

                break;
        }

        return getPlaying();
    }
    private AudioBean getNextPlaying() {
        switch (playMode){
            case LOOP:
                mQueueIndex = (mQueueIndex + 1) % mQueue.size() ;

                break;
            case RANDOM:
                mQueueIndex =  new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:

                break;
        }

        return getPlaying();
    }


    public ArrayList<AudioBean> getQueue(){
        return mQueue == null? new ArrayList<AudioBean>() : mQueue;
    }

    /**
     * 设置播放队列
     * @param queue
     */

    public void setQueue(ArrayList<AudioBean> queue){
        this.setQueue(queue,0);
    }

    public void setQueue(ArrayList<AudioBean> queue,int queueIndex){
        mQueue.addAll(queue);
        mQueueIndex = queueIndex;
    }

    public PlayMode getPlayMode(){
        return  playMode;
    }
    public void setPlayMode(PlayMode playMode){
        this.playMode = playMode;
    }
    public void setPlayIndex(int index){
        if(mQueue == null){
            throw new AudioQueueEmptyException("当前播放列表为空，请先设置列表");
        }
        mQueueIndex = index;
        play();
    }

    public int getPlayIndex(){
        return mQueueIndex;
    }
    public void addAudio(AudioBean bean){
        addAudio(0,bean);
    }

    /**
     * 添加单一歌曲
     * @param index
     * @param bean
     */
    public void addAudio(int index , AudioBean bean){
        if(mQueue == null){
            throw new AudioQueueEmptyException("当前播放列表为空");
        }
        int query = queryAudio(bean);
        if(query <= -1){
            addCustomAudio(index,bean);
            setPlayIndex(index);

        }else {
            AudioBean currentBean = getNextPlaying();
            if(!bean.id.equals(currentBean.id)){
                setPlayIndex(query);
            }
        }

    }

    private void addCustomAudio(int index, AudioBean bean) {
        mQueue.add(index, bean);
    }

    private int queryAudio(AudioBean bean) {

        return mQueue.indexOf(bean);
    }


    /**
     * 对外提供的play方法
     */

    public void play(){
        AudioBean bean = getNowPlaying();
        audioPlayer.load(bean);
    }



    public void pause(){
        audioPlayer.pause();
    }
    public void resume(){
        audioPlayer.resume();
    }
    public void release(){
        audioPlayer.release();
        EventBus.getDefault().unregister(this);

    }

    /**
     * 下一首歌曲
     */
    public void next(){
        AudioBean bean = getNextPlaying() ;
        audioPlayer.load(bean);
    }

    /**
     * 上一首歌曲
     */
    public void previous(){
        AudioBean bean = getPreviousPlaying();
        audioPlayer.load(bean);
    }

    /**
     * 自动判断播放or暂停
     */
    public void playOrPause(){
        if(isStartState()){
            pause();
        }else if(isPauseState()){
            resume();
        }
    }



    private boolean isPauseState() {
        return CustomMediaPlayer.Status.PAUSED == getStatus();
    }


    public boolean isStartState(){
        return CustomMediaPlayer.Status.STARTED == getStatus();
    }

    private CustomMediaPlayer.Status getStatus() {
        return  audioPlayer.getStatus();
    }


}
