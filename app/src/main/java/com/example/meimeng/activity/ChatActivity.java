package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mapapi.utils.CoordinateConverter;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.ToaskUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.baidu.mapapi.model.LatLng;

import java.lang.ref.WeakReference;

import butterknife.BindView;

public class ChatActivity extends BaseActivity {
    protected static final int REQUEST_CODE_MAP = 1;
    private static final int LOGIN_HX = 2;
    public static final int REQUE_LOGINHX_MAX_COUNT = 3;//请求登录环信的最大次数
    private MyHandler mHandler = new MyHandler(this);

    @BindView(R.id.chatContainer)
    RelativeLayout chatContainer;
    private String mUserId;
    private int mRequestCode;
    private EaseChatFragment mChatFragment;

    public static void startChatActivity(Activity activity, String userId, int requestCode) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("requestCode", requestCode);
//        activity.startActivity(intent);
        activity.startActivityForResult(intent, requestCode);

    }

    @Override
    protected void initView() {

//        getSupportFragmentManager().beginTransaction().add(R.id.chatContainer,new )
        //new出EaseChatFragment或其子类的实例


        Intent intent = getIntent();
        mUserId = intent.getStringExtra("userId");
        mRequestCode = intent.getIntExtra("requestCode", 0);
        if (TextUtils.isEmpty(mUserId)) {
            ToaskUtil.showToast(this, "当前群id为空");
            finish();
            return;
        }
        loginHx();

    }

    private void initFragment(String userId) {
        ServerUserInfo userInfo = APP.getInstance().getServerUserInfo();
        String nickname = userInfo.getNickname();
        String image = userInfo.getImage();
        Log.d("initFragment", "initFragment: " + image);
        String imageKey = userInfo.getImageKey();
        mChatFragment = new EaseChatFragment();
        mChatFragment.setLication(mLication);
        mChatFragment.setHeadimg(image);
        mChatFragment.setHeadimgKey(imageKey);
        mChatFragment.setNikename(nickname);
        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
        args.putString(EaseConstant.EXTRA_USER_ID, userId);
        mChatFragment.setArguments(args);
        try {
            getSupportFragmentManager().beginTransaction().add(R.id.chatContainer, mChatFragment).commit();
        } catch (Exception e) {
            finish();
        }

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
            ToaskUtil.showToast(this, "数据获取失败");
            ActivityManager.getInstance().finishAllActivity();
            return;
        }
        EMClient.getInstance().login(userName, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                Log.d("asyncCreateGroup", "onSuccess: MainActivity环信登录成功回调");
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initFragment(mUserId);
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("asyncCreateGroup", "登录聊天服务器失败！");
                String replace = message.replace(" ", "");
                if (replace.equals("Userisalreadylogin") && code == 200) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            initFragment(mUserId);
                        }
                    });
                    return;
                }
                mHandler.sendEmptyMessageDelayed(LOGIN_HX, 1000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(mRequestCode);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_chat;
    }

    private EaseChatFragment.ILaunchLication mLication = new EaseChatFragment.ILaunchLication() {
        @Override
        public void startEaseBaiduMapActivity() {
//            ToaskUtil.showToast(ChatActivity.this, "跳转定位界面");
            startActivityForResult(new Intent(ChatActivity.this, EaseBaiduMapActivity2.class), REQUEST_CODE_MAP);
        }

        @Override
        public void checkMap(double lat, double lon) {
            EaseBaiduMapActivity.onStartEaseBaiduMap(ChatActivity.this, lat, lon);
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_MAP) { // location
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                LatLng point = new LatLng(latitude, longitude);
                CoordinateConverter converter = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.COMMON);
// sourceLatLng待转换坐标
                converter.coord(point);
                point = converter.convert();
                latitude=point.latitude;
                longitude=point.longitude;
//                ToaskUtil.showToast(this, locationAddress + "经度:" + longitude + ",维度:" + latitude);
                if (locationAddress != null && !locationAddress.equals("")) {
                    if (mChatFragment != null) {
                        mChatFragment.sendLocationMessage(latitude, longitude, locationAddress);
                    }
                } else {
                    Toast.makeText(this, com.hyphenate.easeui.R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    private static class MyHandler extends Handler {

        private WeakReference<Activity> activity;
        private int count = 0;
        private int requstLoginCount = 0;

        public MyHandler(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ChatActivity activity = (ChatActivity) this.activity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case LOGIN_HX:
                    requstLoginCount++;
                    if (requstLoginCount > REQUE_LOGINHX_MAX_COUNT) {
                        ToaskUtil.showToast(activity, "无法连接服务器,请检查网络");
                        requstLoginCount = 0;
                        activity.finish();
                        return;
                    }
                    activity.loginHx();
                    break;
            }
        }
    }


}
