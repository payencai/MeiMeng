package com.example.meimeng.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;

/**
 * 作者：凌涛 on 2018/6/19 11:30
 * 邮箱：771548229@qq..com
 */
public class SDKReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
            // key 验证失败，相应处理
        }
    }
}
