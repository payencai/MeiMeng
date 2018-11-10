package com.example.meimeng.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.example.meimeng.util.ToaskUtil;
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
    int type=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        type=getIntent().getIntExtra("type",0);

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
                if(TextUtils.isEmpty(name)){
                    ToaskUtil.showToast(CertActivity.this,"姓名不能为空");
                    return;
                }
                if(TextUtils.isEmpty(number)){
                    ToaskUtil.showToast(CertActivity.this,"号码不能为空");
                    return;
                }
                if (rb_man.isChecked()) {
                    sex = "男";
                }
                summitRenzhen(name, number, sex);

            }
        });
    }
    private void showAskDialog(String value) {
        //isAskServerUser();
        //Log.e("val",val);
        final Dialog dialog = new Dialog(this, R.style.dialog);
        dialog.setCanceledOnTouchOutside(false);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tishi, null);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        //window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        Display display = getWindowManager().getDefaultDisplay();
        //android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = (int) (display.getWidth() * 0.7);

        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        dialog.setContentView(dialogView);
        TextView textView = (TextView) dialog.findViewById(R.id.dialog_value);
        textView.setText(value);
        dialog.findViewById(R.id.dialog_iknow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
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
                    String msg=jsonObject.getString("message");
                    if(code==0){
                        Toast.makeText(CertActivity.this,"认证成功",Toast.LENGTH_LONG).show();
                        if(type==0)
                          finish();
                        else{
                          startActivity(new Intent(CertActivity.this,MainActivity.class));
                          finish();
                        }
                    }
                    if(code==9999){
                        Toast.makeText(CertActivity.this,msg,Toast.LENGTH_LONG).show();
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
                        if(!TextUtils.isEmpty(id)&&!"null".equals(id)){
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
                            showAskDialog("你已经实名认证过了");
                        }else {
                            if("null".equals(id)){
                                et_name.setText("");
                                et_number.setText("");
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
