package com.example.meimeng.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.activity.ChatActivity;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.manager.ActivityManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.lang.ref.WeakReference;

/**
 * 作者：凌涛 on 2018/7/2 18:02
 * 邮箱：771548229@qq..com
 */
public class HxLoginUtil {
    private int requstLoginCount = 0;
    private static Context mContext;
    private static final int LOGIN_HX = 2;
    public static final int REQUE_LOGINHX_MAX_COUNT = 3;//请求登录环信的最大次数
    private MyHandler mHandler;
    private HxLoginCallBack mCallBack;



    public HxLoginUtil(HxLoginCallBack callBack, Context context) {
        mCallBack = callBack;
        mContext = context.getApplicationContext();
        mHandler = new MyHandler(callBack);
    }

    public void start() {
        loginHx();
    }

    private void loginHx() {
        Log.d("asyncCreateGroup", "loginHx: 登录hx");
        String userName = null;
        String password = null;
        if (APP.sUserType == 0) {
            UserInfo userInfo = APP.getInstance().getUserInfo();
            userName = userInfo.getId();
            password = userInfo.getHxPwd();
        } else {
            ServerUserInfo serverUserInfo = APP.getInstance().getServerUserInfo();
            userName = serverUserInfo.getId();
            password = serverUserInfo.getHxPwd();
        }
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userName)) {
            ToaskUtil.showToast(mContext, "数据获取失败");
            ActivityManager.getInstance().finishAllActivity();
            return;
        }
        EMClient.getInstance().login(userName, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                Log.d("asyncCreateGroup", "onSuccess: MainActivity环信登录成功回调");
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                mCallBack.OnSuccess();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("asyncCreateGroup", "登录聊天服务器失败！");
                mHandler.sendEmptyMessageDelayed(LOGIN_HX, 1000);
            }
        });
    }

    private class MyHandler extends Handler {

        private HxLoginCallBack mCallBack;

        public MyHandler(HxLoginCallBack callBack) {
            mCallBack = callBack;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mCallBack == null) {
                return;
            }
            switch (msg.what) {
                case LOGIN_HX:
                    requstLoginCount++;
                    if (requstLoginCount > REQUE_LOGINHX_MAX_COUNT) {
                        ToaskUtil.showToast(mContext, "无法连接服务器,请检查网络");
                        requstLoginCount = 0;
                        mCallBack.onFailure();
                        return;
                    }
                    loginHx();
                    break;
            }
        }
    }

    public interface HxLoginCallBack {
        void OnSuccess();

        void onFailure();

    }

}
