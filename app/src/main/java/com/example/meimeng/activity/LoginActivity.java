package com.example.meimeng.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.meimeng.http.HttpCallback;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.RandomUtil;
import com.example.meimeng.util.ServerUserInfoSharedPre;
import com.example.meimeng.util.ToaskUtil;
import com.example.meimeng.util.UserInfoSharedPre;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNumberEdit;
    private EditText verificationEdit;
    private TextView loginType;
    private Button submit;
    private Button register;
    private TextView findPassword;
    private TextView title;
    private RelativeLayout headLayout;

    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;

    private int userLoginState = 0;//登录类型，0为用户登录，1为服务登录
    private KyLoadingBuilder mLoginLoadView;
    private AlertDialog mResetLoginAlertDialog;

    /**
     * @param context
     * @param loginType 进入Login的方式
     */
    public static void launchLoginActivity(Context context, int loginType) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("loginType", loginType);
        context.startActivity(intent);
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
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

        userLoginState = LoginSharedUilt.getIntance(this).getLastLoginType();
        updataTypeUI();
        loginType.setOnClickListener(this);
        findPassword.setOnClickListener(this);
        submit.setOnClickListener(this);
        register.setOnClickListener(this);
        getPersimmions();

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*单点登录*/
        Intent intent = getIntent();
        boolean resetLogin = intent.getBooleanExtra("resetLogin", false);
        if (resetLogin) {
            showResetLoginView();
            return;
        }
        /*以下为自动登录代码*/
        String tel;
        String psw;
        String url;
        if (userLoginState == 0) {//用户登录
            UserInfoSharedPre intance = UserInfoSharedPre.getIntance(this);
            url = PlatformContans.UseUser.sLogin;
            tel = (String) intance.getUserInfoFiledValue("account");
            psw = (String) intance.getUserInfoFiledValue("password");
        } else {
            ServerUserInfoSharedPre intance = ServerUserInfoSharedPre.getIntance(this);
            url = PlatformContans.Serveruser.ServerUserLogin;
            tel = (String) intance.getServerUserFiledValue("account");
            psw = (String) intance.getServerUserFiledValue("password");
        }
        if (!TextUtils.isEmpty(tel) && !TextUtils.isEmpty(psw)) {
            mLoginLoadView = openLoadView("");
            requestLogin(url, tel, psw);
        }
    }

    private void showResetLoginView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View loginView = LayoutInflater.from(this).inflate(R.layout.reset_login_layout, null);
        builder.setView(loginView);
        mResetLoginAlertDialog = builder.create();
        mResetLoginAlertDialog.show();
        loginView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResetLoginAlertDialog.dismiss();
            }
        });

    }

    @Override
    protected int getContentId() {
        return R.layout.activity_login;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                //用户账号：13202908144   12345678
                //服务账号：13480197692   123456
                String tel;
                String psw;
                String url;
                if (userLoginState == 0) {
                    url = PlatformContans.UseUser.sLogin;
                } else {
                    url = PlatformContans.Serveruser.ServerUserLogin;
                }
                if (APP.IS_DEBUG) {
                    if (userLoginState == 0) {
                        tel = "13202908144";
                        psw = "12345678";
                    } else {
                        tel = "13202908144";
                        //tel = "17306676089";
                        psw = "12345678";
                    }
                } else {
                    if (userLoginState == 0) {//用户登录
                        UserInfoSharedPre intance = UserInfoSharedPre.getIntance(this);
                        tel = (String) intance.getUserInfoFiledValue("account");
                        psw = (String) intance.getUserInfoFiledValue("password");
                    } else {
                        ServerUserInfoSharedPre intance = ServerUserInfoSharedPre.getIntance(this);
                        tel = (String) intance.getServerUserFiledValue("account");
                        psw = (String) intance.getServerUserFiledValue("password");
                    }

                    tel = userNumberEdit.getEditableText().toString();
                    psw = verificationEdit.getEditableText().toString();
                }
                if (checkForm(tel, psw)) {
                    submit.setEnabled(false);
                    mLoginLoadView = openLoadView("");
                    requestLogin(url, tel, psw);
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
                    register.setVisibility(View.GONE);
                    userLoginState = 1;//处于志愿者登录状态
                } else {
                    title.setText("用户登录");
                    loginType.setText("志愿者登录");
                    register.setVisibility(View.VISIBLE);
                    userLoginState = 0;
                }
//                ToaskUtil.showToast(this, userLoginState + "");
                break;
        }
    }

    private void updataTypeUI() {
        if (userLoginState == 0) {
            title.setText("用户登录");
            loginType.setText("志愿者登录");
            register.setVisibility(View.VISIBLE);
        } else {
            title.setText("志愿者登录");
            loginType.setText("用户登录");//切换成志愿者登录状态
            register.setVisibility(View.GONE);
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        if (requestCode == SDK_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


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
        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        String deviceOnlyId = intance.getDeviceOnlyId();
        if (TextUtils.isEmpty(deviceOnlyId)) {
            Random random = new Random();
            String s = RandomUtil.generateString(random, 12);
            deviceOnlyId = "deviceOnlyId" + s + System.currentTimeMillis();
            intance.saveDeviceOnlyId(deviceOnlyId);
        }
        params.put("equipment", deviceOnlyId);
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
                    Log.e("res",result);
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
                        String address=data.getString("address");
                        if(address.equals("null")||TextUtils.isEmpty(address)){
                            startActivity(new Intent(LoginActivity.this,UserInfoActivity.class));
                            finish();
                        }else{
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }


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
                        ServerUserInfoSharedPre intance = ServerUserInfoSharedPre.getIntance(LoginActivity.this);
                        intance.saveServerUserInfo(userInfo, true);
                        startActivity(new Intent(LoginActivity.this, ServerMainActivity.class));
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
