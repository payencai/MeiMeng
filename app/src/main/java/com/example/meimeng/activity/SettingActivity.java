package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.ServerUserInfoSharedPre;
import com.example.meimeng.util.UserInfoSharedPre;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.client_updatename)
    LinearLayout mLinearLayout;
    @BindView(R.id.title)
    TextView tv_title;
    @BindView(R.id.exit)
    LinearLayout exit;

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        tv_title.setText("设置");
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, UpdateNameActivity.class));
            }
        });
        ImageView back;
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected int getContentId() {
        return R.layout.show_settings_content;
    }

    @OnClick({R.id.tv_logout, R.id.exit})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit:
            case R.id.tv_logout:
                logout();
                break;
        }
    }

    private void logout() {
        UserInfoSharedPre.getIntance(this).clearUserInfo();
        ActivityManager.getInstance().finishAllActivity();
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void initData() {

//        RVBaseAdapter<UserInfo> adapter = new RVBaseAdapter<UserInfo>() {
//            @Override
//            protected void onViewHolderBound(RVBaseViewHolder holder, int position) {
//
//            }
//            @Override
//            protected void onClick(RVBaseViewHolder holder, int position) {
//
//            }
//        };
//        List<UserInfo> list = new ArrayList<>();//
////        adapter.setData();
//        adapter.setData(list);


    }
}
