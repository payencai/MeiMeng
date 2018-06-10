package com.example.meimeng.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;

public class SystemMsgDetailedActivity extends BaseActivity implements View.OnClickListener {

    private TextView textContent;
    private TextView tvTitle;

    public static void startSystemMsgDetailedActivity(Context context, String title, String content) {
        Intent intent = new Intent(context, SystemMsgDetailedActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        textContent = (TextView) findViewById(R.id.textContent);
        findViewById(R.id.back).setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.title);
        Intent intent = getIntent();
        String content = intent.getStringExtra("content");
        String title = intent.getStringExtra("title");

        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            textContent.setText(content);
        }
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_system_msg_detailed;
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
