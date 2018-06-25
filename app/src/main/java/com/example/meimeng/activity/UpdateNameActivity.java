package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import okhttp3.internal.platform.Platform;

public class UpdateNameActivity extends BaseActivity {
    TextView title;
    TextView save;
    EditText et_updatename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        title=findViewById(R.id.title);
        et_updatename=findViewById(R.id.et_update_nick);
        save=findViewById(R.id.saveText);
        save.setVisibility(View.VISIBLE);
        title.setText("修改昵称");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=et_updatename.getEditableText().toString();

                String token=APP.getInstance().getUserInfo().getToken();
                updateNickname(name,token);
                //finish();
//                if(APP.sUserType==0){

//                }else{
//                    token=APP.getInstance().getServerUserInfo().getToken();
//                }

            }
        });
        ImageView back;
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   finish();
            }
        });
    }

    @Override
    protected int getContentId() {
        return R.layout.show_updatename_content;
    }
    public void updateNickname(final String name, String token){
        if(APP.sUserType==0){
            HttpProxy.obtain().post(PlatformContans.UseUser.sUpdateUseUser, token, returnJsonString(name), new ICallBack() {
                @Override
                public void OnSuccess(String result) {
                    JSONObject jsonObject= null;
                    try {
                        jsonObject = new JSONObject(result);
                        int code=jsonObject.getInt("resultCode");
                        if(code==0){
                            Intent intent=new Intent();
                            Bundle bundle=new Bundle();
                            bundle.putString("name",name);
                            intent.putExtras(bundle);
                            setResult(RESULT_OK,intent);
                            Toast.makeText(UpdateNameActivity.this,"修改成功",Toast.LENGTH_LONG).show();
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
        }else{
            HttpProxy.obtain().post(PlatformContans.Serveruser.sUpdateServerUser, token, returnServer(name), new ICallBack() {
                @Override
                public void OnSuccess(String result) {
                    JSONObject jsonObject= null;
                    try {
                        jsonObject = new JSONObject(result);
                        int code=jsonObject.getInt("resultCode");
                        if(code==0){
                            Toast.makeText(UpdateNameActivity.this,"修改成功",Toast.LENGTH_LONG).show();
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


    }
    public String returnJsonString(String name){
        UserInfo userInfo=null;
        Map<String,Object> params=new HashMap<>();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        if (APP.sUserType==0){
             userInfo=APP.getInstance().getUserInfo();
        }
        String address=userInfo.getAddress();
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
        params.put("nickname",name);
        params.put("province",province);
        params.put("area",area);
        params.put("city",city);
        params.put("address",address);
        params.put("longitude",longitude);
        params.put("latitude",latitude);
        return gson.toJson(params);
    }
    private String returnServer(String name){
        Map<String,Object> params=new HashMap<>();
        params.put("nickname",name);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(params);
    }
}
