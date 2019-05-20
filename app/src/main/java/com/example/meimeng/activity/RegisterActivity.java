package com.example.meimeng.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.custom.KyLoadingBuilder;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.TimerCount;
import com.example.meimeng.util.ToaskUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNumberEdit;
    private EditText passwordEdit;
    private EditText verificationEdit;
    private TextView obtainCodeBtn;
    private Button register;
    private RelativeLayout headLayout;
    private int mIntoType = 0;//进入的方式，0为注册，1为修改密码
    private TextView title;
    private KyLoadingBuilder mLoadView;

    /**
     * @param context
     * @param intoType 进入Login的方式
     */
    public static void launchLoginActivity(Context context, int intoType) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra("intoType", intoType);
        context.startActivity(intent);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        userNumberEdit = (EditText) findViewById(R.id.userNumberEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        verificationEdit = (EditText) findViewById(R.id.verificationEdit);
        obtainCodeBtn = (TextView) findViewById(R.id.obtainCodeBtn);
        register = (Button) findViewById(R.id.register);
        headLayout = findViewById(R.id.headLayout);
        Intent intent = getIntent();
        mIntoType = intent.getIntExtra("intoType", 0);
        headLayout.setBackgroundColor(Color.parseColor("#00ffffff"));
        title = (TextView) findViewById(R.id.title);
        if (mIntoType == 0) {
            title.setText("注册");
            register.setText("注册");
        } else {
            title.setText("修改密码");
            register.setText("完成");
        }
        findViewById(R.id.tv_xieyi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, ProxyActivity.class));
            }
        });
        findViewById(R.id.back).setOnClickListener(this);
        obtainCodeBtn.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    private void checkIsRegister(final String tel) {
        Map<String, Object> params = new HashMap<>();
        params.put("telephone", tel);
        HttpProxy.obtain().get(PlatformContans.UseUser.sIsRegister, params, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                obtainCodeBtn.setEnabled(true);
                MLog.log("checkIsRegister", result);
                //{"resultCode":0,"message":"验证码已发送！"}
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    String message = object.getString("message");
                    if (resultCode == 6003) {
                        ToaskUtil.showToast(RegisterActivity.this, message);
                    }
                    if (resultCode == 0) {
                        TimerCount timer = new TimerCount(60000, 1000, obtainCodeBtn);
                        timer.start();

                        getAuthCode(tel, PlatformContans.UseUser.sGetVerificationCode);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String error) {
                obtainCodeBtn.setEnabled(true);
                ToaskUtil.showToast(RegisterActivity.this, "请检查网络");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.obtainCodeBtn:
                String number = userNumberEdit.getEditableText().toString();
                if (checkFormObtainCode(number)) {
                    if (mIntoType == 0)
                        checkIsRegister(number);
                    else {
                        TimerCount timer = new TimerCount(60000, 1000, obtainCodeBtn);
                        timer.start();
                        // obtainCodeBtn.setEnabled(false);
                        getAuthCode(number, PlatformContans.UseUser.sFindCode);

                    }
                }
                break;
            case R.id.register:
                String numberSubmit = userNumberEdit.getEditableText().toString();
                String code = verificationEdit.getEditableText().toString().replace(" ", "");
                String password = passwordEdit.getEditableText().toString().replace(" ", "");
                if (checkForm(numberSubmit, code, password)) {
                    register.setEnabled(false);
                    mLoadView = openLoadView("");
                    if (mIntoType == 0) {
                        registerSubmit(PlatformContans.UseUser.sUseUserRegister, numberSubmit, code, password);
                    } else {
                        resetPwdSubmit(PlatformContans.UseUser.sUpdateUserpwd, numberSubmit, code, password);
                    }
                    break;
                }
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

    private boolean checkForm(String numberSubmit, String auth, String password) {
        if (!checkFormObtainCode(numberSubmit)) {
            return false;
        }
        if (TextUtils.isEmpty(auth)) {
            ToaskUtil.showToast(this, "请输入验证码");
            return false;
        }
        if (auth.length() != 6) {
            ToaskUtil.showToast(this, "请输入完整验证码");
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

    /**
     * 获取验证码
     *
     * @param tel
     */
    private void getAuthCode(String tel, String url) {
        Map<String, Object> params = new HashMap<>();
        params.put("telephone", tel);
        params.put("type", 1);
        HttpProxy.obtain().get(url, params, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                obtainCodeBtn.setEnabled(true);
                MLog.log("getAuthCode", result);
                //{"resultCode":0,"message":"验证码已发送！"}
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    String message = object.getString("message");
                    ToaskUtil.showToast(RegisterActivity.this, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String error) {
                obtainCodeBtn.setEnabled(true);
                ToaskUtil.showToast(RegisterActivity.this, "请检查网络");
            }
        });
    }

    private void resetPwdSubmit(String url, String numberSubmit, String auth, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("account", numberSubmit);
        params.put("password", password);
        params.put("vcode", auth);
        String json = new Gson().toJson(params);
        MLog.log("resetPwdSubmit", json + "");
        HttpProxy.obtain().post(url, "", json, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                if (mLoadView != null) {
                    mLoadView.dismiss();
                }
                register.setEnabled(true);

                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    String message = object.getString("message");
                    ToaskUtil.showToast(RegisterActivity.this, message);
                    if (resultCode == 0) {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                ToaskUtil.showToast(RegisterActivity.this, "请检查网络");
                if (mLoadView != null) {
                    mLoadView.dismiss();
                }
                register.setEnabled(true);
            }
        });
    }

    private void registerSubmit(String url, String numberSubmit, String auth, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("telephone", numberSubmit);
        params.put("password", password);
        params.put("vcode", auth);
        HttpProxy.obtain().get(url, params, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                if (mLoadView != null) {
                    mLoadView.dismiss();
                }
                register.setEnabled(true);
                MLog.log("userRegister", result);
                //{"resultCode":0,"message":"注册成功"}
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    String message = object.getString("message");
                    ToaskUtil.showToast(RegisterActivity.this, message);
                    if (resultCode == 0) {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                ToaskUtil.showToast(RegisterActivity.this, "请检查网络");
                if (mLoadView != null) {
                    mLoadView.dismiss();
                }
                register.setEnabled(true);
            }
        });
    }

}
