package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.util.ToaskUtil;

import butterknife.OnClick;

public class SelectAddressActivity extends BaseActivity implements View.OnClickListener {
    TextView save;
    TextView title;
    EditText et_input_address;
    LinearLayout select_address;
    TextView tv_name;
    private String mTag;
    private String consignSite;
    private int responseCode;
    public static final int REQUEST_CODE_FROM_SELECTADDRESSACTIVITY = 1 << 8;
    private AddressBean mAddress;

    public static void startSelectAddressActivity(Activity activity, String tag, int responseCode, String consignSite) {
        Intent intent = new Intent(activity, SelectAddressActivity.class);
        intent.putExtra("tag", tag);
        intent.putExtra("responseCode", responseCode);
        intent.putExtra("consignSite", consignSite);
        activity.startActivityForResult(intent, responseCode);
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        mTag = intent.getStringExtra("tag");
        consignSite = intent.getStringExtra("consignSite");

        responseCode = intent.getIntExtra("responseCode", -1);
        if (TextUtils.isEmpty(mTag)) {
            ToaskUtil.showToast(this, "tag 为空");
            finish();
            return;
        }
        tv_name=findViewById(R.id.tv_name);
        select_address = findViewById(R.id.select_address_layout);
        save = findViewById(R.id.saveText);
        title = findViewById(R.id.title);
        et_input_address = findViewById(R.id.et_input_address);
        title.setText("选择地址");
        save.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(consignSite)) {
            et_input_address.setText(consignSite);
        }

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
                Intent intent2 = new Intent();
                intent2.putExtra(mTag, mAddress);
                setResult(responseCode, intent2);
                finish();
                break;
            case R.id.select_address_layout:
                AddressSelectionActivity.startAddressSelectionActivity(this, REQUEST_CODE_FROM_SELECTADDRESSACTIVITY);
                break;
            case R.id.saveText:
                if(mAddress==null){
                    Toast.makeText(this,"请选择地址！",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent();
                mAddress.setAddress(et_input_address.getEditableText().toString());
                intent.putExtra(mTag, mAddress);
                setResult(responseCode, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            et_input_address.setEnabled(true);
            if (requestCode == REQUEST_CODE_FROM_SELECTADDRESSACTIVITY) {
                et_input_address.setEnabled(true);
//                String address = data.getStringExtra("address");
                mAddress = (AddressBean) data.getSerializableExtra("address");
                tv_name.setText(mAddress.getName());
                String addressString = mAddress.getAddress();
                if (!TextUtils.isEmpty(addressString)) {
                    et_input_address.setText(addressString);
                }
            }
        }
    }
}
