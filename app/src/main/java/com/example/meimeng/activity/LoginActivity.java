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
import com.example.meimeng.util.ToaskUtil;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNumberEdit;
    private EditText verificationEdit;
    private TextView obtainCodeBtn;
    private TextView loginType;
    private Button submit;
    private Button register;
    private RelativeLayout headLayout;

    private int userLoginState = 0;//登录类型，0为用户登录，1为服务登录

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
        obtainCodeBtn = (TextView) findViewById(R.id.obtainCodeBtn);
        loginType = (TextView) findViewById(R.id.loginType);
        submit = (Button) findViewById(R.id.submit);
        register = (Button) findViewById(R.id.register);
        headLayout = findViewById(R.id.headLayout);
        findViewById(R.id.back).setVisibility(View.GONE);

        headLayout.setBackgroundColor(Color.parseColor("#00ffffff"));
        ((TextView) findViewById(R.id.title)).setText("登录");

        loginType.setOnClickListener(this);
        obtainCodeBtn.setOnClickListener(this);
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
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.obtainCodeBtn:
                if (checkFormObtainCode()) {
                    ToaskUtil.showToast(this, "获取验证码成功");
                }
                break;
            case R.id.loginType:
                String replace = loginType.getText().toString().replace(" ", "");
                if (replace.equals("服务号登录")) {
                    userLoginState = 1;
                    loginType.setText("用户登录");
                    register.setVisibility(View.VISIBLE);
                } else {
                    userLoginState = 0;
                    loginType.setText("服务号登录");
                    register.setVisibility(View.GONE);
                }
                break;
        }
    }

    private boolean checkFormObtainCode() {
        String number = userNumberEdit.getEditableText().toString();
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

    private boolean checkFormSubmit() {
        if (checkFormObtainCode()) {
            String verEdit = verificationEdit.getEditableText().toString();
            if (TextUtils.isEmpty(verEdit)) {
                ToaskUtil.showToast(this, "请输入验证吗");
                return false;
            }
            return true;
        }
        return false;
    }


}
