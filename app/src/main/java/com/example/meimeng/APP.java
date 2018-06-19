package com.example.meimeng;

import android.app.Application;
import android.app.Service;
import android.location.Location;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.processor.OkHttpProcessor;
import com.example.meimeng.service.LocationService;

/**
 * 作者：凌涛 on 2018/5/30 1818
 * 邮箱：771548229@qq..com
 */
public class APP extends Application {

    public static final boolean IS_DEBUG = true;//是否是调试
    public LocationService locationService;
    public Vibrator mVibrator;
    private UserInfo mUserInfo;//用户类型
    private ServerUserInfo mServerUserInfo;//志愿者类型
    private static APP sInstance;
    public static int sUserType = 0;//用户类型，0为用户，1为志愿者


    //志愿者测试账号 账号 13480197926  密码 123456
    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public static APP getInstance() {
        return sInstance;
    }

    public ServerUserInfo getServerUserInfo() {
        return mServerUserInfo;
    }

    public void setServerUserInfo(ServerUserInfo serverUserInfo) {
        mServerUserInfo = serverUserInfo;
    }

    @Override

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        // SHA1 c01bdf21e1652917fdf29c91898aec5b80fe44db
        // SHA256 3f2a849d046f5948eeae6e243163b051afc940a500bf6db7b07b20c8e489cbd6
        // SHA256 3f2a849d046f5948eeae6e243163b051afc940a500bf6db7b07b20c8e489cbd6
        //初始化百度地图

        locationService = new LocationService(this);
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        //初始化定位sdk，建议在Application中创建
        SDKInitializer.initialize(this);
        //初始化网络请求
        HttpProxy.init(new OkHttpProcessor());


    }
}
