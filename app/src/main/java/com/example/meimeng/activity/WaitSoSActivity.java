package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WaitSoSActivity extends BaseActivity implements View.OnClickListener {

    private TextView cancel;

    private MyHandler mHandler = new MyHandler(this);
    private static final int CREATE_GROUP_FAILURE = 1;
    private static final int LOGIN_HX = 2;
    public static final int REQUE_LOGINHX_MAX_COUNT = 3;//请求登录环信的最大次数

    @Override
    protected void initView() {
        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        loginHx();
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
        private int count = 0;

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
                case CREATE_GROUP_FAILURE:
                    break;
                case LOGIN_HX:
                    count++;
                    if (count > REQUE_LOGINHX_MAX_COUNT) {
                        ToaskUtil.showToast(activity, "无法连接服务器,请检查网络");
                        count = 0;
                        return;
                    }
                    activity.loginHx();
                    break;
            }
        }
    }

    //连接后台
    private void requestHelpInfo(String groupid) {
        Log.d("asyncCreateGroup", "requestHelpInfo:  新增救援");
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
            json.put("groupid ", groupid);
            json.put("latitude", lat + "");//维度
            json.put("longitude", lon + "");//经度
            json.put("userAddress", addr);//求救所在地
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = json.toString();
        Log.d("asyncCreateGroup", "requestHelpInfo: " + jsonString);
        if (TextUtils.isEmpty(tokenString)) {
            return;
        }
        addHelper(PlatformContans.ForHelp.sAddForHelpInfo, tokenString, jsonString);
//        addHelper(PlatformContans.ForHelp.sAddForHelpInfo, tokenString, jsonString, new ICallBack() {
//            @Override
//            public void OnSuccess(String result) {
//                Log.d("asyncCreateGroup", "OnSuccess: " + result);
//                try {
//                    JSONObject object = new JSONObject(result);
//                    int resultCode = object.getInt("resultCode");
//                    if (resultCode == 0) {
////                                mHandler.sendEmptyMessage(0);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(String error) {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToaskUtil.showToast(WaitSoSActivity.this, "请检查网络");
//                    }
//                });
//            }
//        });
    }

    public void addHelper(String httpUrl, String token, String json, ICallBack callBack) {
        Log.d("asyncCreateGroup", "addHelper: " + httpUrl);
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
        MediaType jsonType = MediaType.parse("application/json;charset=utf-8");
        RequestBody body = RequestBody.create(jsonType, json);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .addHeader("token", token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("asyncCreateGroup", "onFailure: 失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
//                    Log.d("asyncCreateGroup", "onResponse: " + response.body().string());
                    Log.d("asyncCreateGroup", "onResponse: 请求成功，但是不知道是啥");
                }
            }
        });
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
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                LoginSharedUilt intance = LoginSharedUilt.getIntance(WaitSoSActivity.this);
                String groupId = intance.getGroupId();
                if (TextUtils.isEmpty(groupId)) {
                    createGroupChat();
                } else {
                    requestHelpInfo(groupId);
                }
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
                LoginSharedUilt intance = LoginSharedUilt.getIntance(WaitSoSActivity.this);
                intance.saveGroupId(groupId);
                requestHelpInfo(groupId);
            }

            @Override
            public void onError(int i, final String s) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToaskUtil.showToast(WaitSoSActivity.this, "创建失败," + s);
                    }
                });
                Log.d("asyncCreateGroup", "onSuccess: 创建失败," + Thread.currentThread().getName() + "," + s);
            }
        });
    }

    //添加管理员权限
    private void addGroupAdmin() {
        /**
         * 增加群组管理员，需要owner权限
         * @param groupId
         * @param admin
         * @return
         * @throws HyphenateException
         */
//        EMClient.getInstance().groupManager().addGroupAdmin(String groupId, final String admin);//需异部处理
    }

    //移除管理员权限
    private void removeGroupAdmin() {
        /**
         * 删除群组管理员，需要owner权限
         * @param groupId
         * @param admin
         * @return
         * @throws HyphenateException
         */
//        EMClient.getInstance().groupManager().removeGroupAdmin(String groupId, String admin);//需异部处理
    }

    //群组加人
    private void addMember() {
        //群主加人调用此方法
//        EMClient.getInstance().groupManager().addUsersToGroup(groupId, newmembers);//需异步处理
        //私有群里，如果开放了群成员邀请，群成员邀请调用下面方法
//        EMClient.getInstance().groupManager().inviteUser(groupId, newmembers, null);//需异步处理
    }

    //群组踢人
    private void removeUser() {
        //把username从群组里删除
//        EMClient.getInstance().groupManager().removeUserFromGroup(groupId, username);//需异步处理
    }

    //加入某个群组
    //只能用于加入公开群。
    private void applyGroup() {
        //如果群开群是自由加入的，即group.isMembersOnly()为false，直接join
//        EMClient.getInstance().groupManager().joinGroup(groupid);//需异步处理
//需要申请和验证才能加入的，即group.isMembersOnly()为true，调用下面方法
//        EMClient.getInstance().groupManager().applyJoinToGroup(groupid, "求加入");//需异步处理
    }

    //获取完整的群成员列表
    private void obtionGroupUserInfo() {
        //如果群成员较多，需要多次从服务器获取完成

//        List<String> memberList = new ArrayList<String>();
//        EMCursorResult<String> result = null;
//        final int pageSize = 20;
//        do {
//            result = EMClient.getInstance().groupManager().fetchGroupMembers(groupId,
//                    result != null ? result.getCursor() : "", pageSize);
//            memberList.addAll(result.getData());
//        } while (!TextUtils.isEmpty(result.getCursor()) && result.getData().size() == pageSize);
    }

    //更新群扩展字段
    private void updataChatInfo() {
//        EMClient.getInstance().groupManager().updateGroupExtension(groupId, extension);
    }


}
