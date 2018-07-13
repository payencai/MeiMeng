package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
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
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.bean.Point;
import com.example.meimeng.bean.ServerUser;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.fragment.HomeFragment;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.mywebsocket.WsManager;
import com.example.meimeng.service.LocationService;
import com.example.meimeng.util.CustomPopWindow;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
//import com.hyphenate.util.LatLng;
import com.hyphenate.exceptions.HyphenateException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class WaitSalvationActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "WaitSalvationActivity";
    private TextView cancel;
    private PopupWindow mFinishPw;
    private ImageView showContent;
    private ImageView hideContent;
    private RelativeLayout parent;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    private double lon;
    private double lat;

    private LruCache<String, Bitmap> mMemoryCache;
    private Set<BitmapWorkerTask> taskCollection;

    //    private Fragment mContentFragment;
    private FragmentManager fm;
    private static final String TAG_FRAGMENT = "content_fragment";
    private boolean isShowFragment = true;
    //ws://127.0.0.1:8080/memen/websocket
    //ws://47.106.164.34:8080/memen/websocket
    private int count = 0;//救援人数
    public static final int REQUE_LOGINHX_MAX_COUNT = 3;//请求登录环信的最大次数
    private static final int UPDATA_WAIT_TIME = 1;
    protected static final int REQUEST_CODE_MAP = 1 << 6;
    private static final int LOGIN_HX = 1 << 7;
    private static final int SEND_LOCATION_CODE = 1 << 8;
    private static final int DELAY_REQUEST_CODE = 1 << 9;
    private static final int UPDATA_OVERLAY = 1 << 27;

    private MyHandler mHandler = new MyHandler(this);

    private Map<String, Point> mLocationMap = new HashMap<>();
    private TextView helpNumber;
    private TextView waitTime;
    //等待救援的时间
    private int mWaitTimeNumber = 0;
    private WebSocketClient mWebSocketClient;
    private String mGroupId;
    private EaseChatFragment mChatFragment;
    private LocationService locationService;

    private int mSalvationState = 0;//救助状态：0为取消求助，1为完成救助,2为救援完成，未退出当前界面
    private MediaPlayer mMediaPlayer;
    //是否登录环信
    private boolean isLoginHx = false;

    private int end = 0;//1所有人救援结束(完成救援) 2单方面结束救援(取消救援)
    private boolean isFristShowEnd = true;//是否是第一次当初结束框

    @Override
    protected int getContentId() {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        locationService = APP.getInstance().locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，
        // 然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(myListener);
        return R.layout.activity_wait_salvation;
    }

    @Override
    protected void initView() {
        WsManager.getInstance().init();
        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        lon = intance.getLon();
        lat = intance.getLat();

        mMediaPlayer = MediaPlayer.create(this, R.raw.hujiu);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.start();
                mMediaPlayer.setLooping(true);
            }
        });
        mMediaPlayer.start();
        mWaitTimeNumber = intance.getHelpTime();
        mGroupId = intance.getGroupId();
        if (TextUtils.isEmpty(mGroupId)) {
            ToaskUtil.showToast(this, "群Id为空");
            completeHelp(true);
            return;
        }
        loginHx();
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
        parent = (RelativeLayout) findViewById(R.id.parent);
        cancel = (TextView) findViewById(R.id.cancel);
        showContent = (ImageView) findViewById(R.id.showContent);
        hideContent = (ImageView) findViewById(R.id.hideContent);
        helpNumber = (TextView) findViewById(R.id.helpNumber);
        waitTime = (TextView) findViewById(R.id.waitTime);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        //开启定位图层
        mBaiduMap = mMapView.getMap();
        fm = getSupportFragmentManager();

        cancel.setOnClickListener(this);
        showContent.setOnClickListener(this);
        hideContent.setOnClickListener(this);

        if (lon != 0 && lat != 0) {
            LatLng point = new LatLng(lat, lon);
            setMarker(point);
            setUserMapCenter();
        }
        locationService.start();
        initSockect();
        mHandler.sendEmptyMessage(UPDATA_WAIT_TIME);
    }

    private void hideFragment() {
        if (mChatFragment == null) {
            return;
        }
        isShowFragment = false;
        showContent.setVisibility(View.VISIBLE);
        hideContent.setVisibility(View.GONE);
        fm.beginTransaction().hide(mChatFragment).commit();
    }

    private void showFragment() {
        if (mChatFragment == null) {
            return;
        }
        isShowFragment = true;
        showContent.setVisibility(View.GONE);
        hideContent.setVisibility(View.VISIBLE);
        fm.beginTransaction().show(mChatFragment).commit();
    }

    /**
     * 添加marker
     */
    private void setMarker(LatLng point) {
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
    private void setUserMapCenter() {
        LatLng cenpt = new LatLng(lat, lon);
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

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer = null;
        mMapView.onDestroy();
        isLoginHx = false;
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        try {
            mWebSocketClient.close();
        } catch (Exception e) {

        }
        locationService.unregisterListener(myListener); //注销掉监听
        locationService.stop(); //停止定位服务
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:

                if (mSalvationState == 0) {
                    String token = APP.getInstance().getUserInfo().getToken();
                    LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
                    String helpId = intance.getHelpId();
                    cancelHelp(token, helpId);
                } else if (mSalvationState == 1) {
                    setBackgroundDrakValue(0.75f);
                    showPopupW(v);
                } else {
                    finish();
                }

                break;
            case R.id.popCancel:
                mFinishPw.dismiss();
                break;
            case R.id.popConfirm:
                mFinishPw.dismiss();
                String token = APP.getInstance().getUserInfo().getToken();
                LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
                String helpId = intance.getHelpId();
                helpEnd("1", helpId);
                break;
            case R.id.showContent:
                showFragment();
                break;
            case R.id.hideContent:
                hideFragment();
                break;
        }
    }

    private void showPopupW(View view) {

        View shareview = LayoutInflater.from(this).inflate(R.layout.popup_salvation_finish, null);
        handlerView(shareview);
        mFinishPw = new PopupWindow(shareview,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()));
        mFinishPw.setFocusable(true);
        mFinishPw.setBackgroundDrawable(new BitmapDrawable());
        mFinishPw.setOutsideTouchable(true);
        mFinishPw.setAnimationStyle(R.style.CustomPopWindowStyle);
        mFinishPw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundDrakValue(1.0f);
            }
        });
        //第一个参数可以取当前页面的任意一个View
        //第二个参数表示pw从哪一个方向显示出来
        //3、4表示pw的偏移量
        mFinishPw.showAtLocation(view, Gravity.CENTER, 0, 0);

    }

    private void handlerView(View view) {

        view.findViewById(R.id.popCancel).setOnClickListener(this);
        view.findViewById(R.id.popConfirm).setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        if (isShowFragment) {
            hideFragment();
            return;
        }
        if (mLocationMap != null) {
            if (mLocationMap.size() > 0) {
                ToaskUtil.showToast(this, "已有救援人员赶过来，不可退出");
                return;
            } else {
                String token = APP.getInstance().getUserInfo().getToken();
                String helpId = intance.getHelpId();
                cancelHelp(token, helpId);
            }
        }
        super.onBackPressed();
//        intance.saveHelpTime(mWaitTimeNumber);
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
                    Log.i(TAG, "onOpen: ");
                    String jsonSrting = getJsonSrting();
                    mWebSocketClient.send(jsonSrting);
                }

                @Override
                public void onMessage(String s) {
                    Log.i(TAG, "onMessage: " + s);
                    if (s.length() < 5) {
                        return;
                    }
                    disposeWebData(s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i(TAG, "onClose: " + s);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onError: ");
                }
            };
        }
        mWebSocketClient.connect();
    }

    private void disposeWebData(String s) {
        try {
            JSONObject object = new JSONObject(s);
            String userId = object.getString("userId");
            String image = object.getString("image");
            String imageKey = object.getString("imageKey");
            double longitude = object.getDouble("longitude");
            double latitude = object.getDouble("latitude");
            mLocationMap.put(userId, new Point(longitude, latitude, image, userId));
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
                                closeRescue(parent, closeName, closeTelephone);
                                completeHelp(false);
                            }
                        }
                    });
                    return;
                } else if (end == 2) {
                    if (mLocationMap.containsKey(userId)) {
                        mLocationMap.remove(userId);
                    }
                }
            }
