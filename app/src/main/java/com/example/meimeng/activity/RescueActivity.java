package com.example.meimeng.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
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
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.service.LocationService;
import com.example.meimeng.util.CustomPopWindow;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RescueActivity extends BaseActivity {

    private static final String TAG = RescueActivity.class.getSimpleName();

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
    @BindView(R.id.metronome)
    TextView metronome;


    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private CurrentHelpInfo mCurrentHelpInfo;

    private WebSocketClient mWebSocketClient;

    private double mCurLat;
    private double mCurLon;
    private int end = 0;//1所有人救援结束(完成救援) 2单方面结束救援(取消救援)

    private LocationService locationService;

    private LocationHandler mHandler = new LocationHandler(this);

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    /**
     * 发送当前位置信息
     */
    private static final int SEND_LOCATION_CODE = 0;

    private static final int DELAY_REQUEST_CODE = 1;

    @Override

    protected int getContentId() {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        locationService = APP.getInstance().locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，
        // 然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(myListener);
        return R.layout.activity_rescue;
    }

    @Override
    protected void initView() {
        //获取地图控件引用
        ButterKnife.bind(this);

        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        double lat = intance.getLat();
        double lon = intance.getLon();

        mCurLat = lat;
        mCurLon = lon;

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
        locationService.start();
        initSockect();
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
//        if (lat != 0 || lon != 0) {
//            LatLng cenpt = new LatLng(lat, lon);
//            setMarker(cenpt);
//            setUserMapCenter(cenpt);
//        }
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
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        locationService.unregisterListener(myListener); //注销掉监听
        locationService.stop(); //停止定位服务
    }

    /**
     * 添加marker
     */
    private void setMarker(LatLng point) {
        //定义Maker坐标点
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_high_volunteer);
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

    @OnClick({R.id.comeBackText, R.id.saveText, R.id.callTel, R.id.metronome, R.id.messageText, R.id.helperHead})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.comeBackText:
//                finish();
                closeRescue(view);
                break;
            case R.id.saveText:
                showHelpEnd(view);
                break;
            case R.id.callTel:
                checkPower();
                break;
            case R.id.metronome:
                startActivity(new Intent(this, CPROptionActivity.class));
                break;
            case R.id.messageText:
                startActivity(new Intent(this, ChatActivity.class));
                break;
            case R.id.helperHead:
                //TODO:
//                String endString = getEndString();
//                mWebSocketClient.send(endString);
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
    public void onBackPressed() {
        if (true) {
            return;
        }
        super.onBackPressed();
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

    private String address = "ws://47.106.164.34:80/memen/websocket";
    private String PORT = "80";
    private URI uri;

    public void initSockect() {
        try {
            uri = new URI(address);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (null == mWebSocketClient) {
            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.i(TAG, "onOpen: " + serverHandshake.getHttpStatusMessage());
                    String jsonSrting = getEndString();
                    String jsonSrting1 = getJsonSrting();
                    Log.d(TAG, "onOpen: " + jsonSrting);
//                    mWebSocketClient.send(jsonSrting1);
                }

                @Override
                public void onMessage(String s) {
                    Log.i(TAG, "onMessage: " + s);
//                    disposeWebData(s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i(TAG, "onClose: " + s);
                }

                @Override
                public void onError(Exception e) {
                    Log.i(TAG, "onError: ");
                }
            };
        }
        mWebSocketClient.connect();
    }

    private String getJsonSrting() {
        String result = null;
        JSONObject json = null;
        ServerUserInfo userInfo = APP.getInstance().getServerUserInfo();
        if (userInfo == null) {
            ToaskUtil.showToast(this, "登录异常");
            ActivityManager.getInstance().finishAllActivity();
            return "";
        }

        try {
            json = new JSONObject();
            json.put("type", 2);//1求救人员 2救援人员
            json.put("demand_latitude", mCurLat);
            json.put("demand_longitude", mCurLon);
            json.put("toUserId", userInfo.getId());
            json.put("image", userInfo.getImage());
            json.put("imageKey", userInfo.getImageKey());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private String getEndString() {
        JSONObject json = null;
        ServerUserInfo userInfo = APP.getInstance().getServerUserInfo();
        if (userInfo == null) {
            ToaskUtil.showToast(this, "登录异常");
            ActivityManager.getInstance().finishAllActivity();
            return "";
        }

        try {
            json = new JSONObject();
            json.put("type", 2);//1求救人员 2救援人员
            json.put("demand_latitude", mCurLat);
            json.put("demand_longitude", mCurLon);
            json.put("toUserId", userInfo.getId());
            json.put("image", userInfo.getImage());
            json.put("imageKey", userInfo.getImageKey());
            json.put("isCertificate", userInfo.getIsCertificate());
            if (end > 0) {
//                dic[@"end"] = @(end);//1所有人救援结束(完成救援) 2单方面结束救援(取消救援)
//                dic[@"closeUserId"] = [BaseNetworkModel shareBaseNetwork].Id;//关闭人ID
//                dic[@"closeUserType"] = @(1);//关闭人类型，1：求救，2：救援
//                dic[@"closeName"] = [BaseNetworkModel shareBaseNetwork].name;//关闭人姓名
//                dic[@"closeTelephone"] = [BaseNetworkModel shareBaseNetwork].telephone;//关闭人电话
                json.put("end", end);
                json.put("closeUserId", userInfo.getId());
                json.put("closeUserType", 2);
                json.put("closeName", userInfo.getName());
                json.put("closeTelephone", userInfo.getTelephone());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
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

    private void showHelpEnd(View view) {
        View otherView = LayoutInflater.from(this).inflate(R.layout.pw_endhelp_layout, null);
        CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(otherView)
                .sizeByPercentage(this, 0.8f, 0)
                .setOutsideTouchable(true)
                .enableBackgroundDark(true)
                .setAnimationStyle(R.style.CustomPopWindowStyle)
                .setBgDarkAlpha(0.5f)
                .create();
        handlerEndHelpView(otherView, customPopWindow);
        customPopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void handlerEndHelpView(View view, final CustomPopWindow customPopWindow) {
        view.findViewById(R.id.complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
                helpEnd("2", mCurrentHelpInfo.getId(), 0);
            }
        });
        view.findViewById(R.id.details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
                helpEnd("2", mCurrentHelpInfo.getId(), 1);//这里有内存泄漏风险
            }
        });
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
                                end = 2;
                                helpFinish();
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

    private void helpEnd(String endType, String id, final int type) {
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
                    if (resultCode == 0) {
                        if (type == 1) {
                            startActivity(new Intent(RescueActivity.this, ServerRecordActivity.class));
                        }
                        end = 1;
                        helpFinish();
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

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            int LocType = location.getLocType();    //返回码
            mCurLat = location.getLatitude();
            mCurLon = location.getLongitude();
            location();
            locationService.setLocationOption(locationService.getSingleLocationClientOption());
            // TODO Auto-generated method stub
        }
    };

    public void location() {
        LatLng point = new LatLng(mCurLat, mCurLon);
        setMarker(point);
        setUserMapCenter(point);
//        mHandler.sendEmptyMessage(SEND_LOCATION_CODE);
    }

    /**
     * 某种原因结束救援
     */
    private void helpFinish() {
        getEndString();
    }

    private static class LocationHandler extends Handler {
        private WeakReference<Activity> activity;

        public LocationHandler(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RescueActivity activity = (RescueActivity) this.activity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case SEND_LOCATION_CODE:
                    String endString = activity.getEndString();
                    activity.mWebSocketClient.send(endString);
                    sendEmptyMessageDelayed(DELAY_REQUEST_CODE, 2000);
                    break;
                case DELAY_REQUEST_CODE:
                    activity.locationService.start();
                    break;
            }
        }
    }
}
