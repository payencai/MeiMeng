package com.example.meimeng.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
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
import com.example.meimeng.util.ToaskUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedSearchActivity extends BaseActivity {
    private RecyclerView drugRv;
    RVBaseAdapter<DrugInfo> adapter;
    TextView mTextView;
    @Override
    protected int getContentId() {
        return R.layout.activity_med_search;
    }
    private String calledPhone="";
    private boolean isIsits(String name){
        SharedPreferences preferences = getSharedPreferences("medhistory", MODE_PRIVATE);
        int count= preferences.getInt("count",0);
        //Log.e("count",count+"");
        for(int i=1;i<count+1;i++){
            String str=preferences.getString("h"+i,"");
            //Log.e("str",str);
            if(TextUtils.equals(name,str)){
                    return true;
            }
        }
        return false;
    }
    private int flag=0;
    @Override
    protected void initView() {
        mTextView=findViewById(R.id.search_null);
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
            flag= preferences.getInt("flag",0);
            //Log.e("falg",flag+"");
            if(!isIsits(name)){
                if(count>=5)
                {
                    switch (flag){
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            flag++;
                            editor.putString("h"+flag,name);
                            if(flag<5){
                                editor.putInt("flag",flag);
                            }else{
                                editor.putInt("flag",0);
                            }
                            editor.commit();
                            break;
                        default:
                            break;
                    }

                }else{
                    int newNum=count+1;
                    String val="h"+newNum;
                    editor.putString(val,name);
                    editor.putInt("count",newNum);
                    editor.commit();
                }
            }

        }
        adapter = new RVBaseAdapter<DrugInfo>() {
            @Override
            protected void onViewHolderBound(RVBaseViewHolder holder, final int position) {
                ImageView call=holder.getImageView(R.id.drug_phone);
                LinearLayout med= (LinearLayout) holder.getView(R.id.medicine);
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calledPhone=getData().get(position).getTelephone();
                        //Log.e("call","call");
                        call(calledPhone);

                    }
                });
                med.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // ToaskUtil.showToast(MedSearchActivity.this,"导航");
                        double lat= Double.parseDouble(adapter.getData().get(position).getLatitude());
                        double lon= Double.parseDouble(adapter.getData().get(position).getLongitude());
                        //showDialog(new LatLng(lat,lon));

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
    private void showDialog(final LatLng latLng) {
        final Dialog dialog = new Dialog(this, R.style.dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_map, null);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        dialog.setContentView(dialogView);
        dialog.findViewById(R.id.tv_baidu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                baidu(latLng.latitude,latLng.longitude);
            }
        });
        dialog.findViewById(R.id.tv_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                google(latLng.latitude,latLng.longitude);

            }
        });
        dialog.findViewById(R.id.tv_gaode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                gaode(latLng.latitude,latLng.longitude);

            }
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }
    private void google(double mLatitude,double mLongitude){
        if (isAvilible(this, "com.google.android.apps.maps")) {
            Uri gmmIntentUri = Uri.parse("google.navigation:q="
                    + mLatitude + "," + mLongitude
                    + ", + Sydney +Australia");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                    gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "您尚未安装谷歌地图", Toast.LENGTH_LONG)
                    .show();
            Uri uri = Uri
                    .parse("market://details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
    private void baidu(double mLatitude,double mLongitude){
        LatLng poit=new LatLng(mLatitude,mLongitude);
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
// sourceLatLng待转换坐标
        converter.coord(poit);
        poit = converter.convert();
        if (isAvilible(this, "com.baidu.BaiduMap")) {// 传入指定应用包名

            try {
                Intent intent = Intent.getIntent("intent://map/direction?destination=latlng:"
                        + poit.latitude + ","
                        + poit.longitude + "|name:" + // 终点
                        "&mode=driving&" + // 导航路线方式
                        "region=广东" + //
                        "&src=广州番禺#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                startActivity(intent); // 启动调用
            } catch (URISyntaxException e) {
                Log.e("intent", e.getMessage());
            }
        } else {// 未安装
            Toast.makeText(this, "您尚未安装百度地图", Toast.LENGTH_LONG)
                    .show();
            Uri uri = Uri
                    .parse("market://details?id=com.baidu.BaiduMap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
    private void gaode(double mLatitude,double mLongitude){
        if (isAvilible(this, "com.autonavi.minimap")) {
            try {
                Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=新疆和田&poiname="+"广州"+"&lat="
                        + mLatitude
                        + "&lon="
                        + mLongitude + "&dev=0");
                startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "您尚未安装高德地图", Toast.LENGTH_LONG)
                    .show();
            Uri uri = Uri
                    .parse("market://details?id=com.autonavi.minimap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
    public static boolean isAvilible(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
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
            latitude=APP.lat+"";
            longitude=APP.lon+"";
            Log.e("latlng",latitude+"-------"+longitude);
        } else {
            token = APP.getInstance().getServerUserInfo().getToken();
            latitude=APP.getInstance().getServerUserInfo().getWorkLatitude();
            longitude=APP.getInstance().getServerUserInfo().getWorkLongitude();
        }

        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("name", medname);
        HttpProxy.obtain().get(PlatformContans.ForHelp.sGetServerMedicine, params, token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                //mSearchText.setVisibility(View.GONE);
                //Log.e("search1", result);
                Log.e("med",result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("resultCode");
                    if (code == 0) {
                        List<DrugInfo> list = new ArrayList<>();
                        JSONArray beanlist = jsonObject.getJSONArray("data");
                        if (beanlist.length() == 0) {
                            mTextView.setVisibility(View.VISIBLE);
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
                                String latitude=object.getString("latitude");
                                String longitude=object.getString("longitude");
                                drugInfo.setLatitude(latitude);
                                drugInfo.setLongitude(longitude);
                                LatLng start=new LatLng(APP.locateLat,APP.locateLon);
                                LatLng end=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
                                double distance= DistanceUtil.getDistance(start,end);
                                drugInfo.setDistance(distance);
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
