package com.example.meimeng.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bumptech.glide.Glide;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AEDInfo;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.bean.CurrentHelpInfo;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.bean.Point;
import com.example.meimeng.bean.ServerUser;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.overlayutil.TransitRouteOverlay;
import com.example.meimeng.overlayutil.WalkingRouteOverlay;
import com.example.meimeng.service.LocationService;
import com.example.meimeng.test.RouteLineAdapter;
import com.example.meimeng.test.RoutePlanDemo;
import com.example.meimeng.util.CustomPopWindow;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.exceptions.HyphenateException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RescueActivity extends BaseActivity implements OnGetRoutePlanResultListener {

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
    @BindView(R.id.parent)
    LinearLayout parent;
    @BindView(R.id.my_unread_msg_number)
    TextView mUnreadMsgNumber;


    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private CurrentHelpInfo mCurrentHelpInfo;

    private WebSocketClient mWebSocketClient;
    private boolean isFristShowEnd = true;

    private double mCurLat;
    private double mCurLon;

    private double mCallerLat;
    private double mCallerLon;

    private int end = 0;//1所有人救援结束(完成救援) 2单方面结束救援(取消救援)

    private LocationService locationService;

    private LocationHandler mHandler = new LocationHandler(this);

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    /**
     * 发送当前位置信息
     */
    private static final int SEND_LOCATION_CODE = 0;

    private static final int DELAY_REQUEST_CODE = 1;

    private static final int LOGIN_HX = 2;

    public static final int REQUE_LOGINHX_MAX_COUNT = 3;//请求登录环信的最大次数

    public static final int CLEAR_UNREAD_MSG = 1 << 4;

    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    private LruCache<String, Bitmap> mMemoryCache;
    private Set<BitmapWorkerTask> taskCollection;

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

        taskCollection = new HashSet<>();
        //获取系统给每一个应用所分配的内存大小
        long maxMemory = Runtime.getRuntime().maxMemory();
        int maxSize = (int) (maxMemory / 8);
        //参数表示LruCache中可缓存的图片总大小
        mMemoryCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //返回图片的大小，默认返回图片的数量
                return value.getByteCount();
            }
        };

        Intent intent = getIntent();
        mCurrentHelpInfo = (CurrentHelpInfo) intent.getSerializableExtra("currentHelpInfo");
        if (mCurrentHelpInfo == null) {
            finish();
            return;
        }

        String latitude = mCurrentHelpInfo.getLatitude();
        String longitude = mCurrentHelpInfo.getLongitude();
        try {
            mCallerLat = Double.parseDouble(latitude);
            mCallerLon = Double.parseDouble(longitude);
        } catch (Exception e) {
            ToaskUtil.showToast(this, "位置异常");
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
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        initDataView();
        locationService.start();
        applyGroup();
        initSockect();
        setMessageListener();

    }

    private void setMessageListener() {
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            getUnreadMsg(mCurrentHelpInfo.getGroupId());
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    private void getUnreadMsg(String username) {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        final int unreadMsgCount = conversation.getUnreadMsgCount();
        Log.d("getUnreadMsg", "getUnreadMsg: " + unreadMsgCount);
        if (unreadMsgCount > 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mUnreadMsgNumber.setVisibility(View.VISIBLE);
                    mUnreadMsgNumber.setText(unreadMsgCount + "");
                }
            });

        }
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
        if (mSearch != null) {
            mSearch.destroy();
        }
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        locationService.unregisterListener(myListener); //注销掉监听
        locationService.stop(); //停止定位服务
        try {
            mWebSocketClient.close();
        } catch (Exception e) {

        }
//        记得在不需要的时候移除listener，如在activity的onDestroy()时
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
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
     * 添加marker
     */
    private void setMarker2(LatLng point) {
        //定义Maker坐标点
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_lation_wait_helper);
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

    @OnClick({R.id.comeBackText, R.id.saveText, R.id.callTel, R.id.metronome, R.id.messageText, R.id.helperHead, R.id.toAED})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.comeBackText:
