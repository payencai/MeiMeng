package com.example.meimeng.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientUserInfoActivity extends BaseActivity {
    @BindView(R.id.title)
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        title.setText("个人资料");
    }

    @Override
    protected int getContentId() {
        return R.layout.show_usermsg_content;
    }
}
