package com.example.meimeng.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.DrugInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedSearchActivity extends BaseActivity {
    private RecyclerView drugRv;
    RVBaseAdapter<DrugInfo> adapter;

    @Override
    protected int getContentId() {
        return R.layout.activity_med_search;
    }
    private String calledPhone="";
    @Override
    protected void initView() {
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        drugRv=(RecyclerView)findViewById(R.id.med_rv);
        String name=getIntent().getStringExtra("name")+"";
        if(!TextUtils.isEmpty(name)){
            SharedPreferences preferences = getSharedPreferences("medhistory", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();


            int count= preferences.getInt("count",0);
            switch (count){
                case  0:
                case  1:
                case  2:
                case  3:
                case  4:
                    if (!preferences.contains(name)){
                        count++;
                        editor.putString("h"+count, name);
                        editor.putInt("count",count);
                        editor.commit();
                    }
                    break;
                default:
                    editor.remove(preferences.getString("h5",""));
                    editor.commit();
                    editor.putString("h"+5, name);
                    editor.commit();
                    break;
            }
        }
        adapter = new RVBaseAdapter<DrugInfo>() {
            @Override
            protected void onViewHolderBound(RVBaseViewHolder holder, final int position) {
                ImageView call=holder.getImageView(R.id.drug_phone);

                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calledPhone=getData().get(position).getTelephone();
                        Log.e("call","call");
                        call(calledPhone);

                    }
                });
            }
        };
        //Log.e("med",name);
        searchMedicine(name);
    }
    private void  getVirtualPhone(String phone){
        Map<String,Object> param=new HashMap<>();
        param.put("callingPhone",APP.getInstance().getUserInfo().getAccount());
        param.put("calledPhone",phone);
        HttpProxy.obtain().get(PlatformContans.Medicine.sGetVirtualNumber, param, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    calledPhone=jsonObject.getString("data");
                    requestPermission();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }


    /**
     * 注册权限申请回调
     * @param requestCode 申请码
     * @param permissions 申请的权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone(calledPhone);
                } else {
                    Toast.makeText(MedSearchActivity.this, "CALL_PHONE Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

        /**
         * 申请权限
         */
    private void requestPermission()
    {
        //判断Android版本是否大于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                       1);
                return;
            }
            else
            {
                callPhone(calledPhone);
            }
        }
        else
        {
            callPhone(calledPhone);
        }
    }

    /**
     * 拨打电话
     */
    private void callPhone(String calledPhone) {
        // 1. 到了拨号界面，但是实际的拨号是由用户点击实现的。
        Intent intent = new Intent(Intent.ACTION_DIAL);
        // 2. 对用户没有提示，直接拨打电话
        // Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + calledPhone);
        intent.setData(data);
        startActivity(intent);
    }
    private void call(final String phone){
        AlertDialog.Builder builder = new AlertDialog.Builder(MedSearchActivity.this);
        builder.setTitle("温馨提示")
                .setMessage("使用虚拟号码拨打，主叫号码必须是你的登录账号，若非登录账号拨打，请选择正常号码拨打")
                .setPositiveButton("本机号码拨打", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission();
                    }
                })
                .setNegativeButton("虚拟号码拨打", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getVirtualPhone(phone);
                    }
                });
        builder.show();


    }
    private void searchMedicine(String medname) {
        Map<String, Object> params = new HashMap<>();
        String latitude = "23.05184555053711";
        String longitude = "113.4033126831055";
        String token = "";
        if (APP.sUserType == 0) {
            token = APP.getInstance().getUserInfo().getToken();
            latitude=APP.getInstance().getUserInfo().getLatitude();
            longitude=APP.getInstance().getUserInfo().getLongitude();

        } else {
            token = APP.getInstance().getServerUserInfo().getToken();
            latitude=APP.getInstance().getServerUserInfo().getHomeLatitude();
            longitude=APP.getInstance().getServerUserInfo().getHomeLongitude();
        }
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("name", medname);
        HttpProxy.obtain().get(PlatformContans.ForHelp.sGetServerMedicine, params, token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                //mSearchText.setVisibility(View.GONE);
                Log.e("search1", result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("resultCode");
                    if (code == 0) {
                        List<DrugInfo> list = new ArrayList<>();
                        JSONArray beanlist = jsonObject.getJSONArray("data");
                        if (beanlist.length() == 0) {
                            adapter.setData(list);
                            drugRv.setLayoutManager(new LinearLayoutManager(MedSearchActivity.this));
                            drugRv.setAdapter(adapter);
                        } else {
                            for (int i = 0; i < beanlist.length(); i++) {
                                DrugInfo drugInfo = new DrugInfo();
                                JSONObject object = (JSONObject) beanlist.get(i);
                                drugInfo.setName(object.getString("name"));
                                drugInfo.setAddress(object.getString("address"));
                                drugInfo.setTelephone(object.getString("telephone"));
                                drugInfo.setMedicine(object.getString("medicine"));
                                drugInfo.setDistance(object.getInt("distance"));
                                list.add(drugInfo);
                            }
                            adapter.setData(list);
                            drugRv.setLayoutManager(new LinearLayoutManager(MedSearchActivity.this));
                            drugRv.setAdapter(adapter);
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
}
