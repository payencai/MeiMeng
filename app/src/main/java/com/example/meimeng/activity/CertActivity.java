package com.example.meimeng.activity;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.util.MLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CertActivity extends BaseActivity {
    EditText et_name;
    EditText et_number;
    Button summit;
    RadioGroup rg_sex;
    RadioButton rb_man;
    RadioButton rb_nv;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        String id=APP.getInstance().getUserInfo().getIdNumber();
        title = findViewById(R.id.title);
        et_name = findViewById(R.id.et_realname);
        et_number = findViewById(R.id.et_certnumber);
        rb_man = findViewById(R.id.rb_nan);
        rb_nv = findViewById(R.id.rb_nv);
        rg_sex = findViewById(R.id.rg_sex);
        title.setText("实名认证");

        Drawable drawable= getResources().getDrawable(R.drawable.sex_selector);
        drawable.setBounds(0,0,30,30);//将drawable设置为宽100 高100固定大小
        rb_man.setCompoundDrawables(drawable,null,null,null);
        Drawable drawable2= getResources().getDrawable(R.drawable.sex_selector);
        drawable2.setBounds(0,0,30,30);//将drawable设置为宽100 高100固定大小
        rb_nv.setCompoundDrawables(drawable2,null,null,null);
        rb_nv.setChecked(true);
        ImageView back;
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        summit = findViewById(R.id.btn_renzhen);

        getUserInfo();
        summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sex = "女";
                String name = et_name.getEditableText().toString();
                String number = et_number.getEditableText().toString();
                if (rb_man.isChecked()) {
                    sex = "男";
                }
                summitRenzhen(name, number, sex);

            }
        });
    }

    private void summitRenzhen(String name, String number, String sex) {
        Map<String, String> p = new HashMap<>();
        p.put("idNumber", number);
        p.put("name", name);
        p.put("sex", sex);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String data = gson.toJson(p);
        HttpProxy.obtain().post(PlatformContans.UseUser.sUpdateRealNameAuthentication, APP.getInstance().getUserInfo().getToken(), data, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                try {
                   // Log.e("post",result);
                    JSONObject jsonObject=new JSONObject(result);
                    int code=jsonObject.getInt("resultCode");
                    if(code==0){
                        Toast.makeText(CertActivity.this,"认证成功",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    if(code==9999){

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MLog.log("sAuth", result);
            }

            @Override
            public void onFailure(String error) {
                MLog.log("sAuth", "error");
            }
        });
    }

    private void getUserInfo(){
        HttpProxy.obtain().get(PlatformContans.UseUser.sGetUseUser, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.e("result",result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("resultCode");
                    if (code == 0) {
                        JSONObject object = jsonObject.getJSONObject("data");
                         String id=object.getString("idNumber");
                         String name=object.getString("name");
                        if(!TextUtils.isEmpty(id)){
                            summit.setVisibility(View.GONE);
                            et_name.setText(name);
                            et_number.setText(id);
                            et_number.setEnabled(false);
                            et_name.setEnabled(false);
                            String sex=object.getString("sex");
                            if (TextUtils.equals(sex,"男")) {
                                rb_man.setChecked(true);
                                rb_nv.setChecked(false);
                                rb_nv.setEnabled(false);
                            }
                        }



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
    protected int getContentId() {
        return R.layout.show_certification_content;
    }
}
