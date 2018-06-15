package com.example.meimeng.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientUserInfoActivity extends BaseActivity {

    private SpinerPopWindow<String> mSpinerPopWindow;
    private List<String> list;
    @BindView(R.id.tv_value)
     TextView tvValue;
    @BindView(R.id.blood_layout)
     LinearLayout bloodLayout;

    //定义一个String类型的List数组作为数据源
    private List<String> dataList;

    //定义一个ArrayAdapter适配器作为spinner的数据适配器
    private ArrayAdapter<String> adapter;

    @BindView(R.id.layout_address)
    LinearLayout address;
    @BindView(R.id.title)
    TextView tv_title;
    @BindView(R.id.btn_userinfo_save)
    Button mBtnSave;
    @BindView(R.id.et_userinfo_name)
    EditText et_name;
    @BindView(R.id.et_userinfo_phone)
    EditText et_phone;
    @BindView(R.id.et_userinfo_fixphone)
    EditText et_fixphone;
    @BindView(R.id.et_user_address)
    TextView et_address;
    @BindView(R.id.et_userinfo_age)
    EditText et_age;

   TextView tv_bloodType;
    @BindView(R.id.et_userinfo_sicken)
    EditText et_sicken;
    @BindView(R.id.et_userinfo_othersicken)
    EditText et_othersicken;
    @BindView(R.id.et_userinfo_lianxi2)
    EditText et_lianxi2;
    @BindView(R.id.et_userinfo_lianxi3)
    EditText et_lianxi3;
    @BindView(R.id.et_userinfo_lianxi1)
    EditText et_lianxi1;
    @BindView(R.id.rb_userinfo_man)
    RadioButton rb_man;
    @BindView(R.id.rb_userinfo_nv)
    RadioButton rb_nv;
    @BindView(R.id.rg_userinfo_sex)
    RadioGroup rg_sex;
    private UserInfo userInfo;
    @Override
    protected void initView() {

        ButterKnife.bind(this);

        userInfo=APP.getInstance().getUserInfo();
        tv_title.setText("个人资料");
        Drawable drawable= getResources().getDrawable(R.drawable.sex_selector);
        drawable.setBounds(0,0,30,30);//将drawable设置为宽100 高100固定大小
        rb_man.setCompoundDrawables(drawable,null,null,null);
        Drawable drawable2= getResources().getDrawable(R.drawable.sex_selector);
        drawable2.setBounds(0,0,30,30);//将drawable设置为宽100 高100固定大小
        rb_nv.setCompoundDrawables(drawable2,null,null,null);
        //rb_nv.setChecked(true);
        setValue();


        initData();

        bloodLayout.setOnClickListener(clickListener);
        mSpinerPopWindow = new SpinerPopWindow<String>(this, list,itemClickListener);
       // mSpinerPopWindow.setOnDismissListener(dismissListener);
        ImageView back;
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectAddressActivity.startSelectAddressActivity(ClientUserInfoActivity.this, "address", 0, et_address.getText().toString()+"");
            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data=returnJsonString();
                String token=userInfo.getToken();
                updateUserInfo(data,token);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 0) {
                AddressBean address = (AddressBean) data.getSerializableExtra("address");
                if(address!=null){
                    String addressStr = address.getAddress();
                    if (!TextUtils.isEmpty(addressStr)) {
                        et_address.setText(addressStr);
                        et_address.setTextColor(ContextCompat.getColor(this,R.color.text_333));
                    }
                }

            }
        }
    }

    private void updateUserInfo(String data, String token){
        HttpProxy.obtain().post(PlatformContans.UseUser.sUpdateUseUser, token, data, new ICallBack() {
            @Override
            public void OnSuccess(String result) {

                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(result);
                    int code=jsonObject.getInt("resultCode");
                    if(code==0){
                        Toast.makeText(ClientUserInfoActivity.this,"更新成功",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    if(code==9999){

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
    protected int getContentId() {
        return R.layout.show_usermsg_content;
    }

    public String returnJsonString(){

        Map<String,Object> params=new HashMap<>();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String nickname=et_name.getEditableText().toString();
        String telephone =et_phone.getEditableText().toString();
        String fixedLineTelephone =et_fixphone.getEditableText().toString();
        String address=et_address.getText().toString();
        String sex="女";
        if(rb_man.isChecked()){
             sex="男";
        }

        String sickenHistory =et_sicken.getEditableText().toString();
        String otherSicken=et_othersicken.getEditableText().toString();
        String linkman1 =et_lianxi1.getEditableText().toString();
        String linkman2 =et_lianxi2.getEditableText().toString();
        String linkman3 =et_lianxi3.getEditableText().toString();
        int age=Integer.parseInt(et_age.getEditableText().toString());
        String area=userInfo.getArea();
        String city=userInfo.getCity();
        String province=userInfo.getProvince();
        String latitude=userInfo.getLatitude();
        String longitude=userInfo.getLongitude();
        if(area==null){
            area="";
        }if (city==null){
            city="";
        }
        if(province==null){
            province="";
        }
        if (latitude==null){
            latitude="1";
        }
        if(longitude==null){
            longitude="1";
        }

        params.put("age",age);
        params.put("bloodType",tvValue.getText().toString());
        params.put("fixedLineTelephone",fixedLineTelephone);
        params.put("linkman1",linkman1);
        params.put("linkman2",linkman2);
        params.put("linkman3",linkman3);
        params.put("nickname",nickname);
        params.put("otherSicken",otherSicken);
        params.put("sickenHistory",sickenHistory);
        params.put("telephone",telephone);
        params.put("province",province);
        params.put("area",area);
        params.put("city",city);
        params.put("address",address);
        params.put("longitude",longitude);
        params.put("latitude",latitude);
        return gson.toJson(params);
    }
    private String bloodtype="";
    public void setValue(){
        HttpProxy.obtain().get(PlatformContans.UseUser.sGetUseUser, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                JSONObject object = null;
                try {
                    object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    if (resultCode == 0) {
                        JSONObject data = object.getJSONObject("data");
                        et_name.setText(data.getString("name")+"");
                        et_fixphone.setText(data.getString("fixedLineTelephone"));
                        bloodtype=data.getString("bloodType");
                        tvValue.setText(bloodtype+"");
                        et_address.setText(data.getString("address")+"");
                        et_address.setTextColor(ContextCompat.getColor(ClientUserInfoActivity.this,R.color.text_333));
                        et_phone.setText(data.getString("telephone")+"");
                        et_age.setText(data.getInt("age")+"");
                        String sex=data.getString("sex");
                        rg_sex.setEnabled(false);
                        rb_nv.setEnabled(false);
                        rb_man.setEnabled(false);
                        if(sex.equals("男")){
                            rb_man.setChecked(true);
                        }else{
                            rb_nv.setChecked(false);
                        }
                        et_sicken.setText(data.getString("sickenHistory")+"");
                        et_othersicken.setText(data.getString("otherSicken")+"");
                        et_lianxi1.setText(data.getString("linkman1")+"");
                        et_lianxi2.setText(data.getString("linkman2")+"");
                        et_lianxi3.setText(data.getString("linkman3")+"");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String error) {
              Log.e("user",error);
            }
        });

    }




    /**
     * popupwindow显示的ListView的item点击事件
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            mSpinerPopWindow.dismiss();
            tvValue.setText(list.get(position));

        }
    };

    /**
     * 显示PopupWindow
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.blood_layout:
                    mSpinerPopWindow.setWidth(bloodLayout.getWidth());
                    mSpinerPopWindow.showAsDropDown(tvValue);
                    break;
            }
        }
    };

    /**
     * 初始化数据
     */
    private void initData() {
        list = new ArrayList<String>();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("AB");
        list.add("未知");


    }

    /**
     * 给TextView右边设置图片
     * @param resId
     */
    private void setTextImage(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
        tvValue.setCompoundDrawables(null, null, drawable, null);
    }
}
