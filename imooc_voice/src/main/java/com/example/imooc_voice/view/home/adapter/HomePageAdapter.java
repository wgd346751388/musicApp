package com.example.imooc_voice.view.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.imooc_voice.view.discory.DiscoryFragment;
import com.example.imooc_voice.view.friend.FriendFragment;
import com.example.imooc_voice.view.home.model.CHANNEL;
import com.example.imooc_voice.view.mine.MineFragment;

public class HomePageAdapter extends FragmentPagerAdapter {
    private CHANNEL[] mList;
    public HomePageAdapter(FragmentManager fm,CHANNEL[] datas) {
        super(fm);
        mList=datas;
    }


    @Override
    public Fragment getItem(int i) {
        int type = mList[i].getValue();
        switch (type){
           case CHANNEL.MINE_ID:
               return MineFragment.newInstance();
            case CHANNEL.FRIEND_ID:
                return FriendFragment.newInstance();
            case CHANNEL.DISCORY_ID:
                return DiscoryFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mList.length;
    }
}
