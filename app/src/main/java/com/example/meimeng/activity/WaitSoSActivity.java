package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.custom.WaitSOSView;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WaitSoSActivity extends BaseActivity implements View.OnClickListener {

    private TextView cancel;

    private MyHandler mHandler = new MyHandler(this);

    private static final int LOGIN_HX = 1 << 7;
    public static final int REQUE_LOGINHX_MAX_COUNT = 3;//请求登录环信的最大次数
    private int createGroupType = 0;//创建聊天窗口类型
    private WaitSOSView waitview;

    @Override
    protected void initView() {
        cancel = (TextView) findViewById(R.id.cancel);
        waitview = (WaitSOSView) findViewById(R.id.waitview);
        setRotate();
        cancel.setOnClickListener(this);
//        loginHx();
        LoginSharedUilt intance = LoginSharedUilt.getIntance(WaitSoSActivity.this);
        String groupId = intance.getGroupId();
        String helpId = intance.getHelpId();
        if (TextUtils.isEmpty(groupId)) {
            MLog.log("asyncCreateGroup", "initView: 没有GroupId");
            createGroupChat();
        } else {
            MLog.log("asyncCreateGroup", "initView: 有GroupId");
            requestHelpInfo(groupId);
        }
//        if (TextUtils.isEmpty(helpId)) {
//            if (TextUtils.isEmpty(groupId)) {
//                MLog.log("asyncCreateGroup", "initView: 没有GroupId 和 没有HelpId");
//                createGroupChat();
//            } else {
//                MLog.log("asyncCreateGroup", "initView: 有GroupId 和 没有HelpId");
//                requestHelpInfo(groupId);
//            }
//        } else {
//            if (TextUtils.isEmpty(groupId)) {
//                MLog.log("asyncCreateGroup", "initView: 没有GroupId 和 有HelpId");
//                createGroupChat2();
//            } else {
//                MLog.log("asyncCreateGroup", "initView: 有GroupId 和 有HelpId");
//                mHandler.sendEmptyMessage(0);
//            }
//        }

    }

    private void setRotate() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, -359, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(5000);
        //设置旋转重复次数,INFINITE表示旋转次数无限循环
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        //设置重复旋转模式
        rotateAnimation.setRepeatMode(RotateAnimation.RESTART);
        //设置插值器，使动画匀速执行
        rotateAnimation.setInterpolator(new LinearInterpolator());
        waitview.startAnimation(rotateAnimation);

//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
//        waitview.startAnimation(animation);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_wait_so_s;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                if (mHandler.hasMessages(0)) {
                    mHandler.removeMessages(0);
                }
                finish();
                break;
        }
    }

    private static class MyHandler extends Handler {

        private WeakReference<Activity> activity;
        private int requstLoginCount = 0;

        public MyHandler(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            WaitSoSActivity activity = (WaitSoSActivity) this.activity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    activity.startActivity(new Intent(activity, WaitSalvationActivity.class));
                    activity.finish();
                    break;
                case LOGIN_HX:
                    requstLoginCount++;
                    if (requstLoginCount > REQUE_LOGINHX_MAX_COUNT) {
                        ToaskUtil.showToast(activity, "无法连接服务器,请检查网络");
                        requstLoginCount = 0;
                        return;
                    }
                    activity.loginHx(activity.createGroupType);
                    break;
            }
        }
    }

    //连接后台
    private void requestHelpInfo(String groupid) {
        MLog.log("asyncCreateGroup", "requestHelpInfo:  新增救援");
        if (TextUtils.isEmpty(groupid)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToaskUtil.showToast(WaitSoSActivity.this, "groupid is not");
                }
            });
            return;
        }
        String tokenString;
        if (APP.sUserType == 0) {
            tokenString = APP.getInstance().getUserInfo().getToken();
        } else {
            tokenString = APP.getInstance().getServerUserInfo().getToken();
        }
        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        double lat = intance.getLat();
        double lon = intance.getLon();
        String addr = intance.getAddr();
        if (lat == 0 || lon == 0 || TextUtils.isEmpty(addr)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToaskUtil.showToast(WaitSoSActivity.this, "定位未成功");
                    finish();
                }
            });
            return;
        }

        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("groupid", groupid);
            json.put("latitude", lat + "");//维度
            json.put("longitude", lon + "");//经度
            json.put("userAddress", addr);//求救所在地
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = json.toString();
        MLog.log("asyncCreateGroup", "requestHelpInfo: " + jsonString);
        if (TextUtils.isEmpty(tokenString)) {
            return;
        }
        String url = PlatformContans.ForHelp.sAddForHelpInfo;
        MLog.log("asyncCreateGroup", "requestHelpInfo: " + url);
        addHelper(url, tokenString, jsonString);
    }

    public void addHelper(String httpUrl, String token, String json, ICallBack callBack) {
        MLog.log("asyncCreateGroup", "addHelper: " + httpUrl);
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        try {
            // 创建url资源
            URL url = new URL(httpUrl);
            // 建立http连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
//            conn.setRequestProperty("Connection", "Keep-Alive");
            //关闭长连接
            conn.setRequestProperty("Connection", "close");
            conn.setRequestProperty("token", token);
            // 设置文件字符集:
//            conn.setRequestProperty("Charset", "UTF-8");
            //转换为字节数组
            byte[] data = json.getBytes();
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
//            conn.setRequestProperty("contentType", "application/json");
            conn.setRequestProperty("contentType", "application/json;charset=utf-8");
//            conn.setRequestProperty("ContentType", "application/json");
//            conn.setRequestProperty("ContentType", "application/x-www-form-urlencoded;charset=utf-8");
            // 开始连接请求
            conn.connect();
            OutputStream out = conn.getOutputStream();
            // 写入请求的字符串
            out.write(data);
            out.flush();
            out.close();
            // 请求返回的状态
            if (conn.getResponseCode() == 200) {
                // 请求返回的数据
                InputStream in = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                }
                reader.close();
                result = sbf.toString();
                callBack.OnSuccess(result);
            } else {
                callBack.onFailure("失败了");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addHelper(String url, String token, String json) {


        OkHttpClient client = new OkHttpClient();
//        MediaType jsonType = MediaType.parse("application/json;charset=utf-8");
        MediaType jsonType = MediaType.parse("application/json;charset=UTF-8");
        RequestBody body = RequestBody.create(jsonType, json);

//        try {
//            body = RequestBody.create(jsonType, URLEncoder.encode(json, "utf-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            MLog.log("asyncCreateGroup", "addHelper: 解析出错");
//            body = RequestBody.create(jsonType, json);
//        }
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
//                .method("POST", body)
                .addHeader("token", token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MLog.log("asyncCreateGroup", "onFailure: 失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    MLog.log("asyncCreateGroup", "onResponse: " + result);
                    try {
                        JSONObject object = new JSONObject(result);
                        int resultCode = object.getInt("resultCode");
                        String data = object.getString("data");
                        if (!TextUtils.isEmpty(data)) {
                            LoginSharedUilt intance = LoginSharedUilt.getIntance(WaitSoSActivity.this);
                            intance.saveHelpId(data);
                            if (resultCode == 0 || resultCode == 1) {
                                mHandler.sendEmptyMessage(0);
                            }
                        } else {
                            ToaskUtil.showToast(WaitSoSActivity.this, "求救发起失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToaskUtil.showToast(WaitSoSActivity.this, "请求失败");
                }
            }
        });
    }

    private void createGroupChat() {
        String[] allMembers = new String[]{};
        EMGroupOptions option = new EMGroupOptions();
        option.maxUsers = 200;
        option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
        long l = System.currentTimeMillis();
        EMClient.getInstance().groupManager().asyncCreateGroup("" + l, "救援互动群", allMembers, "创建群", option, new EMValueCallBack<EMGroup>() {
            @Override
            public void onSuccess(EMGroup emGroup) {
                MLog.log("asyncCreateGroup", "onSuccess: 创建成功," + Thread.currentThread().getName());
                String groupId = emGroup.getGroupId();
                LoginSharedUilt intance = LoginSharedUilt.getIntance(WaitSoSActivity.this);
                intance.saveGroupId(groupId);
                requestHelpInfo(groupId);
            }

            @Override
            public void onError(int i, final String s) {
                MLog.log("asyncCreateGroup", "onSuccess: 创建失败," + i + "," + Thread.currentThread().getName() + "," + s);
                String tmpe = "Userisnotlogin";
                String tmpe2 = "Serverisnotreachable";
                String replace = s.replace(" ", "");
                MLog.log("asyncCreateGroup", "onError: " + tmpe);
                MLog.log("asyncCreateGroup", "onError: " + replace);
                if (tmpe.equals(replace)) {
                    MLog.log("asyncCreateGroup", "onError: 重新登录");
                    loginHx(0);
                }
                if (replace.equals(tmpe2)) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToaskUtil.showToast(WaitSoSActivity.this, "创建群聊失败，无法连接网络");
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void loginHx(final int tag) {
        createGroupType = tag;
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
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                MLog.log("asyncCreateGroup", "onSuccess: 登录成功");
                if (tag == 0) {
                    createGroupChat();
                } else {
                    createGroupChat2();
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                String temp = "Networkisn'tavaliable";
                MLog.log("asyncCreateGroup", "登录聊天服务器失败！" + code + "," + message);
                if (code == 2 && temp.equals(message)) {
                    ToaskUtil.showToast(WaitSoSActivity.this, "无法访问网络");
                    finish();
                    return;
                }
                mHandler.sendEmptyMessageDelayed(LOGIN_HX, 1000);
            }
        });
    }

    private void createGroupChat2() {
        String[] allMembers = new String[]{};
        EMGroupOptions option = new EMGroupOptions();
        option.maxUsers = 200;
        option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
        long l = System.currentTimeMillis();
        EMClient.getInstance().groupManager().asyncCreateGroup("" + l, "救援互动群", allMembers, "创建群", option, new EMValueCallBack<EMGroup>() {
            @Override
            public void onSuccess(EMGroup emGroup) {
                MLog.log("asyncCreateGroup", "onSuccess: 创建成功," + Thread.currentThread().getName());
                String groupId = emGroup.getGroupId();
                LoginSharedUilt intance = LoginSharedUilt.getIntance(WaitSoSActivity.this);
                intance.saveGroupId(groupId);
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onError(int i, final String s) {
                MLog.log("asyncCreateGroup", "onSuccess: 创建失败," + Thread.currentThread().getName() + "," + s);
                String tmpe = "Userisnotlogin";
                String replace = s.replace(" ", "");
                if (tmpe.equals(replace)) {
                    loginHx(1);
                }
            }
        });
    }

}
