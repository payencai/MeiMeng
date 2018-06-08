package com.example.meimeng.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.custom.KyLoadingBuilder;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;
import com.example.meimeng.util.UserInfoSharedPre;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNumberEdit;
    private EditText verificationEdit;
    private TextView loginType;
    private Button submit;
    private Button register;
    private TextView findPassword;
    private TextView title;
    private RelativeLayout headLayout;

    private int userLoginState = 0;//登录类型，0为用户登录，1为服务登录
    private KyLoadingBuilder mLoginLoadView;

    /**
     * @param context
     * @param loginType 进入Login的方式
     */
    public static void launchLoginActivity(Context context, int loginType) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("loginType", loginType);
        context.startActivity(intent);
    }


    @Override
    protected void initView() {
        userNumberEdit = (EditText) findViewById(R.id.userNumberEdit);
        verificationEdit = (EditText) findViewById(R.id.verificationEdit);
        findPassword = (TextView) findViewById(R.id.findPassword);
        loginType = (TextView) findViewById(R.id.loginType);
        submit = (Button) findViewById(R.id.submit);
        register = (Button) findViewById(R.id.register);
        headLayout = findViewById(R.id.headLayout);
        findViewById(R.id.back).setVisibility(View.GONE);

        headLayout.setBackgroundColor(Color.parseColor("#00ffffff"));

        title = (TextView) findViewById(R.id.title);
        title.setText("用户登录");

        loginType.setOnClickListener(this);
        findPassword.setOnClickListener(this);
        submit.setOnClickListener(this);
        register.setOnClickListener(this);

    }

    @Override
    protected int getContentId() {
        return R.layout.activity_login;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                if (APP.IS_DEBUG) {
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    String tel = userNumberEdit.getEditableText().toString();
                    String psw = verificationEdit.getEditableText().toString();
                    String url;
                    if (userLoginState == 0) {
                        url = PlatformContans.UseUser.sLogin;
                    } else {
                        url = PlatformContans.Serveruser.ServerUserLogin;
                    }
                    if (checkForm(tel, psw)) {
                        submit.setEnabled(false);
                        mLoginLoadView = openLoadView("");
                        requestLogin(url, tel, psw);
                    }
                }
                break;
            case R.id.register:
                RegisterActivity.launchLoginActivity(this, 0);
                break;
            case R.id.findPassword:
                RegisterActivity.launchLoginActivity(this, 1);
                break;
            case R.id.loginType:
                String replace = loginType.getText().toString().replace(" ", "");
                if (replace.equals("志愿者登录")) {//前一个状态为用户登录状态
                    title.setText("志愿者登录");
                    loginType.setText("用户登录");//切换成志愿者登录状态
                    userLoginState = 1;//处于志愿者登录状态
                    register.setVisibility(View.GONE);
                } else {
                    title.setText("用户登录");
                    loginType.setText("志愿者登录");
                    userLoginState = 0;
                    register.setVisibility(View.VISIBLE);
                }
//                ToaskUtil.showToast(this, userLoginState + "");
                break;
        }
    }

    private boolean checkFormObtainCode(String number) {
        if (TextUtils.isEmpty(number)) {
            ToaskUtil.showToast(this, "请输入号码");
            return false;
        }
        if (number.length() != 11) {
            ToaskUtil.showToast(this, "号码格式错误");
            return false;
        }
        return true;
    }

    private boolean checkForm(String numberSubmit, String password) {
        if (!checkFormObtainCode(numberSubmit)) {
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            ToaskUtil.showToast(this, "请输入密码");
            return false;
        }
        if (password.length() < 6) {
            ToaskUtil.showToast(this, "密码不能少于6位数");
            return false;
        }
        return true;
    }

    private void requestLogin(String url, String tel, final String psw) {
        Map<String, Object> params = new HashMap<>();
        params.put("telephone", tel);
        params.put("password", psw);
        HttpProxy.obtain().get(url, params, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                MLog.log("requestLogin", result);
                submit.setEnabled(true);
                if (mLoginLoadView != null) {
                    mLoginLoadView.dismiss();
                }
                if (userLoginState == 0) {
                    userLogin(result);
                } else {
                    serverLogin(result);
                }

            }

            private void userLogin(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    String message = object.getString("message");
                    if (resultCode == 0) {
                        ToaskUtil.showToast(LoginActivity.this, "登录成功");
                        JSONObject data = object.getJSONObject("data");
                        UserInfo userInfo = new Gson().fromJson(data.toString(), UserInfo.class);
                        userInfo.setPassword(psw);
                        UserInfoSharedPre intance = UserInfoSharedPre.getIntance(LoginActivity.this);
                        intance.saveUserInfo(userInfo, true);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } else {
                        ToaskUtil.showToast(LoginActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void serverLogin(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    String message = object.getString("message");
                    if (resultCode == 0) {
                        ToaskUtil.showToast(LoginActivity.this, "登录成功");
                        JSONObject data = object.getJSONObject("data");
                        ServerUserInfo userInfo = new Gson().fromJson(data.toString(), ServerUserInfo.class);
                        userInfo.setPassword(psw);
                        UserInfoSharedPre intance = UserInfoSharedPre.getIntance(LoginActivity.this);
                        intance.saveServerUserInfo(userInfo, true);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        ToaskUtil.showToast(LoginActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                ToaskUtil.showToast(LoginActivity.this, "请检查网络");
                submit.setEnabled(true);
                if (mLoginLoadView != null) {
                    mLoginLoadView.dismiss();
                }
            }
        });

    }
}
