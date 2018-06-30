package com.example.meimeng.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.CurrentHelpInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.CustomPopWindow;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RescueActivity extends BaseActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.comeBackText)
    TextView comeBackText;
    @BindView(R.id.saveText)
    TextView saveText;
    @BindView(R.id.helperHead)
    CircleImageView helperHead;
    @BindView(R.id.helperName)
    TextView helperName;
    @BindView(R.id.helperTime)
    TextView helperTime;
    @BindView(R.id.workerNumber)
    TextView workerNumber;
    @BindView(R.id.helperAdress)
    TextView helperAdress;
    @BindView(R.id.callTel)
    TextView callTel;
    @BindView(R.id.helperDistance)
    TextView helperDistance;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private CurrentHelpInfo mCurrentHelpInfo;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    @Override

    protected int getContentId() {
        return R.layout.activity_rescue;
    }

    @Override
    protected void initView() {
//获取地图控件引用
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentHelpInfo = (CurrentHelpInfo) intent.getSerializableExtra("currentHelpInfo");
        if (mCurrentHelpInfo == null) {
            finish();
            return;
        }

        back.setVisibility(View.GONE);
        comeBackText.setVisibility(View.VISIBLE);
        saveText.setVisibility(View.VISIBLE);
        comeBackText.setText("取消救援");
        title.setText("正在救援");
        saveText.setText("完成救援");

        mMapView = (MapView) findViewById(R.id.bmapView);
        //开启定位图层
        mBaiduMap = mMapView.getMap();

        initDataView();
    }

    private void initDataView() {
//        helperHead
        Glide.with(this).load(mCurrentHelpInfo.getImage()).into(helperHead);
        helperAdress.setText(mCurrentHelpInfo.getUserAddress());
        helperName.setText(mCurrentHelpInfo.getUseUserName());
        int distance = (int) mCurrentHelpInfo.getDistance();
        helperDistance.setText("与您" + distance + "米范围内");
        helperTime.setText(mCurrentHelpInfo.getCreateTime());
        int helpNum = mCurrentHelpInfo.getHelpNum();
        workerNumber.setText("已有" + helpNum + "人前往");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        double lat = intance.getLat();
        double lon = intance.getLon();
        if (lat != 0 || lon != 0) {
            LatLng cenpt = new LatLng(lat, lon);
            setMarker(cenpt);
            setUserMapCenter(cenpt);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    /**
     * 添加marker
     */
    private void setMarker(LatLng point) {
        //定义Maker坐标点
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_location);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    /**
     * 设置中心点
     */
    private void setUserMapCenter(LatLng cenpt) {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

    }

    @OnClick({R.id.comeBackText, R.id.saveText, R.id.callTel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.comeBackText:
//                finish();
                closeRescue(view);
                break;
            case R.id.saveText:
                helpEnd("2", mCurrentHelpInfo.getId());
                break;
            case R.id.callTel:
                checkPower();
                break;
        }
    }

    private void checkPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            callPhone();
        }
    }

    @SuppressLint("MissingPermission")
    public void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + mCurrentHelpInfo.getUseUserTelephone());
        intent.setData(data);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone();
            } else {
                // Permission Denied
                Toast.makeText(this, "权限拒绝", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void closeRescue(View view) {
        View otherView = LayoutInflater.from(this).inflate(R.layout.pw_close_rescue_layout, null);
        CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(otherView)
                .sizeByPercentage(this, 0.8f, 0.5f)
                .setOutsideTouchable(true)
                .enableBackgroundDark(true)
                .setAnimationStyle(R.style.CustomPopWindowStyle)
                .setBgDarkAlpha(0.5f)
                .create();
        handlerCloseRescueView(otherView);
        customPopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private String causeCache;

    private void handlerCloseRescueView(View view) {
        final EditText closeCause = (EditText) view.findViewById(R.id.closeCause);
        if (TextUtils.isEmpty(causeCache)) {
            closeCause.setText(causeCache);
        }
        closeCause.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                causeCache = s.toString();
            }
        });
        view.findViewById(R.id.submitCause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToaskUtil.showToast(RescueActivity.this, "提交原因");
                String s = closeCause.getEditableText().toString();
                if (TextUtils.isEmpty(s)) {
                    ToaskUtil.showToast(RescueActivity.this, "请填写原因");
                    return;
                }
                cancelHelp(s, mCurrentHelpInfo.getId());
            }
        });
    }

    private void cancelHelp(String reason, String helpId) {

        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("reason", reason);
            json.put("helpId", helpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("cancelbyserveruser", "cancelHelp: " + json.toString());
        String token = APP.getInstance().getServerUserInfo().getToken();
        if (TextUtils.isEmpty(token)) {
            ToaskUtil.showToast(this, "登录过期");
            ActivityManager.getInstance().finishAllActivity();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        HttpProxy.obtain().post(PlatformContans.ForHelp.sUpdateForHelpInfoToCancelByServerUser,
                token, json.toString(), new ICallBack() {
                    @Override
                    public void OnSuccess(String result) {
                        MLog.log("cancelbyserveruser", result);
                        try {
                            JSONObject object = new JSONObject(result);
                            int resultCode = object.getInt("resultCode");
                            String message = object.getString("message");
                            ToaskUtil.showToast(RescueActivity.this, message);
                            if (resultCode == 0) {
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        ToaskUtil.showToast(RescueActivity.this, "请检查网络");
                    }
                });
    }

    private void helpEnd(String endType, String id) {
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("endType", endType);
            json.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("cancelbyserveruser", "cancelHelp: " + json.toString());
        String token = APP.getInstance().getServerUserInfo().getToken();
        if (TextUtils.isEmpty(token)) {
            ToaskUtil.showToast(this, "登录过期");
            ActivityManager.getInstance().finishAllActivity();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        HttpProxy.obtain().post(PlatformContans.ForHelp.sUpdateForHelpInfoToEnd, token, json.toString(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                MLog.log("sUpdateForHelpInfoToEnd", result);
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    String message = object.getString("message");
//                    ToaskUtil.showToast(RescueActivity.this, message);
//                    if (resultCode == 0) {
//                        finish();
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                ToaskUtil.showToast(RescueActivity.this, "请检查网络");
            }
        });
    }
}
