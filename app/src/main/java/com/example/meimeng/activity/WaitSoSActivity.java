package com.example.meimeng.activity;

import android.content.Intent;
import android.os.SystemClock;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;

public class WaitSoSActivity extends BaseActivity {

    @Override
    protected void initView() {

    }

    @Override
    protected int getContentId() {
        return R.layout.activity_wait_so_s;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemClock.sleep(500);
        startActivity(new Intent(this, WaitSalvationActivity.class));
        finish();
    }
}
