package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;

public class ShengjiActivity extends BaseActivity {
    LinearLayout shengji;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
         shengji=findViewById(R.id.shengji_layout);
         shengji.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 startActivity(new Intent(ShengjiActivity.this,AddGaojiActivity.class));
             }
         });
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
        return R.layout.server_shengji;
    }
}
