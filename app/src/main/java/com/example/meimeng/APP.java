package com.example.meimeng;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.processor.OkHttpProcessor;
import com.example.meimeng.mywebsocket.ForegroundCallbacks;
import com.example.meimeng.mywebsocket.WsManager;
import com.example.meimeng.service.LocationService;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;

import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


/**
 * 作者：凌涛 on 2018/5/30 1818
 * 邮箱：771548229@qq..com
 */
public class APP extends Application {

    private static final String TAG = "Application";
    public static final boolean IS_DEBUG = false;//是否是调试
    public LocationService locationService;
    public Vibrator mVibrator;
    private UserInfo mUserInfo;//用户类型
    private ServerUserInfo mServerUserInfo;//志愿者类型
    private static APP sInstance;
    public static int sUserType = 0;//用户类型，0为用户，1为志愿者
    public static double lat;
    public static double lon;

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


        initECChat();
//        initAppStatusListener();
        //init demo helper
//        DemoHelper.getInstance().init(applicationContext);
        // 初始化华为 HMS 推送服务
//        HMSPushHelper.getInstance().initHMSAgent(instance);

        //极光推送
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
    }

    //获取processAppName
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    //application中初始化该监听,当应用回到前台的时候尝试重连
    private void initAppStatusListener() {
        ForegroundCallbacks.init(this).addListener(new ForegroundCallbacks.Listener() {
            @Override
            public void onBecameForeground() {
                Log.i(TAG, "onBecameForeground: 应用回到前台调用重连方法");
                WsManager.getInstance().reconnect();
            }

            @Override
            public void onBecameBackground() {

            }
        });
    }


    private void initECChat() {
        //初始话环信环境
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        options.setAutoLogin(true);
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.setAutoTransferMessageAttachments(true);
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true);
        //初始化
        EMClient.getInstance().init(this, options);
        EaseUI.getInstance().init(this, options);

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null ||!processAppName.equalsIgnoreCase(sInstance.getPackageName())) {
            Log.e(TAG, "enter the service process!");
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(false);
    }

}
