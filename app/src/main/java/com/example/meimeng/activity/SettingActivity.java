package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.manager.ActivityManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.title)
    TextView tv_title;
    @Override
    protected void initView() {
        ButterKnife.bind(this);
        tv_title.setText("设置");
    }

    @Override
    protected int getContentId() {
        return R.layout.show_settings_content;
    }
    @OnClick({R.id.tv_logout})
    void onClick(View view){
        switch (view.getId()){
            case R.id.tv_logout:
                logout();
                break;
        }
    }

    private void logout() {
        startActivity(new Intent(this,LoginActivity.class));
        finish();
        ActivityManager.getInstance().finishActivity(MainActivity.class);
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
