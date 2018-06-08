package com.example.meimeng.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.fragment.FirstAidFragment;
import com.example.meimeng.fragment.HomeFragment;
import com.example.meimeng.fragment.UsFragment;
import com.example.meimeng.fragment.UserCenterFragment;
import com.example.meimeng.util.ToaskUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

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

    private PopupWindow mSoSPw;
    private PopupWindow mTypeSelectPw;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    private List<Fragment> fragments;
    private FragmentManager fm;
    //当前打电话的号码
    private String curCallTel = "";
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
                Toast.makeText(MainActivity.this, "权限拒绝", Toast.LENGTH_SHORT).show();
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
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
        //显示主页
        resetStateForTagbar(R.id.home);
        hideAllFragment();
        showFragment(0);
    }

    @Override
    protected void initView() {

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
                setBackgroundDrakValue(0.5f);
                showPopupW(v);
                break;
            case R.id.callFirstAidImg:
                ToaskUtil.showToast(this, "打开搜救界面");
                if (mSoSPw != null) {
                    mSoSPw.dismiss();
                }
                startActivity(new Intent(this, com.example.meimeng.activity.WaitSoSActivity.class));
                break;
            case R.id.call120:
//                ToaskUtil.showToast(this, "打120");
                curCallTel = "10086";
                checkPower();
                break;
            case R.id.callEmergencyContact:
                curCallTel = "10010";
                checkPower();
                break;

        }


    }

    private void showPopupW(View view) {

        View shareview = LayoutInflater.from(this).inflate(R.layout.popup_call_help, null);
        handlerView(shareview);
        mSoSPw = new PopupWindow(shareview,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230, getResources().getDisplayMetrics()));
        mSoSPw.setFocusable(true);
        mSoSPw.setBackgroundDrawable(new BitmapDrawable());
        mSoSPw.setOutsideTouchable(true);
        mSoSPw.setAnimationStyle(R.style.CustomPopWindowStyle);
        mSoSPw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundDrakValue(1.0f);
            }
        });
        //第一个参数可以取当前页面的任意一个View
        //第二个参数表示pw从哪一个方向显示出来
        //3、4表示pw的偏移量
        mSoSPw.showAtLocation(view, Gravity.CENTER, 0, 0);

    }

    private void handlerView(View view) {

        view.findViewById(R.id.callFirstAidImg).setOnClickListener(this);
        view.findViewById(R.id.call120).setOnClickListener(this);
        view.findViewById(R.id.callEmergencyContact).setOnClickListener(this);

    }

    private void initLoginView(View shareview) {

    }


}
