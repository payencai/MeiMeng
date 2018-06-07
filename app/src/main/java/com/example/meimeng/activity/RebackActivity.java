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

import java.util.HashMap;
import java.util.Map;

public class RebackActivity extends BaseActivity {
    EditText et_input;
    TextView tv_save;
    TextView tittle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_save=findViewById(R.id.saveText);
        et_input=findViewById(R.id.reback_text);
        tittle=findViewById(R.id.title);
        et_input.setVisibility(View.VISIBLE);
        tittle.setText("意见反馈");
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input=et_input.getEditableText().toString();
                summitMsg(input);
            }
        });
    }

    @Override
    protected void initView() {

    }
    private void summitMsg(String input){
        Map<String,Object> params=new HashMap<>();
        params.put("data",input);
        String token=null;
        if(APP.sUserType==0){
            token=APP.getInstance().getUserInfo().getToken();

        }else{
            token=APP.getInstance().getServerUserInfo().getToken();
        }
        HttpProxy.obtain().post(PlatformContans.UserAdvice.sAddAdvice, token, input, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.e("advice",result);
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
    @Override
    protected int getContentId() {
        return R.layout.show_sugg_content;
    }
}
