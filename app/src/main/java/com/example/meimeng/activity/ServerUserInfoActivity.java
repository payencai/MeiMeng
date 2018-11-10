package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.util.TimeSelectPopWindow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServerUserInfoActivity extends BaseActivity implements View.OnClickListener{
    TextView title;
    RelativeLayout updatename;
    RelativeLayout homeaddress;
    RelativeLayout workaddress;
    RelativeLayout time;
    TextView tv_name;
    TextView tv_home;
    TextView tv_work;
    TextView tv_time;
    Button save;
    private String homelon;
    private String homelat;
    private String worklat;
    private String worklon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        title=findViewById(R.id.title);
        updatename=findViewById(R.id.server_updatemname);
        workaddress=findViewById(R.id.server_work);
        homeaddress=findViewById(R.id.server_home);
        time=findViewById(R.id.server_time);
        title.setText("个人资料");
        tv_name=findViewById(R.id.tv_server_nick);
        tv_home=findViewById(R.id.tv_server_home);
        tv_work=findViewById(R.id.tv_server_work);
        tv_time=findViewById(R.id.tv_server_time);
        tv_name.setText(APP.getInstance().getServerUserInfo().getNickname());
        tv_work.setText(APP.getInstance().getServerUserInfo().getWorkAddress());
        tv_home.setText(APP.getInstance().getServerUserInfo().getHomeAddress());
        tv_time.setText(APP.getInstance().getServerUserInfo().getWorkTime());
        getServerUser();
        save=findViewById(R.id.server_save);
        updatename.setOnClickListener(this);
        workaddress.setOnClickListener(this);
        homeaddress.setOnClickListener(this);
        save.setOnClickListener(this);
        time.setOnClickListener(this);
        ImageView back;
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void getServerUser() {
        HttpProxy.obtain().get(PlatformContans.Serveruser.sGetServerUser, APP.getInstance().getServerUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.e("why",result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("resultCode");
                    if (code == 0) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        String nickname=object.getString("nickname");
                        if(!TextUtils.isEmpty(nickname)&&!TextUtils.equals("null",nickname))
                           tv_name.setText(nickname);
                        else{
                            tv_name.setText("用户"+APP.getInstance().getServerUserInfo().getAccount().substring(7,11));
                        }
                        tv_work.setText(object.getString("workAddress"));
                        tv_home.setText(object.getString("homeAddress"));
                        tv_time.setText(object.getString("workTime"));
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
        return R.layout.server_userinfo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            if (resultCode==RESULT_OK){
               tv_name.setText(data.getExtras().getString("name"));
               tv_name.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == 1&&data!=null) {
            AddressBean address = (AddressBean) data.getSerializableExtra("address");
            if(address!=null){
                String addressStr = address.getAddress();

                double lon = address.getLon();
                double lat = address.getLat();
                homelat=""+lat;
                homelon=""+lon;
                Log.d("onActivityResult", "onActivityResult: 经度：" + lon + ",维度:" + lat);
                if (!TextUtils.isEmpty(addressStr)) {
                    tv_home.setText(addressStr);
                }
            }

        }
        if (requestCode == 3&&data!=null) {

            AddressBean address = (AddressBean) data.getSerializableExtra("address");
            if(address!=null){
                String addressStr = address.getAddress();
                double lon = address.getLon();
                double lat = address.getLat();
                worklat=""+lat;
                worklon=""+lon;
                Log.d("onActivityResult", "onActivityResult: 经度：" + lon + ",维度:" + lat);
                if (!TextUtils.isEmpty(addressStr)) {
                    tv_work.setText(addressStr);
                }
            }

        }

    }
    String name="";
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.server_home:
                SelectAddressActivity.startSelectAddressActivity(this, "address", 1, tv_home.getText().toString());
                break;
            case R.id.server_updatemname:
               startActivityForResult(new Intent(ServerUserInfoActivity.this,UpdateNameActivity.class),2);
                break;
            case R.id.server_work:
                SelectAddressActivity.startSelectAddressActivity(this, "address", 3, tv_work.getText().toString());
                break;
            case R.id.server_time:
                final Window window=this.getWindow();
                final TimeSelectPopWindow mPop=new TimeSelectPopWindow(this);
                final WindowManager.LayoutParams params=window.getAttributes();
                params.alpha= (float) 0.5;
                window.setAttributes(params);
                mPop.showAtLocation(ServerUserInfoActivity.this.findViewById(R.id.server_main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                //window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        final WindowManager.LayoutParams params=window.getAttributes();
                        params.alpha= (float) 1.0;
                        window.setAttributes(params);

                    }
                });

                mPop.setOnItemClickListener(new TimeSelectPopWindow.OnItemClickListener() {
                     @Override
                     public void setOnItemClick(View v) {
                         switch (v.getId()){
                             case R.id.pop_cancel:
                                 mPop.dismiss();
                                 break;
                             case R.id.pop_right:
                                 mPop.dismiss();
                                 break;
                             case R.id.tv_dan:
                                 tv_time.setText("单休");
                                 break;
                             case R.id.tv_shuang:
                                 tv_time.setText("双休");
                                 break;

                         }
                     }
                 });
                break;
            case R.id.server_save:

                HttpProxy.obtain().post(PlatformContans.Serveruser.sUpdateServerUser, APP.getInstance().getServerUserInfo().getToken(), returnServer(), new ICallBack() {
                    @Override
                    public void OnSuccess(String result) {
                        JSONObject jsonObject= null;
                        try {
                            Log.e("sup",result);
                            jsonObject = new JSONObject(result);
                            int code=jsonObject.getInt("resultCode");
                            if(code==0){
                                JSONObject object=jsonObject.getJSONObject("data");
                                name=object.getString("nickname");
                                Intent intent=new Intent();
                                Bundle bundle=new Bundle();
                                bundle.putString("name",name);
                                intent.putExtras(bundle);
                                setResult(RESULT_OK,intent);
                                Toast.makeText(ServerUserInfoActivity.this,"修改成功",Toast.LENGTH_LONG).show();
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
                break;
            default:
                break;

        }
    }



    private String returnServer(){
        Map<String,Object> params=new HashMap<>();
        //ServerUserInfo serverUserInfo=APP.getInstance().getServerUserInfo();
        params.put("nickname",tv_name.getText().toString());
        params.put("homeAddress",tv_home.getText().toString());
        params.put("workAddress",tv_work.getText().toString());
        params.put("workTime",tv_time.getText().toString());
        params.put("workLatitude",worklat);
        params.put("workLongitude",worklon);
        params.put("homeLatitude",homelat);
        params.put("homeLongitude",homelon);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(params);
    }
}
