package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.util.SpinerPopWindow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoActivity extends BaseActivity {
    EditText etName;
    EditText etPhone;
    EditText etFixPhone;
    TextView etAddress;
    TextView etBlood;
    EditText etAge;
    EditText etSicken;
    EditText etOtherSicken;
    TextView contact1;
    TextView contact2;
    TextView contact3;
    ImageView back;
    TextView tuichu;
    TextView title;
    TextView save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContentId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initView() {
        bloodLayout=findViewById(R.id.ll_blood_layout);
        title=findViewById(R.id.title);
        etName=findViewById(R.id.et_user_name);
        etPhone=findViewById(R.id.et_user_phone);
        etFixPhone=findViewById(R.id.et_user_fixphone);
        etAddress=findViewById(R.id.et_user_address);
        back=findViewById(R.id.back);
        back.setVisibility(View.GONE);
        etAge=findViewById(R.id.et_user_age);
        etBlood=findViewById(R.id.tv_blood);
        etSicken=findViewById(R.id.et_user_sicken);
        etOtherSicken=findViewById(R.id.et_user_othersicken);
        contact1=findViewById(R.id.et_user_lianxi1);
        contact2=findViewById(R.id.et_user_lianxi2);
        contact3=findViewById(R.id.et_user_lianxi3);
        save=findViewById(R.id.btn_user_save);
        tuichu=findViewById(R.id.comeBackText);
        tuichu.setVisibility(View.VISIBLE);
        tuichu.setText("退出");
        title.setText("个人资料");
        tuichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        contact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeContactsActivity.startMakeContactsActivity(UserInfoActivity.this, 1);
            }
        });
        contact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeContactsActivity.startMakeContactsActivity(UserInfoActivity.this, 2);
            }
        });
        contact3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeContactsActivity.startMakeContactsActivity(UserInfoActivity.this, 3);

            }
        });
        initData();
        bloodLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpinerPopWindow.setWidth(bloodLayout.getWidth());
                mSpinerPopWindow.showAsDropDown(bloodLayout);
            }
        });

        mSpinerPopWindow = new SpinerPopWindow<String>(this, list, itemClickListener);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = etAddress.getText().toString();
                String linkman1 = contact1.getText().toString();
                String name = etName.getEditableText().toString();
                String telephone = etPhone.getEditableText().toString();
                String age=etAge.getEditableText().toString();
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(UserInfoActivity.this,"*为必填！",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(address)){
                    Toast.makeText(UserInfoActivity.this,"*为必填！",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(linkman1)){
                    Toast.makeText(UserInfoActivity.this,"*为必填！",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(telephone)){
                    Toast.makeText(UserInfoActivity.this,"*为必填！",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(age)){
                    Toast.makeText(UserInfoActivity.this,"*为必填！",Toast.LENGTH_LONG).show();
                    return;
                }
                String data = returnJsonString();
                String token = APP.getInstance().getUserInfo().getToken();
                updateUserInfo(data, token);
            }
        });
        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectAddressActivity.startSelectAddressActivity(UserInfoActivity.this, "address", 0, etAddress.getText().toString() + "");
            }
        });
    }
    /**
     * popupwindow显示的ListView的item点击事件
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            etBlood.setText(list.get(position));
            // tvValue.setTextSize(14);

        }
    };

    private SpinerPopWindow<String> mSpinerPopWindow;
    LinearLayout bloodLayout;
    private List<String> list;
    /**
     * 初始化数据
     */
    private void initData() {
        list = new ArrayList<String>();
        list.add("A");
        list.add("B");
        list.add("O");
        list.add("AB");
        list.add("未知");


    }
    private AddressBean mAddressBean=null;
    private String returnJsonString(){
        Map<String, Object> params = new HashMap<>();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String name = etName.getEditableText().toString();
        String telephone = etPhone.getEditableText().toString();
        String fixedLineTelephone = etFixPhone.getEditableText().toString();
        String address = etAddress.getText().toString();
        String sickenHistory = etSicken.getEditableText().toString();
        String otherSicken = etOtherSicken.getEditableText().toString();
        String linkman1 = contact1.getText().toString();
        String linkman2 = contact2.getText().toString();
        String linkman3 = contact3.getText().toString();
        int age = Integer.parseInt(etAge.getEditableText().toString());
        if(mAddressBean!=null){
            String area = mAddressBean.getArea();
            String city = mAddressBean.getCity();
            String province = mAddressBean.getProvince();
            String latitude = String.valueOf(mAddressBean.getLat());
            String longitude = String.valueOf(mAddressBean.getLon());
            params.put("province", province);
            params.put("area", area);
            params.put("city", city);
            params.put("address", address);
            params.put("longitude", longitude);
            params.put("latitude", latitude);
        }
        params.put("age", age);
        params.put("bloodType", etBlood.getText().toString());
        params.put("fixedLineTelephone", fixedLineTelephone);
        params.put("linkman1", linkman1);
        params.put("linkman2", linkman2);
        params.put("linkman3", linkman3);
        params.put("nickname", name);
        params.put("otherSicken", otherSicken);
        params.put("sickenHistory", sickenHistory);
        params.put("telephone", telephone);
        return gson.toJson(params);
    }
    private void updateUserInfo(String data,String token ){
        HttpProxy.obtain().post(PlatformContans.UseUser.sUpdateUseUser, token, data, new ICallBack() {
            @Override
            public void OnSuccess(String result) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("resultCode");
                    if (code == 0) {
                        //Toast.makeText(ClientUserInfoActivity.this, "更新成功", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(UserInfoActivity.this,MainActivity.class));
                        finish();
                    }
                    if (code == 9999) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 0) {
                mAddressBean = (AddressBean) data.getSerializableExtra("address");
                if (mAddressBean != null) {
                    String addressStr = mAddressBean.getAddress();
                    if (!TextUtils.isEmpty(addressStr)) {
                        etAddress.setText(addressStr);
                        // et_address.setTextColor(ContextCompat.getColor(this, R.color.text_333));
                    }
                }

            }
            else if (requestCode == 1) {
                String name = data.getStringExtra("name");
                String tel = data.getStringExtra("tel");
                if (TextUtils.isEmpty(name)&&TextUtils.isEmpty(tel)){
                    contact1.setText("");
                    return;
                }
                String showString = name + ": " + tel;
                contact1.setText(showString);
            } else if (requestCode == 2) {
                String name = data.getStringExtra("name");
                String tel = data.getStringExtra("tel");
                if (TextUtils.isEmpty(name)&&TextUtils.isEmpty(tel)){
                    contact2.setText("");
                    return;
                }
                String showString = name + ": " + tel;
                contact2.setText(showString);

            } else if (requestCode == 3) {
                String name = data.getStringExtra("name");
                String tel = data.getStringExtra("tel");
                if (TextUtils.isEmpty(name)&&TextUtils.isEmpty(tel)){
                    contact3.setText("");
                    return;
                }
                String showString = name + ": " + tel;
                contact3.setText(showString);
            }
        }
    }

}
