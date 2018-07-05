package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.CurrentHelpInfo;

import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.fragment.HelpInfoFragment;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ServerMainActivity extends BaseActivity {
    private static final String TAG = ServerMainActivity.class.getSimpleName();
    @BindView(R.id.sun1)
    ImageView sun1;
    @BindView(R.id.sun2)
    ImageView sun2;
    @BindView(R.id.sun3)
    ImageView sun3;
    @BindView(R.id.sun4)
    ImageView sun4;
    @BindView(R.id.sun5)
    ImageView sun5;
    @BindView(R.id.tv_help_time)
    TextView time;
    @BindView(R.id.tv_help_count)
    TextView count;
    @BindView(R.id.tv_help_distance)
    TextView distance;
    @BindView(R.id.tv_juli_date)
    TextView juli_date;
    @BindView(R.id.tv_or_help)
    TextView or_help;
    @BindView(R.id.iv_main_head)
    CircleImageView iv_head;
    @BindView(R.id.enter_main)
    LinearLayout enter_main;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.saveImg)
    ImageView saveImg;
    @BindView(R.id.getcurrenthelp)
    RecyclerView mRecyclerView;
    @BindView(R.id.base_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    RVBaseAdapter<CurrentHelpInfo> adapter;
    private HelpInfoFragment mHelpInfoFragment;
    private boolean isLoginHx = false;

    private static final int LOGIN_HX = 2;
    public static final int REQUE_LOGINHX_MAX_COUNT = 3;//请求登录环信的最大次数
    private MyHandler mHandler = new MyHandler(this);
    private boolean isCanRequest = false;


    @Override
    protected void initView() {
        ButterKnife.bind(this);
        loginHx();
        ServerUserInfo userInfo = APP.getInstance().getServerUserInfo();
        juli_date.setText("距离升星还有" + userInfo.getLevelMessage() + "日");
        or_help.setText("或救援" + userInfo.getLevelHelp() + "次");
        title.setText("美盟全民救援");
        time.setText(userInfo.getOnlineTime() + "");
        count.setText(userInfo.getHelpNum() + "");
        distance.setText(userInfo.getHelpDistance() + "");
        back.setVisibility(View.GONE);
        saveImg.setImageResource(R.mipmap.ic_common_nav_normal_messag);
        saveImg.setVisibility(View.VISIBLE);
        Glide.with(this).load(APP.getInstance().getServerUserInfo().getImage()).into(iv_head);
        enter_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServerMainActivity.this, ServerCenterActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("image", image);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
        int level = userInfo.getLevel();
        Log.e("level", level + "");
        if (level == 1) {
            sun2.setVisibility(View.GONE);
            sun3.setVisibility(View.GONE);
            sun4.setVisibility(View.GONE);
            sun5.setVisibility(View.GONE);
        } else if (level == 2) {
            sun3.setVisibility(View.GONE);
            sun4.setVisibility(View.GONE);
            sun5.setVisibility(View.GONE);

        } else if (level == 3) {
            sun4.setVisibility(View.GONE);
            sun5.setVisibility(View.GONE);

        } else if (level == 4) {
            sun5.setVisibility(View.GONE);
        } else if (level == 5) {

        }
        adapter = new RVBaseAdapter<CurrentHelpInfo>() {
            @Override
            protected void onViewHolderBound(RVBaseViewHolder holder, int position) {
            }

            @Override
            protected void onClick(final RVBaseViewHolder holder, final int position) {
                holder.getView(R.id.item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isLoginHx) {
                            Context context = holder.getItemView().getContext();
                            CurrentHelpInfo currentHelpInfo = mData.get(position);
                            requestLeaveHelp(context, currentHelpInfo);
                        } else {
                            if (isCanRequest) {
                                isCanRequest = false;
                                loginHx();
                            }
                            ToaskUtil.showToast(holder.getItemView().getContext(), "聊天未登录，请稍等...");
                        }
                    }
                });

            }
        };
        saveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ServerMainActivity.this, HelpMsgActivity.class));
            }
        });
        initRvView();
    }

    private void initRvView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ServerMainActivity.this));
        mRecyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCurrentHelp();
            }
        });
