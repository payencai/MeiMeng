package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;

public class ServerUserInfoActivity extends BaseActivity implements View.OnClickListener{
    TextView title;
    LinearLayout updatename;
    LinearLayout homeaddress;
    LinearLayout workaddress;
    LinearLayout time;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        title=findViewById(R.id.title);
        updatename=findViewById(R.id.server_updatemname);
        workaddress=findViewById(R.id.server_work);
        homeaddress=findViewById(R.id.server_home);
        time=findViewById(R.id.server_time);
        title.setText("个人资料");
        save=findViewById(R.id.server_save);
        updatename.setOnClickListener(this);
        workaddress.setOnClickListener(this);
        homeaddress.setOnClickListener(this);
        save.setOnClickListener(this);
        time.setOnClickListener(this);
        ImageView back;
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected int getContentId() {
        return R.layout.server_userinfo;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.server_home:
                startActivity(new Intent(ServerUserInfoActivity.this,SelectAddressActivity.class));
                break;
            case R.id.server_updatemname:
                startActivity(new Intent(ServerUserInfoActivity.this,UpdateNameActivity.class));
                break;
            case R.id.server_work:
                startActivity(new Intent(ServerUserInfoActivity.this,SelectAddressActivity.class));
                break;
            case R.id.server_time:
                break;
            case R.id.server_save:
                break;
            default:
                break;

        }
    }
}
