package com.example.meimeng.activity;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.fragment.ContentFragment;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.ToaskUtil;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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

    private Fragment mContentFragment;
    private FragmentManager fm;
    private static final String TAG_FRAGMENT = "content_fragment";
    private boolean isShowFragment = true;
    //ws://127.0.0.1:8080/memen/websocket
    //ws://47.106.164.34:8080/memen/websocket
    private int count = 0;//救援人数

    @Override
    protected int getContentId() {
        return R.layout.activity_wait_salvation;
    }


    @Override
    protected void initView() {
        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        lon = intance.getLon();
        lat = intance.getLat();

        cancel = (TextView) findViewById(R.id.cancel);
        showContent = (ImageView) findViewById(R.id.showContent);
        hideContent = (ImageView) findViewById(R.id.hideContent);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        //开启定位图层
        mBaiduMap = mMapView.getMap();
        fm = getSupportFragmentManager();

        cancel.setOnClickListener(this);
        showContent.setOnClickListener(this);
        hideContent.setOnClickListener(this);

        if (lon != 0 && lat != 0) {
            setMarker();
            setUserMapCenter();
        }
        if (mContentFragment == null) {
            mContentFragment = new ContentFragment();
        }
        fm.beginTransaction().add(R.id.contentContainer, mContentFragment).commit();
        createLongConnect();
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
    private void setMarker() {
        //定义Maker坐标点
        LatLng point = new LatLng(lat, lon);
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
                finish();
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
        if (isShowFragment) {
            hideFragment();
            return;
        }
        super.onBackPressed();
    }

    private String address = "ws://47.106.164.34:80/memen/websocket";
    private String PORT = "80";

    private void createLongConnect() {
        AsyncHttpClient.getDefaultInstance().websocket(
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
                                Log.d(TAG, "onStringAvailable: " + s);
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
            json.put("rescuepersonnum", count);
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

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }


}
