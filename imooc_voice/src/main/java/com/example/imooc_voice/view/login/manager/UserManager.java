package com.example.imooc_voice.view.login.manager;


import android.util.Log;

import com.example.imooc_voice.view.login.user.User;

/**
 * 单例管理用户类
 */

public class UserManager {
    private static UserManager instance;
    private User mUser;
    //双检查机制
    public static UserManager getInstance(){
        if(instance==null){
            synchronized (UserManager.class){
                if(instance==null){
                    instance = new UserManager();
                }
            }
        }
        return  instance;
    }

    /**
     * 保存用户信息到内存
     */
    public void saveUser(User user){
        mUser =user;
        saveLocal(user);
    }

    /**
     * 持久化用户信息
     * @param user
     */
    private void saveLocal(User user){

    }

    /**
     * 获取用户信息
     * @return
     */
    public User getUser(){
        //空判断
        //todo List
        return mUser;
    }
    /**
     * 从本地取
     */
    public User getLocal(){
        return null;
    }

    /**
     * 判断是否登录
     * @return
     */
    public boolean hasLogin(){
        return  getUser()!=null;
    }

    public void removeUser(){
        mUser=null;
    }

    /**
     * 数据库删除
     */
    private void removeLocal(){

    }
}
