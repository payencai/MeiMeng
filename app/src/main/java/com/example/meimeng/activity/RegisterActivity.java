package com.example.meimeng.activity;

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

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText userNumberEdit;
    private EditText verificationEdit;
    private TextView obtainCodeBtn;
    private Button register;
    private RelativeLayout headLayout;

    @Override
    protected int getContentId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        userNumberEdit = (EditText) findViewById(R.id.userNumberEdit);
        verificationEdit = (EditText) findViewById(R.id.verificationEdit);
        obtainCodeBtn = (TextView) findViewById(R.id.obtainCodeBtn);
        register = (Button) findViewById(R.id.register);
        headLayout = findViewById(R.id.headLayout);

        headLayout.setBackgroundColor(Color.parseColor("#00ffffff"));
        ((TextView) findViewById(R.id.title)).setText("注册");

        findViewById(R.id.back).setOnClickListener(this);
        obtainCodeBtn.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.obtainCodeBtn:
                if (checkFormObtainCode()) {
                    ToaskUtil.showToast(this, "获取验证码成功");
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
}
