package com.example.meimeng.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tv_title;
    @Override
    protected void initView() {
        ButterKnife.bind(this);
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
    }
}