//            Message message = Message.obtain();
//            message.what = UPDATA_OVERLAY;
//            message.obj = image;
            mHandler.sendEmptyMessage(UPDATA_OVERLAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getJsonSrting() {
        JSONObject json = null;
        UserInfo userInfo = APP.getInstance().getUserInfo();
        if (userInfo == null) {
            ToaskUtil.showToast(this, "登录异常");
            ActivityManager.getInstance().finishAllActivity();
            return "";
        }

        try {
            json = new JSONObject();
            json.put("type", 1);//1求救人员 2救援人员
            json.put("demand_latitude", lat);
            json.put("demand_longitude", lon);
            json.put("toUserId", userInfo.getId());
            json.put("image", userInfo.getImage());
            json.put("imageKey", userInfo.getImageKey());
            json.put("rescuepersonnum", mLocationMap.size());//来救援的人数
            if (end > 0) {
                json.put("end", end);//1所有人救援结束(完成救援) 2单方面结束救援(取消救援)
                json.put("closeUserId", userInfo.getId());//关闭人ID
                json.put("closeUserType", 1);//关闭人类型，1：求救，2：救援
                json.put("closeName", userInfo.getName());
                json.put("closeTelephone", userInfo.getTelephone());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
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
        UserInfo userInfo = APP.getInstance().getUserInfo();
        String token = userInfo.getToken();
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
                        end = 1;
                        completeHelp(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                ToaskUtil.showToast(WaitSalvationActivity.this, "请检查网络");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_MAP) { // location
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
//                ToaskUtil.showToast(this, locationAddress + "经度:" + longitude + ",维度:" + latitude);
                if (locationAddress != null && !locationAddress.equals("")) {
                    if (mChatFragment != null) {
                        mChatFragment.sendLocationMessage(latitude, longitude, locationAddress);
                    }
                } else {
                    Toast.makeText(this, com.hyphenate.easeui.R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 取消求救
     *
     * @param token
     * @param helpId
     */
    private void cancelHelp(String token, String helpId) {
        Map<String, Object> params = new HashMap<>();
        params.put("helpId", helpId);
        HttpProxy.obtain().get(PlatformContans.ForHelp.sUpdateForHelpInfoToCancel, params, token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.d("ForHelpInfoToCancel", "OnSuccess: " + result);
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    String message = object.getString("message");
                    if (resultCode == 0) {
                        ToaskUtil.showToast(WaitSalvationActivity.this, message);
                        end = 1;
                        completeHelp(true);
                    } else {
                        ToaskUtil.showToast(WaitSalvationActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                ToaskUtil.showToast(WaitSalvationActivity.this, "请检查网络");
            }
        });
    }

    public static class MyHandler extends Handler {
        private WeakReference<Activity> activity;
        private int requstLoginCount = 0;

        public MyHandler(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            WaitSalvationActivity activity = (WaitSalvationActivity) this.activity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case UPDATA_OVERLAY:
                    Map<String, Point> locationMap = activity.mLocationMap;
                    activity.mBaiduMap.clear();
                    LatLng point = new LatLng(activity.lat, activity.lon);
                    activity.setMarker(point);
                    //0名用户正在赶来，
                    activity.helpNumber.setText(locationMap.size() + "名用户正在赶来，");
                    activity.mSalvationState = locationMap.size();
                    if (locationMap.size() > 0) {
                        activity.cancel.setText("完成救助");
                    } else {
                        activity.cancel.setText("取消求救");
                    }
                    for (String s : locationMap.keySet()) {
                        Point point1 = locationMap.get(s);
                        Log.d("handleMessage", "handleMessage: " + point1.toString());
                        activity.setMarkerByNet(point1, 0);
                    }
                    break;
                //0名用户正在赶来，等待时间00:00:00
                case UPDATA_WAIT_TIME:
                    int l = activity.mWaitTimeNumber++;
                    activity.waitTime.setText(getFormatHMS(l));
                    sendEmptyMessageDelayed(UPDATA_WAIT_TIME, 1000);
                    break;
                case LOGIN_HX:
                    requstLoginCount++;
                    if (requstLoginCount > REQUE_LOGINHX_MAX_COUNT) {
                        ToaskUtil.showToast(activity, "无法连接服务器,请检查网络");
                        requstLoginCount = 0;
                        return;
                    }
                    activity.loginHx();
                    break;
                case SEND_LOCATION_CODE:
                    String endString = activity.getJsonSrting();
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
            }
        }

        /**
         * 根据毫秒返回时分秒
         *
         * @return
         */
        public static String getFormatHMS(int cnt) {
            //等待时间00:00:00

            int hour = cnt / 3600;
            int min = cnt % 3600 / 60;
            int second = cnt % 60;
//            return String.format("%02d:%02d:%02d", h, m, s);
            return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
        }

    }

    /**
     * 添加marker
     */
    private void setMarkerByNet(Point point, int type) {
        //定义Maker坐标点
        //构建Marker图标
        LatLng loPoint = new LatLng(point.latitude, point.longitude);
        View view = LayoutInflater.from(this).inflate(R.layout.avator_view, null);

        ImageView backgroundSex = (ImageView) view.findViewById(R.id.background_sex);
        ImageView friendHead = (ImageView) view.findViewById(R.id.friend_touxiang);
        backgroundSex.setImageResource(R.drawable.jijiurenyuan3x);

        Bitmap chacheBitmap = getBitmapFromMemoryCache(point.image);
        if (chacheBitmap == null) {
            if (type == 0) {
                BitmapWorkerTask task = new BitmapWorkerTask(point);
                taskCollection.add(task);
                task.execute();
                return;
            } else {
                chacheBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.volunteer1);
                friendHead.setImageBitmap(chacheBitmap);
            }
        } else {
            friendHead.setImageBitmap(chacheBitmap);
        }

        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_high_volunteer);


        Bitmap bitmapView = getViewBitmap(view);
        BitmapDescriptor bitmap3 = BitmapDescriptorFactory.fromBitmap(bitmapView);
        OverlayOptions option = new MarkerOptions()
                .position(loPoint)
                .icon(bitmap3);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    private Bitmap getViewBitmap(View addViewContent) {
        addViewContent.setDrawingCacheEnabled(true);
        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(114, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(138, View.MeasureSpec.EXACTLY));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());
        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        return bitmap;
    }

    private void closeRescue(View view, String closeName, String closeTelephone) {
        View otherView = LayoutInflater.from(this).inflate(R.layout.pw_hint_helpend_layout, null);
        CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(otherView)
                .sizeByPercentage(this, 0.8f, 0f)
                .setOutsideTouchable(true)
                .enableBackgroundDark(true)
                .setAnimationStyle(R.style.CustomPopWindowStyle)
                .setBgDarkAlpha(0.5f)
                .create();
        handlerCloseRescueView(otherView, customPopWindow, closeName, closeTelephone);
        customPopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void handlerCloseRescueView(View view, final CustomPopWindow customPopWindow, String closeName, String closeTelephone) {
        //有救援人:13480197692\n点击完成救助
        TextView showContent = (TextView) view.findViewById(R.id.showContent);
        String content = "有救援人:" + closeName + "\n点击完成救助";
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

    /**
     * 完成救援的数据处理
     */
    private void completeHelp(boolean isFinish) {
        final LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        final String groupId = intance.getGroupId();
        mLocationMap.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().destroyGroup(groupId);//需异步处理
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        mHandler.removeCallbacksAndMessages(null);
        String endString = getJsonSrting();
        try {
            mWebSocketClient.send(endString);
        } catch (Exception e) {
            mWebSocketClient = null;
        }
        ToaskUtil.showToast(this, "已完成救援");
        intance.saveHelpId(null);
        intance.saveHelpTime(0);
        intance.saveGroupId(null);
        intance.saveIsCanBack(true);
        try {
            mWebSocketClient.close();
        } catch (Exception e) {
        }
        if (isFinish) {
            finish();
        }
    }

    private void loginHx() {
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
                Log.d("asyncCreateGroup", "onSuccess: 登录成功");
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initFragment(mGroupId);
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                String replace = message.replace(" ", "");
                Log.d("asyncCreateGroup", "登录聊天服务器失败！" + code + "," + message);
                if (replace.equals("Userisalreadylogin") && code == 200) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            initFragment(mGroupId);
                        }
                    });
                    return;
                }
                mHandler.sendEmptyMessageDelayed(LOGIN_HX, 1000);
            }
        });
    }

    private void initFragment(String userId) {
        UserInfo userInfo = APP.getInstance().getUserInfo();
        String nickname = userInfo.getNickname();
        String image = userInfo.getImage();
        String imageKey = userInfo.getImageKey();
        isLoginHx = true;
        mChatFragment = new EaseChatFragment();
        mChatFragment.setHeadimg(image);
        mChatFragment.setHeadimgKey(imageKey);
        mChatFragment.setNikename(nickname);
        mChatFragment.setHintHead(true);
        mChatFragment.setTransparency(true);
        mChatFragment.setLication(mLication);
        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
        args.putString(EaseConstant.EXTRA_USER_ID, userId);
        mChatFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.contentContainer, mChatFragment).commit();
    }

    private EaseChatFragment.ILaunchLication mLication = new EaseChatFragment.ILaunchLication() {
        @Override
        public void startEaseBaiduMapActivity() {
            startActivityForResult(new Intent(WaitSalvationActivity.this, EaseBaiduMapActivity2.class), REQUEST_CODE_MAP);
        }

        @Override
        public void checkMap(double lat, double lon) {
            EaseBaiduMapActivity.onStartEaseBaiduMap(WaitSalvationActivity.this, lat, lon);
        }
    };

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

            lat = location.getLatitude();
            lon = location.getLongitude();

            LoginSharedUilt intance = LoginSharedUilt.getIntance(WaitSalvationActivity.this);
            intance.saveLat(lat);
            intance.saveLon(lon);
            intance.saveCity(city);
            intance.saveAddr(addr);
            locationService.setLocationOption(locationService.getSingleLocationClientOption());
            location(city, street);
            // TODO Auto-generated method stub
        }
    };

    public void location(String city, String street) {
//        mBaiduMap.clear();
//        setMarker();
//        setMarker2(point2);
//        walkProject(point);
//
//
//        if (isFristMaoCenter) {
//            isFristMaoCenter = false;
//            setUserMapCenter(point);
//        }
        mHandler.sendEmptyMessage(SEND_LOCATION_CODE);
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
            setMarkerByNet(point, 1);
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
