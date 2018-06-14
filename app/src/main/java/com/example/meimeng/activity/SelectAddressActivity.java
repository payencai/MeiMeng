package com.example.meimeng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;

import butterknife.OnClick;

public class SelectAddressActivity extends BaseActivity implements View.OnClickListener {
    TextView save;
    TextView title;
    EditText et_input_address;
    LinearLayout select_address;


    @Override
    protected void initView() {
        select_address = findViewById(R.id.select_address_layout);
        save = findViewById(R.id.saveText);
        title = findViewById(R.id.title);
        et_input_address = findViewById(R.id.et_input_address);
        title.setText("选择地址");
        save.setVisibility(View.VISIBLE);

        findViewById(R.id.back).setOnClickListener(this);
        select_address.setOnClickListener(this);
        save.setOnClickListener(this);

    }

    @Override
    protected int getContentId() {
        return R.layout.show_select_adress;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.select_address_layout:
                startActivity(new Intent(this, AddressSelectionActivity.class));
                break;
            case R.id.saveText:
                String address = et_input_address.getEditableText().toString() + "";
                Bundle bundle = new Bundle();
                bundle.putString("address", address);
                setResult(RESULT_OK, new Intent().putExtras(bundle));
                finish();
                break;
        }
    }
}
