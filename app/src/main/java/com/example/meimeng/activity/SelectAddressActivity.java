package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;

public class SelectAddressActivity extends BaseActivity {
    TextView save;
    TextView title;
    EditText et_input_address;
    LinearLayout select_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        select_address=findViewById(R.id.select_address_layout);
        save=findViewById(R.id.saveText);
        title=findViewById(R.id.title);
        et_input_address=findViewById(R.id.et_input_address);
        title.setText("选择地址");
        save.setVisibility(View.VISIBLE);
        final ImageView back;
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address=et_input_address.getEditableText().toString()+"";
                Bundle bundle=new Bundle();
                bundle.putString("address",address);
                setResult(RESULT_OK,new Intent().putExtras(bundle));
                finish();
            }
        });
        select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected int getContentId() {
        return R.layout.show_select_adress;
    }
}
