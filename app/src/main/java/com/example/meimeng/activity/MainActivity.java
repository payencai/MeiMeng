package com.example.meimeng.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.fragment.FirstAidFragment;
import com.example.meimeng.fragment.HomeFragment;
import com.example.meimeng.fragment.UsFragment;
import com.example.meimeng.fragment.UserCenterFragment;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.CustomPopWindow;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.ToaskUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMultiDeviceListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, EMConnectionListener {

    private static final String TAG = "MainActivity";
    private ImageView imgSos;
    private LinearLayout home;
    private LinearLayout firstAid;
    private LinearLayout aboutUs;
    private LinearLayout userCenter;

    private ImageView homeImg;
    private ImageView firstAidImg;
    private ImageView aboutUsImg;
    private ImageView userCenterImg;

    private TextView homeText;
    private TextView firstAidText;
    private TextView aboutUsText;
    private TextView userCenterText;

    private Fragment mHomeFragment;
    private Fragment mFirstAidFragment;
    private Fragment mUsFragment;
    private Fragment mUserCenterFragment;

    //    private PopupWindow mSoSPw;
    private PopupWindow mTypeSelectPw;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    private static final int LOGIN_HX = 2;
    public static final int REQUE_LOGINHX_MAX_COUNT = 3;//请求登录环信的最大次数

    private MyHandler mHandler = new MyHandler(this);

    private List<Fragment> fragments;
    private FragmentManager fm;
    //当前打电话的号码
    private String curCallTel = "";
    private MyMultiDeviceListener myMultiDeviceListener = new MyMultiDeviceListener();
    //当前显示的Fragment

    private void checkPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            callPhone();
        }
    }

    @SuppressLint("MissingPermission")
    public void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + curCallTel);
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone();
            } else {
                // Permission Denied
                Toast.makeText(this, "权限拒绝", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initFragment() {
        fm = getSupportFragmentManager();

        mHomeFragment = new HomeFragment();
        mFirstAidFragment = new FirstAidFragment();
        mUsFragment = new UsFragment();
        mUserCenterFragment = new UserCenterFragment();

        fragments = new ArrayList<>();
        fragments.add(mHomeFragment);
        fragments.add(mFirstAidFragment);
        fragments.add(mUsFragment);
        fragments.add(mUserCenterFragment);

        for (Fragment fragment : fragments) {
            if (!fragment.isAdded())
                fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
        //显示主页
        resetStateForTagbar(R.id.home);
        hideAllFragment();
        showFragment(0);
    }

    @Override
    protected void initView() {
        //请求登录环信
//        loginHx();
        imgSos = (ImageView) findViewById(R.id.imgSos);
        home = (LinearLayout) findViewById(R.id.home);
        firstAid = (LinearLayout) findViewById(R.id.firstAid);
        aboutUs = (LinearLayout) findViewById(R.id.aboutUs);
        userCenter = (LinearLayout) findViewById(R.id.userCenter);

        homeImg = (ImageView) findViewById(R.id.homeImg);
        firstAidImg = (ImageView) findViewById(R.id.firstAidImg);
        aboutUsImg = (ImageView) findViewById(R.id.aboutUsImg);
        userCenterImg = (ImageView) findViewById(R.id.userCenterImg);

        homeText = (TextView) findViewById(R.id.homeText);
        firstAidText = (TextView) findViewById(R.id.firstAidText);
        aboutUsText = (TextView) findViewById(R.id.aboutUsText);
        userCenterText = (TextView) findViewById(R.id.userCenterText);


        imgSos.setOnClickListener(this);

        home.setOnClickListener(this);
        firstAid.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        userCenter.setOnClickListener(this);

        initFragment();
        loginHx();
        setOtherLoginListener();
//        注册监听,监听多设备登录问题
        EMClient.getInstance().addMultiDeviceListener(myMultiDeviceListener);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_main;
    }

    //重置所有文本的选中状态
    public void selected() {
        homeImg.setSelected(false);
        firstAidImg.setSelected(false);
        aboutUsImg.setSelected(false);
        userCenterImg.setSelected(false);

        homeText.setSelected(false);
        firstAidText.setSelected(false);
        aboutUsText.setSelected(false);
        userCenterText.setSelected(false);

    }

    private void resetStateForTagbar(int viewId) {
        selected();
        if (viewId == R.id.home) {
            Log.d("resetStateForTagbar", "resetStateForTagbar: 11111");
            homeImg.setSelected(true);
            homeText.setSelected(true);
            return;
        }
        if (viewId == R.id.firstAid) {
            Log.d("resetStateForTagbar", "resetStateForTagbar: 22222");
            firstAidImg.setSelected(true);
            firstAidText.setSelected(true);
            return;
        }
        if (viewId == R.id.aboutUs) {
            Log.d("resetStateForTagbar", "resetStateForTagbar: 33333");
            aboutUsImg.setSelected(true);
            aboutUsText.setSelected(true);
            return;
        }
        if (viewId == R.id.userCenter) {
            Log.d("resetStateForTagbar", "resetStateForTagbar: 4444");
            userCenterImg.setSelected(true);
            userCenterText.setSelected(true);
            return;
        }
    }

    private void hideAllFragment() {
        for (Fragment fragment : fragments) {
            fm.beginTransaction().hide(fragment).commit();
        }
    }

    private void showFragment(int position) {
        fm.beginTransaction().show(fragments.get(position)).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:
                resetStateForTagbar(R.id.home);
                hideAllFragment();
                showFragment(0);
                break;
            case R.id.firstAid:
                resetStateForTagbar(R.id.firstAid);
                hideAllFragment();
                showFragment(1);
                break;
            case R.id.aboutUs:
                resetStateForTagbar(R.id.aboutUs);
                hideAllFragment();
                showFragment(2);
                break;
            case R.id.userCenter:
                resetStateForTagbar(R.id.userCenter);
                hideAllFragment();
                showFragment(3);
                break;
            case R.id.imgSos:
//                setBackgroundDrakValue(0.8f);
//                setBackgroundDrakValue(1f);
                showPopupW(v);
                break;

        }


    }

    private void showPopupW(View view) {

//        View shareview = LayoutInflater.from(this).inflate(R.layout.popup_call_help, null);
//        handlerView(shareview);
//        mSoSPw = new PopupWindow(shareview,
//                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics()),
//                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230, getResources().getDisplayMetrics()));
//        mSoSPw.setFocusable(true);
//        mSoSPw.setBackgroundDrawable(new BitmapDrawable());
//        mSoSPw.setOutsideTouchable(true);
//        mSoSPw.setAnimationStyle(R.style.CustomPopWindowStyle);
//        mSoSPw.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                setBackgroundDrakValue(1.0f);
//            }
//        });
//        //第一个参数可以取当前页面的任意一个View
//        //第二个参数表示pw从哪一个方向显示出来
//        //3、4表示pw的偏移量
//        mSoSPw.showAtLocation(view, Gravity.CENTER, 0, 0);
        View otherView = LayoutInflater.from(this).inflate(R.layout.popup_call_help, null);
        CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(otherView)
                .sizeByPercentage(this, 0.95f, 0)
                .setOutsideTouchable(true)
                .enableBackgroundDark(true)
                .setAnimationStyle(R.style.CustomPopWindowStyle)
                .setBgDarkAlpha(0.9f)
                .create();
        handlerView(otherView, customPopWindow);
        customPopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

    }

    private void handlerView(View view, final CustomPopWindow customPopWindow) {

//        case R.id.callFirstAidImg:
//
////                if (!TextUtils.isEmpty(groupId)) {
////                } else {
////                    ToaskUtil.showToast(this, "数据加载中...");
////                }
//        break;
//        case R.id.call120:
////                ToaskUtil.showToast(this, "打120");
//
//        break;
//        case R.id.callEmergencyContact:
//
//        break;

        view.findViewById(R.id.callFirstAidImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
                LoginSharedUilt intance = LoginSharedUilt.getIntance(MainActivity.this);
                String groupId = intance.getGroupId();
                startActivity(new Intent(MainActivity.this, WaitSoSActivity.class));
            }
        });
        view.findViewById(R.id.call120).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
                curCallTel = "10086";
                checkPower();
            }
        });
        view.findViewById(R.id.callEmergencyContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
                curCallTel = "10010";
                checkPower();
            }
        });

    }

    private void initLoginView(View shareview) {

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
                LoginSharedUilt intance = LoginSharedUilt.getIntance(MainActivity.this);
                String groupId = intance.getGroupId();
                if (TextUtils.isEmpty(groupId)) {
                    createGroupChat();
                } else {
                    Log.d("onSuccess", "onSuccess: 已有聊天窗口");
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                String replace = message.replace(" ", "");
                if (replace.equals("Userisalreadylogin") && code == 200) {
                    LoginSharedUilt intance = LoginSharedUilt.getIntance(MainActivity.this);
                    String groupId = intance.getGroupId();
                    if (TextUtils.isEmpty(groupId)) {
                        createGroupChat();
                    } else {
                        Log.d("onSuccess", "onSuccess: 已有聊天窗口");
                    }
                    return;
                }
                Log.d("asyncCreateGroup", "登录聊天服务器失败！" + code + "," + message);
                mHandler.sendEmptyMessageDelayed(LOGIN_HX, 1000);
            }
        });
    }

    private void createGroupChat() {
        //创建群组
        //@param groupName 群组名称
        //@param desc 群组简介
        //@param allMembers 群组初始成员，如果只有自己传空数组即可
        //@param reason 邀请成员加入的reason
        //@param option 群组类型选项，可以设置群组最大用户数(默认200)及群组类型@see {@link EMGroupStyle}
        //              option.inviteNeedConfirm表示邀请对方进群是否需要对方同意，默认是需要用户同意才能加群的。
        //              option.extField创建群时可以为群组设定扩展字段，方便个性化订制。
        //@return 创建好的group
        //@throws HyphenateException
        //
        //EMGroupStylePrivateOnlyOwnerInvite——私有群，只有群主可以邀请人；
        //EMGroupStylePrivateMemberCanInvite——私有群，群成员也能邀请人进群；
        //EMGroupStylePublicJoinNeedApproval——公开群，加入此群除了群主邀请，只能通过申请加入此群；
        //EMGroupStylePublicOpenJoin ——公开群，任何人都能加入此群。
        //
        String[] allMembers = new String[]{};
        EMGroupOptions option = new EMGroupOptions();
        option.maxUsers = 200;
        option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
        long l = System.currentTimeMillis();
//        EMGroup group = EMClient.getInstance().groupManager().createGroup("" + l, "救援互动群", allMembers, "创建群", option);
        EMClient.getInstance().groupManager().asyncCreateGroup("" + l, "救援互动群", allMembers, "创建群", option, new EMValueCallBack<EMGroup>() {
            @Override
            public void onSuccess(EMGroup emGroup) {
                Log.d("asyncCreateGroup", "onSuccess: 创建成功," + Thread.currentThread().getName());
                String groupId = emGroup.getGroupId();
                LoginSharedUilt intance = LoginSharedUilt.getIntance(MainActivity.this);
                intance.saveGroupId(groupId);
            }

            @Override
            public void onError(int i, final String s) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToaskUtil.showToast(MainActivity.this, "创建失败," + s);
                    }
                });
                Log.d("asyncCreateGroup", "onSuccess: 创建失败," + Thread.currentThread().getName() + "," + s);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        UserCenterFragment fragment = (UserCenterFragment) fragmentManager.getFragments().get(3);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Log.e("request", 1 + "");
            fragment.cropPhoto(fragment.getPhotoUri());

        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Log.e("request", 2 + "");
            Uri uri = data.getData();
            fragment.cropPhoto(uri);
        }
        if (requestCode == 3 && data != null) {
            File file = new File(fragment.getPhotoOutputUri().getPath());
            if (file.exists()) {
                fragment.upImage(PlatformContans.Image.sUpdateImage, file);
            } else {
                Toast.makeText(this, "找不到照片", Toast.LENGTH_SHORT).show();
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
            MainActivity activity = (MainActivity) this.activity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case LOGIN_HX:
                    requstLoginCount++;
                    if (requstLoginCount > REQUE_LOGINHX_MAX_COUNT) {
                        ToaskUtil.showToast(activity, "无法连接服务器,请检查网络");
                        requstLoginCount = 0;
                        return;
                    }
                    activity.loginHx();
                    break;
            }
        }
    }

    private class MyMultiDeviceListener implements EMMultiDeviceListener {

        @Override
        public void onContactEvent(int i, String s, String s1) {
            Log.d("onContactEvent", "onContactEvent: " + i + ",s=" + s + ",s1=" + s1);
        }

        @Override
        public void onGroupEvent(int i, String s, List<String> list) {
            Log.d("onContactEvent", "onGroupEvent: " + i + ",s=" + s);
            Log.d("onContactEvent", "onGroupEvent: -------------------------");
            for (String s1 : list) {
                Log.d("onContactEvent", "onGroupEvent: " + s1);
            }

        }
    }

    private void setOtherLoginListener() {
        EMClient.getInstance().addConnectionListener(this);
    }

    @Override
    public void onConnected() {
        Log.d("onDisconnected", "onConnected: ");
    }

    @Override
    public void onDisconnected(int i) {
        Log.d("onDisconnected", "onDisconnected: " + i);
    }
}
