package com.example.meimeng.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class RebackActivity extends BaseActivity {
    EditText et_input;
    TextView tv_save;
    TextView tittle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {

        tv_save=findViewById(R.id.saveText);
        et_input=findViewById(R.id.reback_text);
        tittle=findViewById(R.id.title);
        tv_save.setVisibility(View.VISIBLE);
        tittle.setText("意见反馈");

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input=et_input.getEditableText().toString();
                summitMsg(input);
            }
        });
    }
    private void summitMsg(String input){
        Map<String, Object> p = new HashMap<>();
        p.put("content",input );
        String data=null;
        String token=null;
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        if(APP.sUserType==0){
            token=APP.getInstance().getUserInfo().getToken();
            p.put("userType",0);
            data = gson.toJson(p);
        }else{
            token=APP.getInstance().getServerUserInfo().getToken();
            p.put("userType",1);
            data = gson.toJson(p);
        }
        HttpProxy.obtain().post(PlatformContans.UserAdvice.sAddAdvice, token, data, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.e("TAG",result);
            }

            @Override
            public void onFailure(String error) {
                Log.e("TAG",error);
            }
        });
    }
    @Override
    protected int getContentId() {
        return R.layout.show_sugg_content;
    }
}
