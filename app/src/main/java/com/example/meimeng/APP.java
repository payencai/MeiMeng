package com.example.meimeng;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.example.meimeng.service.LocationService;

/**
 * 作者：凌涛 on 2018/5/30 1818
 * 邮箱：771548229@qq..com
 */
public class APP extends Application {

    public static final boolean IS_DEBUG = true;//是否是调试
    public LocationService locationService;
    public Vibrator mVibrator;
    @Override
    public void onCreate() {
        super.onCreate();
        // SHA1 c01bdf21e1652917fdf29c91898aec5b80fe44db
        // SHA256 3f2a849d046f5948eeae6e243163b051afc940a500bf6db7b07b20c8e489cbd6
        //初始化百度地图
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(this);
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
//        SDKInitializer.initialize(this);
    }
}
