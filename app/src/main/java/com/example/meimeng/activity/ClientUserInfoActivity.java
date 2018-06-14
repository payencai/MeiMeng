package com.example.meimeng.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientUserInfoActivity extends BaseActivity {


    //定义一个String类型的List数组作为数据源
    private List<String> dataList;

    //定义一个ArrayAdapter适配器作为spinner的数据适配器
    private ArrayAdapter<String> adapter;

    @BindView(R.id.select_blood)
    Spinner select_blood;
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
                startActivityForResult(new Intent(ClientUserInfoActivity.this,SelectAddressActivity.class),0);
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
        if(requestCode==0){
            if(data!=null) {
                et_address.setText(data.getExtras().getString("address") + "");
                et_address.setTextColor(ContextCompat.getColor(this, R.color.text_333));
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
    private String bloodtype="";
    private String type=bloodtype;
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
        //params.put("sex",sex);
        params.put("age",age);
        params.put("bloodType",type);
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
                        initSpinner();
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

    public void initSpinner(){
        dataList = new ArrayList<String>();
        Log.e("blood",bloodtype);
        if(bloodtype.equals("B")){
            dataList.add("B");
            dataList.add("A");
            dataList.add("AB");
            dataList.add("O");
            dataList.add("未知");
        }
        else if (bloodtype.equals("AB")) {
            dataList.add("AB");
            dataList.add("A");
            dataList.add("B");
            dataList.add("O");
            dataList.add("未知");
        }else if (bloodtype.equals("A")) {
            dataList.add("A");
            dataList.add("B");
            dataList.add("AB");
            dataList.add("O");
            dataList.add("未知");
        }
        else if (bloodtype.equals("O")) {
            dataList.add("O");
            dataList.add("A");
            dataList.add("B");
            dataList.add("AB");
            dataList.add("未知");
        }else{
            dataList.add("未知");
            dataList.add("A");
            dataList.add("B");
            dataList.add("AB");
            dataList.add("O");
        }

        adapter = new ArrayAdapter<String>(this,R.layout.item_spinner_blood,dataList);

        //为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //select_blood.setDropDownWidth(260);
        //为spinner绑定我们定义好的数据适配器
        select_blood.setAdapter(adapter);

        //为spinner绑定监听器，这里我们使用匿名内部类的方式实现监听器
        select_blood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 //tv_bloodType = (TextView)view;
                 type=adapter.getItem(position);
                 //Log.e("type",type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });


    }
}
