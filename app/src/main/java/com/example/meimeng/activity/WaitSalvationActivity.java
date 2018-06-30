package com.example.meimeng.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

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
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.bean.Point;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.fragment.ContentFragment;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.mywebsocket.WsManager;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.ToaskUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

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
import java.util.Locale;
import java.util.Map;

public class WaitSalvationActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "WaitSalvationActivity";
    private TextView cancel;
    private PopupWindow mFinishPw;
    private ImageView showContent;
    private ImageView hideContent;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    private double lon;
    private double lat;

    private LruCache<String, Bitmap> lruCache;

    private Fragment mContentFragment;
    private FragmentManager fm;
    private static final String TAG_FRAGMENT = "content_fragment";
    private boolean isShowFragment = true;
    //ws://127.0.0.1:8080/memen/websocket
    //ws://47.106.164.34:8080/memen/websocket
    private int count = 0;//救援人数
    private static final int UPDATA_OVERLAY = 1 << 27;
    private static final int UPDATA_WAIT_TIME = 1;
    private static final int LOGIN_HX = 1 << 7;
    public static final int REQUE_LOGINHX_MAX_COUNT = 3;//请求登录环信的最大次数
    private MyHandler mHandler = new MyHandler(this);

    private Map<String, Point> mLocationMap = new HashMap<>();
    private TextView helpNumber;
    private TextView waitTime;
    //等待救援的时间
    private int mWaitTimeNumber = 0;
    private Future<WebSocket> mFuture;
    private WebSocketClient mWebSocketClient;

    @Override
    protected int getContentId() {
        return R.layout.activity_wait_salvation;
    }

    @Override
    protected void initView() {
        WsManager.getInstance().init();
        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        lon = intance.getLon();
        lat = intance.getLat();
        mWaitTimeNumber = intance.getHelpTime();
        loginHx();

        //获取系统给每一个应用所分配的内存大小
        long maxMemory = Runtime.getRuntime().maxMemory();
        int maxSize = (int) (maxMemory / 8);
        //参数表示LruCache中可缓存的图片总大小
        lruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //返回图片的大小，默认返回图片的数量
                return value.getByteCount();
            }
        };

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
        if (mContentFragment == null) {
            mContentFragment = new ContentFragment();
        }
        fm.beginTransaction().add(R.id.contentContainer, mContentFragment).commit();
