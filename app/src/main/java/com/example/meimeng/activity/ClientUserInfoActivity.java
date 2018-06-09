package com.example.meimeng.activity;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientUserInfoActivity extends BaseActivity {
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
    @BindView(R.id.et_userinfo_address)
    EditText et_address;
    @BindView(R.id.et_userinfo_age)
    EditText et_age;
    @BindView(R.id.tv_userinfo_bloodType)
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
        rb_nv.setChecked(true);
        String sex=APP.getInstance().getUserInfo().getSex();
        Log.e("sex",sex);
        if(sex.equals("男")){
            rb_man.setChecked(true);
        }
        et_name.setText(userInfo.getName()+"");
        et_phone.setText(userInfo.getTelephone()+"");
        et_fixphone.setText(userInfo.getFixedLineTelephone()+"");
        et_address.setText(""+userInfo.getAddress());
        et_age.setText(userInfo.getAge()+"");
        tv_bloodType.setText(userInfo.getBloodType()+"");
        et_sicken.setText(userInfo.getSickenHistory()+"");
        et_othersicken.setText(userInfo.getOtherSicken()+"");
        et_lianxi1.setText(userInfo.getLinkman1()+"");
        et_lianxi2.setText(userInfo.getLinkman2()+"");
        et_lianxi3.setText(userInfo.getLinkman3()+"");

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data=returnJsonString();
                Log.e("data",data);
                String token=userInfo.getToken();
                Log.d("caihuaqing", ":" + token);
                updateUserInfo(data,token);
            }
        });
    }
    private void updateUserInfo(String data,String token){
        Log.d("caihuaqing", ": 进入1");
        HttpProxy.obtain().post(PlatformContans.UseUser.sUpdateUseUser, token, data, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.d("caihuaqing", ": 进入2");
                Log.d("caihuaqing", "" + result);
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
                Log.d("caihuaqing", ": 失败");
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
        String address=et_address.getEditableText().toString();
        String sex="女";
        if(rb_man.isChecked()){
             sex="男";
        }
        String bloodType=tv_bloodType.getText().toString();
        String sickenHistory =et_sicken.getEditableText().toString();
        String otherSicken=et_othersicken.getEditableText().toString();
        String linkman1 =et_lianxi1.getEditableText().toString();
        String linkman2 =et_lianxi2.getEditableText().toString();
        String linkman3 =et_lianxi3.getEditableText().toString();
        String geohash=userInfo.getGeohash();
        String image=userInfo.getImage();
        int age=Integer.parseInt(et_age.getEditableText().toString());
        int accountType=userInfo.getAccountType();
        int isCancel =userInfo.getIsCancel();
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
            latitude="";
        }
        if(longitude==null){
            longitude="";
        }
        if(geohash==null){
            geohash="";
        }
        params.put("accountType",accountType);
        params.put("isCancel",isCancel);
        params.put("address",address);
        params.put("age",age);
        params.put("province",province);
        params.put("area",area);
        params.put("bloodType",bloodType);
        params.put("city",city);
        params.put("fixedLineTelephone",fixedLineTelephone);
        params.put("geohash",geohash);
        params.put("image","");
        params.put("latitude",latitude);
        params.put("linkman1",linkman1);
        params.put("linkman2",linkman2);
        params.put("linkman3",linkman3);
        params.put("longitude",longitude);
        params.put("nickname",nickname);
        params.put("otherSicken",otherSicken);
        params.put("sickenHistory",sickenHistory);
        params.put("telephone",telephone);
        params.put("sex",sex);
        return gson.toJson(params);
    }
}
