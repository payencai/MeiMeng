package com.example.meimeng.service;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.activity.LoginActivity;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：凌涛 on 2018/7/6 11:24
 * 邮箱：771548229@qq..com
 */
public class LoginInfoService extends Service {

    private static final String TAG = LoginInfoService.class.getSimpleName();

    private static final int REUQEST_DEVICE_INFO_CODE = 1 << 1;


    private LoginInfoHandler mHandler = new LoginInfoHandler(this);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public LoginInfoService getInstance() {
            return LoginInfoService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: 信息创建");
        mHandler.sendEmptyMessage(REUQEST_DEVICE_INFO_CODE);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: 连接");
        return super.onStartCommand(intent, flags, startId);
    }

    private static class LoginInfoHandler extends Handler {

        private WeakReference<Context> mContext;

        public LoginInfoHandler(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            Context context = mContext.get();
            if (context == null) {
                return;
            }
            switch (msg.what) {
                case REUQEST_DEVICE_INFO_CODE:
                    Log.i(TAG, "handleMessage: " + Thread.currentThread().getName());
                    requestDeviceInfo(context);
                    break;
            }

        }

        private void requestDeviceInfo(final Context context) {
            int userType = APP.sUserType;
            String account;
            if (userType == 0) {
                UserInfo userInfo = APP.getInstance().getUserInfo();
                account = userInfo.getAccount();
            } else {
                ServerUserInfo userInfo = APP.getInstance().getServerUserInfo();
                account = userInfo.getAccount();
            }
            Map<String, Object> params = new HashMap<>();
            params.put("telephone", account);
            HttpProxy.obtain().get(PlatformContans.UseUser.sEquipment, params, new ICallBack() {
                @Override
                public void OnSuccess(String result) {
                    MLog.log("sEquipment", result);
                    //{"resultCode":0,"message":null,"data":"deviceOnlyIdrLvG0ZQ9EXMP1530873200892"}
                    try {
                        JSONObject object = new JSONObject(result);
                        int resultCode = object.getInt("resultCode");
                        if (resultCode == 0) {
                            String data = object.getString("data");
                            LoginSharedUilt intance = LoginSharedUilt.getIntance(context);
                            String deviceOnlyId = intance.getDeviceOnlyId();
                            if (!deviceOnlyId.equals(data)) {
                                ActivityManager.getInstance().finishAllActivity();
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.putExtra("resetLogin", true);
                                context.startActivity(intent);
                            } else {
                                sendEmptyMessageDelayed(REUQEST_DEVICE_INFO_CODE, 10 * 1000);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                @Override
                public void onFailure(String error) {
                }
            });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: 读写设备信息服务销毁");
        mHandler.removeCallbacksAndMessages(null);
    }
}