//                finish();
                closeRescue(view);
                break;
            case R.id.saveText:
                if (isFristShowEnd) {
                    showHelpEnd(view);
                } else {
                    ToaskUtil.showToast(this, "此救援信息已完成或已取消");
                }
                break;
            case R.id.callTel:
                checkPower();
                break;
            case R.id.metronome:
                startActivity(new Intent(this, CPROptionActivity.class));
                break;
            case R.id.messageText:
                LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
                String groupId = mCurrentHelpInfo.getGroupId();
                ChatActivity.startChatActivity(this, groupId, CLEAR_UNREAD_MSG);
                break;
            case R.id.helperHead:
                break;
            case R.id.toAED:
//                startActivity(new Intent(this, ShowAEDActivity.class));
                ShowAEDActivity.startShowAED(this, mCurLat, mCurLon);
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
        if (isFristShowEnd) {
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
    //    private String address = "ws://192.168.1.18:8080/memen/websocket";
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
//                    String jsonSrting = getJsonSrting();
                    mWebSocketClient.send(jsonSrting);
                }

                @Override
                public void onMessage(String s) {
                    Log.i("onMessagetag", s);
                    disposeWebData(s);
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

    private void disposeWebData(String s) {
        try {
            JSONObject object = new JSONObject(s);
            String toUserId = object.getString("toUserId");
            if (toUserId.equals(mCurrentHelpInfo.getUseUserId())) {

                if (object.has("end")) {
                    int end = object.getInt("end");
                    final String closeName = object.getString("closeName");
                    final String closeTelephone = object.getString("closeTelephone");
                    if (end == 1) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mWebSocketClient.close();
                                    mHandler.removeCallbacksAndMessages(null);
                                } catch (Exception e) {

                                }
                                if (isFristShowEnd) {
                                    isFristShowEnd = false;
                                    showhelpEnd(parent, closeName, closeTelephone);
                                }
                            }
                        });
                        return;
                    }
                }

                if (object.has("rescuepersonnum")) {
                    final int rescuepersonnum = object.getInt("rescuepersonnum");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            workerNumber.setText("已有" + rescuepersonnum + "人前往");
                        }
                    });
                }
                if (object.has("demand_longitude") && object.has("demand_latitude")) {
                    String demand_longitude = object.getString("demand_longitude");
                    String demand_latitude = object.getString("demand_latitude");
                    double demandLat = Double.parseDouble(demand_latitude);
                    double demandLon = Double.parseDouble(demand_longitude);
                    LatLng startPoint = new LatLng(mCurLat, mCurLon);
                    LatLng endPoint = new LatLng(demandLat, demandLon);

                    try {
                        mCallerLat = Double.parseDouble(demand_latitude);
                        mCallerLon = Double.parseDouble(demand_longitude);
                    } catch (Exception e) {
                    }

                    final int distance = (int) DistanceUtil.getDistance(startPoint, endPoint);//距离定位的距离
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            helperDistance.setText("与您" + distance + "米范围内");
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            json.put("latitude", mCurLat);
            json.put("longitude", mCurLon);
            json.put("toUserId", mCurrentHelpInfo.getUseUserId());//求救人的id
            json.put("userId", userInfo.getId());//志愿者的id
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
        String s = json.toString();
        Log.d("getEndString", "getEndString: " + s);
        return s;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUnreadMsgNumber.setText("");
        mUnreadMsgNumber.setVisibility(View.GONE);

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

    private void showhelpEnd(View view, String closeName, String closeTelephone) {
        View otherView = LayoutInflater.from(this).inflate(R.layout.pw_hint_helpend_layout, null);
        CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(otherView)
                .sizeByPercentage(this, 0.8f, 0f)
                .setOutsideTouchable(false)
                .enableBackgroundDark(true)
                .setAnimationStyle(R.style.CustomPopWindowStyle)
                .setBgDarkAlpha(0.5f)
                .create();
        handlerFinishHelpView(otherView, customPopWindow, closeName, closeTelephone);
        customPopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void handlerFinishHelpView(View view, final CustomPopWindow customPopWindow, String closeName, String closeTelephone) {
        //有救援人:13480197692\n点击完成救助
        TextView showContent = (TextView) view.findViewById(R.id.showContent);
        String content = "有救援人:" + closeName + " \n点击完成救助";
        showContent.setText(content);
        view.findViewById(R.id.know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
                finish();
            }
        });
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
            LoginSharedUilt intance = LoginSharedUilt.getIntance(RescueActivity.this);
            intance.saveLat(mCurLat);
            intance.saveLon(mCurLon);
            intance.saveCity(city);
            intance.saveAddr(addr);
            locationService.setLocationOption(locationService.getSingleLocationClientOption());
            location(city, street);
            // TODO Auto-generated method stub
        }
    };

    private void applyGroup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //如果群开群是自由加入的，即group.isMembersOnly()为false，直接join
                    EMClient.getInstance().groupManager().joinGroup(mCurrentHelpInfo.getGroupId());//需异步处理
                    //需要申请和验证才能加入的，即group.isMembersOnly()为true，调用下面方法