//        createLongConnect();
        initSockect();
        mHandler.sendEmptyMessage(UPDATA_WAIT_TIME);
    }

    private void hideFragment() {
        isShowFragment = false;
        showContent.setVisibility(View.VISIBLE);
        hideContent.setVisibility(View.GONE);
        fm.beginTransaction().hide(mContentFragment).commit();
    }

    private void showFragment() {
        isShowFragment = true;
        showContent.setVisibility(View.GONE);
        hideContent.setVisibility(View.VISIBLE);
        fm.beginTransaction().show(mContentFragment).commit();
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
        mMapView.onDestroy();
        Log.d("onDestroy", "onDestroy: 还结束不了了？");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                setBackgroundDrakValue(0.75f);
                showPopupW(v);
                break;
            case R.id.popCancel:
                mFinishPw.dismiss();
                break;
            case R.id.popConfirm:
                mFinishPw.dismiss();
                String token = APP.getInstance().getUserInfo().getToken();
                LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
                String helpId = intance.getHelpId();
                cancelHelp(token, helpId);
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
        boolean isCanBack = intance.getIsCanBack();
//        if (!isCanBack) {
        if (mLocationMap.size() > 0) {
            ToaskUtil.showToast(this, "已有救援人员赶过来，不可退出");
            return;
        } else {
            String token = APP.getInstance().getUserInfo().getToken();
            String helpId = intance.getHelpId();
            cancelHelp(token, helpId);
        }
//        intance.saveHelpTime(mWaitTimeNumber);
    }


    private String address = "ws://47.106.164.34:80/memen/websocket";
    private String PORT = "80";

    private void createLongConnect() {
        // webSocket地址
        // 端口
        // 发送消息的方法
        // note that this data has been read
        mFuture = AsyncHttpClient.getDefaultInstance().websocket(
                address,// webSocket地址
                PORT,// 端口
                new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }
                        String jsonSrting = getJsonSrting();
                        Log.d(TAG, "onCompleted: " + jsonSrting);
                        webSocket.send(jsonSrting);// 发送消息的方法
                        webSocket.send(new byte[10]);
                        webSocket.setStringCallback(new WebSocket.StringCallback() {
                            public void onStringAvailable(String s) {
                                Log.d("onStringAvailable", "onStringAvailable: " + s);
                                disposeWebData(s);
                            }
                        });
                        webSocket.setDataCallback(new DataCallback() {
                            public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                                Log.d(TAG, "onDataAvailable: I got some bytes!");
                                // note that this data has been read
                                byteBufferList.recycle();
                            }
                        });
                    }
                });
    }

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
                    String  jsonSrting = getJsonSrting();
                    mWebSocketClient.send(jsonSrting);
                }

                @Override
                public void onMessage(String s) {
                    Log.i(TAG, "onMessage: " + s);
                    disposeWebData(s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i(TAG, "onClose: ");
                }

                @Override
                public void onError(Exception e) {
                    Log.i(TAG, "onError: ");
                }
            };
        }
        mWebSocketClient.connect();
    }

    private void closeLongConnect() {
//        AsyncHttpClient.getDefaultInstance()
        if (mFuture != null) {
            mFuture.cancel(true);
        }
    }

    private void disposeWebData(String s) {
        try {
            JSONObject object = new JSONObject(s);
            String userId = object.getString("userId");
            String image = object.getString("image");
            String imageKey = object.getString("imageKey");
            double longitude = object.getDouble("longitude");
            double latitude = object.getDouble("latitude");
            if (object.has("end")) {
                int end = object.getInt("end");
                if (end == 1) {
                    completeHelp();
                    return;
                }
            }
            mLocationMap.put(userId, new Point(longitude, latitude, image, userId));
            mHandler.sendEmptyMessage(UPDATA_OVERLAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getJsonSrting() {
        String result = null;
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
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
                        completeHelp();
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
                    Log.d("handleMessage", "handleMessage: 更新覆盖物");
                    Map<String, Point> locationMap = activity.mLocationMap;
                    activity.mBaiduMap.clear();
                    Log.d("handleMessage", "handleMessage: " + locationMap.size());
                    LatLng point = new LatLng(activity.lat, activity.lon);
                    activity.setMarker(point);
                    //0名用户正在赶来，
                    activity.helpNumber.setText(locationMap.size() + "名用户正在赶来，");
                    for (String s : locationMap.keySet()) {
                        Point point1 = locationMap.get(s);
                        Log.d("handleMessage", "handleMessage: " + point1.toString());
                        activity.setMarkerByNet(point1);
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
    private void setMarkerByNet(Point point) {
        //定义Maker坐标点
        //构建Marker图标
        LatLng loPoint = new LatLng(point.latitude, point.longitude);
        Log.d("handleMessage", "setMarkerByNet: lat:" + loPoint.latitude + ",lon:" + loPoint.longitude);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_high_volunteer);
//        BitmapDescriptor bitmap1 = BitmapDescriptorFactory.fromBitmap(getBitmap(point.image, point.imageKey));
//        if (bitmap1 == null) {
//            bitmap1 = bitmap;
//        }
        //方法中设置asBitmap可以设置回调类型
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(loPoint)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    public Bitmap getBitmap(String imgUrl, String imgKey) {
        //从内存中加载一张图片
        Bitmap bitmap = lruCache.get(imgKey);
        if (bitmap == null) {
            //从SD卡加载图片出来
            bitmap = getBitmapFromSDCard(imgKey);
            if (bitmap == null) {
                downloadImg(imgUrl, imgKey);
                return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_high_volunteer);
            } else {//从sd卡中有图片，放入缓存
                lruCache.put(imgKey, bitmap);
                return bitmap;
            }
        } else {//存储中有图片，直接显示
            return bitmap;
        }
    }

    private void downloadImg(final String imgUrl, final String imgKey) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con;
                try {
                    URL url = new URL(imgUrl);
                    con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(5 * 1000);
                    con.connect();
                    if (con.getResponseCode() == 200) {
                        Bitmap bitmap = BitmapFactory.decodeStream(con.getInputStream());
                        saveBitmap2SDCard(bitmap, imgKey);
                        lruCache.put(imgKey, bitmap);
//                        Message msg = mHandler.obtainMessage();
//                        msg.obj = bitmap;
//                        mHandler.sendMessage(msg);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void saveBitmap2SDCard(Bitmap bitmap, String imgKey) {
        File externalCacheDir = this.getExternalCacheDir();
        try {
            FileOutputStream fos = new FileOutputStream(new File(externalCacheDir, imgKey));
            if (imgKey.endsWith(".png") || imgKey.endsWith(".PNG")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromSDCard(String imgKey) {
        File file = this.getExternalCacheDir();
        return BitmapFactory.decodeFile(new File(file, imgKey).getAbsolutePath());
    }

    /**
     * 完成救援的数据处理
     */
    private void completeHelp() {
        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        mLocationMap.clear();
        mLocationMap = null;
        try {
            EMClient.getInstance().groupManager().destroyGroup(intance.getGroupId());//需异步处理
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        mHandler.removeCallbacksAndMessages(null);
        mFuture = null;
        ToaskUtil.showToast(this, "已完成救援");
        intance.saveHelpId(null);
        intance.saveHelpTime(0);
        intance.saveGroupId(null);
        intance.saveIsCanBack(true);
        mWebSocketClient.close();
        finish();
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
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("asyncCreateGroup", "登录聊天服务器失败！");
                mHandler.sendEmptyMessageDelayed(LOGIN_HX, 1000);
            }
        });
    }

}