//        mHelpInfoFragment = new HelpInfoFragment();
//        getSupportFragmentManager().beginTransaction().add(R.id.helpInfoContainer, mHelpInfoFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentHelp();
    }

    String image = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            image = data.getExtras().getString("image");
            if (!TextUtils.isEmpty(data.getExtras().getString("image"))) {
                Glide.with(this).load(data.getExtras().getString("image")).into(iv_head);
            }
        }
    }

    @Override
    protected int getContentId() {
        return R.layout.show_server_main;
    }

    public void getCurrentHelp() {
        Map<String, Object> params = new HashMap<>();
        params.put("latitude", APP.getInstance().getServerUserInfo().getHomeLatitude());
        params.put("longitude", APP.getInstance().getServerUserInfo().getHomeLongitude());
        String token = APP.getInstance().getServerUserInfo().getToken();
        HttpProxy.obtain().get(PlatformContans.ForHelp.sGetCurrentHelp, params, token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                MLog.log(TAG, result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("resultCode");
                    if (code == 0) {
                        JSONArray beanlist = jsonObject.getJSONArray("data");
                        List<CurrentHelpInfo> list = new ArrayList<>();
                        Gson gson = new Gson();
                        for (int i = 0; i < beanlist.length(); i++) {
                            JSONObject item = beanlist.getJSONObject(i);
                            CurrentHelpInfo bean = gson.fromJson(item.toString(), CurrentHelpInfo.class);
                            list.add(bean);
//                            CurrentHelpInfo currentHelpInfo = new CurrentHelpInfo();
//                            JSONObject object = (JSONObject) beanlist.get(i);
//                            currentHelpInfo.setImage(object.getString("image"));
//                            currentHelpInfo.setHelpNum(object.getInt("helpNum"));
//                            currentHelpInfo.setDistance(object.getInt("distance"));
//                            currentHelpInfo.setCreateTime(object.getString("createTime"));
//                            currentHelpInfo.setUseUserName(object.getString("useUserName"));
//                            currentHelpInfo.setUserAddress(object.getString("userAddress"));
//                            list.add(currentHelpInfo);
                        }
                        adapter.reset(list);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    if (code == 9999) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                ToaskUtil.showToast(ServerMainActivity.this, "请检查网络");
            }
        });
    }

    private void requestLeaveHelp(final Context context, final CurrentHelpInfo currentHelpInfo) {
        ServerUserInfo serverUserInfo = APP.getInstance().getServerUserInfo();
        String token = serverUserInfo.getToken();
        if (TextUtils.isEmpty(token)) {
            ToaskUtil.showToast(this, "登录异常");
            ActivityManager.getInstance().finishAllActivity();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        String helpId = currentHelpInfo.getId();
        double lon = intance.getLon();
        double lat = intance.getLat();
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("helpId", helpId);
            json.put("latitude", lat);
            json.put("longitude", lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();
        Log.d("ForHelpInfoByGet", "requestLeaveHelp: " + jsonString);
        HttpProxy.obtain().post(PlatformContans.ForHelp.sUpdateForHelpInfoByGet, token, jsonString, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                MLog.log("ForHelpInfoByGet", result);
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    String message = object.getString("message");
                    if (resultCode == 0) {
                        Intent intent = new Intent(context, RescueActivity.class);
                        intent.putExtra("currentHelpInfo", currentHelpInfo);
                        context.startActivity(intent);
                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            ToaskUtil.showToast(ServerMainActivity.this, message);
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
//        ServerUserInfo serverUserInfo = APP.getInstance().getServerUserInfo();
//        userName = serverUserInfo.getId();
//        password = serverUserInfo.getHxPwd();
        Log.d("asyncCreateGroup", "loginHx: id:" + userName);
        Log.d("asyncCreateGroup", "loginHx: psw::" + password);
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
                isLoginHx = true;
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("asyncCreateGroup", "登录聊天服务器失败！" + code + "," + message);
                String replace = message.replace(" ", "");
                if (replace.equals("Userisalreadylogin") && code == 200) {
                    isLoginHx = true;
                    return;
                }
                mHandler.sendEmptyMessageDelayed(LOGIN_HX, 1000);
            }
        });
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
            ServerMainActivity activity = (ServerMainActivity) this.activity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case LOGIN_HX:
                    requstLoginCount++;
                    if (requstLoginCount > REQUE_LOGINHX_MAX_COUNT) {
                        ToaskUtil.showToast(activity, "无法连接服务器,请检查网络");
                        activity.isCanRequest = true;
                        requstLoginCount = 0;
//                        activity.finish();
                        return;
                    }
                    activity.loginHx();
                    break;
            }
        }
    }


}