//            EMClient.getInstance().groupManager().applyJoinToGroup(groupid, "求加入");//需异步处理
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    String tmep = "Userisnotlogin";
                    int errorCode = e.getErrorCode();
                    String message = e.getMessage();
                    String replace = message.replace(" ", "");
                    if (replace.equals(tmep) && errorCode == 201) {
                        loginHx();
                    }
                    if (errorCode == 601) {
                        loginHx();
                    }
                    Log.d(TAG, "run: 加入群失败，" + e.getDescription() + "," + e.getMessage() + "," + e.getLocalizedMessage() + "," + e
                            .getErrorCode());
                }
            }
        }).start();

    }

    private void loginHx() {
        Log.d("asyncCreateGroup", "loginHx: 登录hx");
        String userName = null;
        String password = null;
        if (APP.sUserType == 0) {
            UserInfo userInfo = APP.getInstance().getUserInfo();
            userName = userInfo.getId();
            password = userInfo.getHxPwd();
        } else {
            ServerUserInfo serverUserInfo = APP.getInstance().getServerUserInfo();
            userName = serverUserInfo.getId();
            password = serverUserInfo.getHxPwd();
        }
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userName)) {
            ToaskUtil.showToast(this, "数据获取失败");
            ActivityManager.getInstance().finishAllActivity();
            return;
        }
        EMClient.getInstance().login(userName, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: MainActivity环信登录成功回调");
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                applyGroup();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("asyncCreateGroup", "登录聊天服务器失败！" + code + "," + message);
                String replace = message.replace(" ", "");
                if (replace.equals("Userisalreadylogin") && code == 200) {
                    applyGroup();
                    return;
                }
                mHandler.sendEmptyMessageDelayed(LOGIN_HX, 1000);
            }
        });
    }

    public void location(String city, String street) {
        LatLng point = new LatLng(mCurLat, mCurLon);
        String latitude = mCurrentHelpInfo.getLatitude();
        String longitude = mCurrentHelpInfo.getLongitude();
        LatLng point2 = null;

        try {
            double lat2 = Double.parseDouble(latitude);
            double lon2 = Double.parseDouble(longitude);
            point2 = new LatLng(lat2, lon2);
        } catch (Exception e) {
        }
        if (point2 == null) {
            ToaskUtil.showToast(this, "位置获取异常");
            end = 2;
            helpFinish();
            return;
        }

        int distance = (int) DistanceUtil.getDistance(point, point2);//距离定位的距离
        helperDistance.setText("与您" + distance + "米范围内");
        mBaiduMap.clear();
        setMarker(point);
        setMarker2(point2);
        walkProject(point);


        if (isFristMaoCenter) {
            isFristMaoCenter = false;
            setUserMapCenter(point);
        }
        mHandler.sendEmptyMessage(SEND_LOCATION_CODE);
    }

    private void walkProject(LatLng point) {
        LatLng endPt = new LatLng(mCallerLat, mCallerLon);

        PlanNode stNode = PlanNode.withLocation(point);
        PlanNode enNode = PlanNode.withLocation(endPt);
        if (endPt == null || stNode == null) {
            ToaskUtil.showToast(this, "位置异常");
            finish();
            return;
        }
        mSearch.walkingSearch((new WalkingRoutePlanOption())
                .from(stNode).to(enNode));
    }

    boolean isFristMaoCenter = true;

    /**
     * 某种原因结束救援
     */
    private void helpFinish() {
        mHandler.removeCallbacksAndMessages(null);
        String endString = getEndString();
        try {
            mWebSocketClient.send(endString);
        } catch (Exception e) {
            mWebSocketClient = null;
        }
        finish();
    }

    private void addGroupChangListener() {
        EMClient.getInstance().groupManager().addGroupChangeListener(new EMGroupChangeListener() {
            @Override
            public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
                //接收到群组加入邀请
            }

            @Override
            public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {
                //用户申请加入群
            }

            @Override
            public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
                //加群申请被同意
                Log.d("onRequestToJoinAccepted", "onRequestToJoinAccepted: 加群申请被同意");
            }

            @Override
            public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
                //加群申请被拒绝
            }

            @Override
            public void onInvitationAccepted(String groupId, String inviter, String reason) {
                //群组邀请被同意
            }

            @Override
            public void onInvitationDeclined(String groupId, String invitee, String reason) {
                //群组邀请被拒绝
            }

            @Override
            public void onUserRemoved(String s, String s1) {

            }

            @Override
            public void onGroupDestroyed(String s, String s1) {

            }

            @Override
            public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
                //接收邀请时自动加入到群组的通知
            }

            @Override
            public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
                //成员禁言的通知
            }

            @Override
            public void onMuteListRemoved(String groupId, final List<String> mutes) {
                //成员从禁言列表里移除通知
            }

            @Override
            public void onAdminAdded(String groupId, String administrator) {
                //增加管理员的通知
            }

            @Override
            public void onAdminRemoved(String groupId, String administrator) {
                //管理员移除的通知
            }

            @Override
            public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
                //群所有者变动通知
            }

            @Override
            public void onMemberJoined(final String groupId, final String member) {
                //群组加入新成员通知
            }

            @Override
            public void onMemberExited(final String groupId, final String member) {
                //群成员退出通知
            }

            @Override
            public void onAnnouncementChanged(String groupId, String announcement) {
                //群公告变动通知
            }

            @Override
            public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {
                //增加共享文件的通知
            }

            @Override
            public void onSharedFileDeleted(String groupId, String fileId) {
                //群共享文件删除通知
            }
        });
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RescueActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//            mBaiduMap.clear();
            WalkingRouteOverlay walkingRouteOverlay = new WalkingRouteOverlay(mBaiduMap);
            walkingRouteOverlay.setData(result.getRouteLines().get(0));
            walkingRouteOverlay.addToMap();
            walkingRouteOverlay.zoomToSpan();
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RescueActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    private void batchAddAED(List<AEDInfo> list) {
        List<OverlayOptions> options = new ArrayList<OverlayOptions>();
        //构建Marker图标
        //当前自己的位置
        BitmapDescriptor bitmap1 = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_exist_aed3);
        //构建Marker图标
        BitmapDescriptor bitmap2 = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_unexist_aed3);
        for (AEDInfo AEDInfobean : list) {
            //isCertificate 1为高级，2为普通
            //workLongitude  经度
            //workLatitude 维度
            AEDInfo.KeyBean key = AEDInfobean.getKey();
            int isPass = key.getIsPass();
            String workLatitude = key.getLatitude();
            String workLongitude = key.getLongitude();
            double latNumber = 0;
            double lonNumber = 0;
            LatLng point;
            try {
                latNumber = Double.parseDouble(workLatitude);
                lonNumber = Double.parseDouble(workLongitude);
            } catch (Exception e) {

            }
            point = new LatLng(latNumber, lonNumber);
            MarkerOptions position = new MarkerOptions().position(point);
            OverlayOptions option;
            if (isPass == 4) {
                option = position.icon(bitmap1);
            } else {
                option = position.icon(bitmap2);
            }
//            options.add(option);
//            Marker marker = (Marker) mBaiduMap.addOverlay(option);
            mBaiduMap.addOverlay(option);
//            String address = key.getAddress();//广州新东方学校大学城教学区广州市番禺区大学城中六路1号广州大学城信息枢纽楼814房
//            String expiryDate = key.getExpiryDate();//2018-05-23
//            String brank = key.getBrank();//回家你那边
//            Bundle bundle = new Bundle();
//            bundle.putInt("type", 0);//0为AED 1为志愿者覆盖物
//            bundle.putString("telephone", key.getTel());
//            bundle.putDouble("latNumber", latNumber);
//            bundle.putDouble("lonNumber", lonNumber);
//
//            bundle.putString("address", address);
//            bundle.putString("expiryDate", expiryDate);
//            bundle.putString("brank", brank);
//            marker.setExtraInfo(bundle);
        }
    }

    private static class LocationHandler extends Handler {
        private WeakReference<Activity> activity;
        private int requstLoginCount = 0;

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
                    try {
                        activity.mWebSocketClient.send(endString);
                        sendEmptyMessageDelayed(DELAY_REQUEST_CODE, 3000);
                    } catch (Exception e) {
                        activity.mWebSocketClient = null;
                        activity.initSockect();
                        sendEmptyMessageDelayed(DELAY_REQUEST_CODE, 3000);
                    }
                    break;
                case DELAY_REQUEST_CODE:
                    activity.locationService.start();
                    break;
                case LOGIN_HX:
                    requstLoginCount++;
                    if (requstLoginCount > REQUE_LOGINHX_MAX_COUNT) {
                        ToaskUtil.showToast(activity, "无法连接服务器,请检查网络");
                        requstLoginCount = 0;
                        activity.finish();
                        return;
                    }
                    activity.loginHx();
                    break;
            }
        }
    }

    /**
     * 异步下载图片的任务。
     *
     * @author guolin
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        /**
         * 图片的URL地址
         */
        private String imageUrl;
        private Point point;

        public BitmapWorkerTask(Point point) {
            this.point = point;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (isCancelled()) {
                return null;
            }
            imageUrl = point.image;
            // 在后台开始下载图片
            Bitmap bitmap = downloadBitmap(imageUrl);
            if (bitmap != null) {
                // 图片下载完成后缓存到LrcCache中
                addBitmapToMemoryCache(imageUrl, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (isCancelled()) {
                return;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            taskCollection.remove(this);
//            setMarkerByNet(point, 1);
        }

        /**
         * 建立HTTP请求，并获取Bitmap对象。
         *
         * @param imageUrl 图片的URL地址
         * @return 解析后的Bitmap对象
         */
        private Bitmap downloadBitmap(String imageUrl) {
            Bitmap bitmap = null;
            HttpURLConnection con = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(10 * 1000);
                bitmap = BitmapFactory.decodeStream(con.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return bitmap;
        }
    }


    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key    LruCache的键，这里传入图片的URL地址。
     * @param bitmap LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        Bitmap bitmap = mMemoryCache.get(key);
        return bitmap;
//        if (bitmap == null) {
//            BitmapWorkerTask task = new BitmapWorkerTask();
//            task.execute(key);
//            return null;
//        } else {
//            return bitmap;
//        }
    }
}
