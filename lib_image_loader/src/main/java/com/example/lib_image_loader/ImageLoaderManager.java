package com.example.lib_image_loader;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.lib_image_loader.image.Utils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ImageLoaderManager {

    private ImageLoaderManager(){

    }
    private static  class SingletonHolder{
        private  static ImageLoaderManager instance = new ImageLoaderManager();
    }
    public static ImageLoaderManager getInstance(){
        return  SingletonHolder.instance;
    }

    public void displayImageForView(ImageView imageView,String  url){
        Glide.with(imageView.getContext())
                .asBitmap()
                .apply(initCommonRequestOption())
                .load(url)
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(imageView);
    }

    /**
     * imageView圆形图片
     * @param imageView
     * @param url
     */
    public void displayImageForCircle(final ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new BitmapImageViewTarget(imageView){
                    //将imageView包装成Target
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(
                                imageView.getResources(),resource);
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }

    /**
     * 完成为viewgruop设置背景并模糊处理
     * @param group
     * @param url
     */
    public void displayImageForViewGroup(final ViewGroup group,String url){
        displayImageForViewGroup(group,url,true);
    }
    public void displayImageForViewGroup(final ViewGroup group,String url,final boolean doBlur){
        Glide.with(group.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new SimpleTarget<Bitmap>(){
                    @Override
                    public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        Observable.just(resource).map(new Function<Bitmap,Drawable>() {
                            @Override
                            public Drawable apply(Bitmap bitmap)  {
                                Drawable drawable = new BitmapDrawable(null, doBlur?Utils
                                        .doBlur(resource,100
                                                ,true):resource);
                                return drawable;
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Drawable>() {
                            @Override
                            public void accept(Drawable drawable) throws Exception {
                                group.setBackground(drawable);
                            }
                        });

                    }
                });
    }

    /**
     * 为notification中的id控件加图片
     * @param context
     * @param rv
     * @param id
     * @param notification
     * @param NOTIFICATION_ID
     * @param url
     */
    public void displayImageForNotification(Context context, RemoteViews rv,
                                            int id, Notification notification,
                                            int NOTIFICATION_ID,String url){
        this.displayImageForTarget(context, initNotificationTarget(context,rv,id,notification,NOTIFICATION_ID),url);
    }
    //NotificationTarget
    private NotificationTarget initNotificationTarget(Context context, RemoteViews rv,
                                                      int id, Notification notification,
                                                      int NOTIFICATION_ID){
        return new NotificationTarget(context,id,rv,notification,NOTIFICATION_ID);
    }
    private void displayImageForTarget(Context context, Target target,String url){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .fitCenter()
                .into(target);
    }
    private RequestOptions initCommonRequestOption(){
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.b4y)
                .error(R.mipmap.b4y)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .priority(Priority.NORMAL);

        return options;
    }

}
