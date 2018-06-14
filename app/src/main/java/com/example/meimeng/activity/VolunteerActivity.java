package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VolunteerActivity extends BaseActivity {
    CardView askVolunteer;
    TextView title;
    CardView askProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {

         askProxy=findViewById(R.id.ask_proxy);
         askVolunteer=findViewById(R.id.ask_volunteer);
         askVolunteer.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 startActivity(new Intent(VolunteerActivity.this,AskVolunteerActivity.class));
             }
         });
         askProxy.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 startActivity(new Intent(VolunteerActivity.this,AskProxyActivity.class));
             }
         });
         title=findViewById(R.id.title);
         title.setText("志愿者招募");
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
        return R.layout.show_volunteer_content;
    }

}
