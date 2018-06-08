package com.example.meimeng.activity;

import android.view.View;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;

public class AddressSelectionActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;

    @Override
    protected void initView() {
        title = (TextView) findViewById(R.id.title);
        title.setText("选择地址");
        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_address_selection;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
