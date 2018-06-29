package com.example.meimeng.mywebsocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.meimeng.APP;

/**
 * 作者：凌涛 on 2018/6/29 09:51
 * 邮箱：771548229@qq..com
 * 应用网络的切换.具体点就是可用网络状态的切换,比如4g切wifi连接会断开我们需要重连.
 */
public class NetStatusReceiver extends BroadcastReceiver {
    private static final String TAG = NetStatusReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

            // 获取网络连接管理器
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) APP.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取当前网络状态信息
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();

            if (info != null && info.isAvailable()) {
                Log.i(TAG, "onReceive: 监听到可用网络切换,调用重连方法");
                WsManager.getInstance().reconnect();//wify 4g切换重连websocket
            }
        }
    }
}