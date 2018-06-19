package com.example.meimeng.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.service.LocationService;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.ToaskUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PathPlanActivity extends BaseActivity {


    @BindView(R.id.drive)
    TextView drive;
    @BindView(R.id.walk)
    TextView walk;
    @BindView(R.id.bus)
    TextView bus;
    @BindView(R.id.bmapView)
    MapView mMapView;

    private static final String TAG = "PathPlanActivity";
    private BaiduMap mBaiduMap;
    private LocationService locationService;
    private double lat;
    private double lon;

    private int mNavigationType = -1;//导航类型
    private WalkNavigateHelper mNaviHelper;
    private WalkNaviLaunchParam mParam;
    private double mLatNumber;
    private double mLonNumber;

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        mLatNumber = bundle.getDouble("latNumber");
        mLonNumber = bundle.getDouble("lonNumber");

        //开启定位图层
        locationService = APP.getInstance().locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，
        // 然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(myListener);
        mBaiduMap = mMapView.getMap();

        LatLng startPt = new LatLng(lat, lon);
        LatLng endPt = new LatLng(mLatNumber, mLonNumber);
        mParam = new WalkNaviLaunchParam().stPt(startPt).endPt(endPt);
        // 获取导航控制类
        mNaviHelper = WalkNavigateHelper.getInstance();
        // 引擎初始化
        mNaviHelper.initNaviEngine(this, new IWEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                Log.d(TAG, "引擎初始化成功");
                routePlanWithParam();
            }

            @Override
            public void engineInitFail() {
                Log.d(TAG, "引擎初始化失败");
            }
        });
//        mNaviHelper.setWalkNaviStatusListener(new IWNaviStatusListener() {
//            @Override
//            public void onWalkNaviModeChange(int mode, WalkNaviModeSwitchListener listener) {
//                Log.d(TAG, "onWalkNaviModeChange : " + mode);
//                mNaviHelper.switchWalkNaviMode(PathPlanActivity.this, mode, listener);
//            }
//
//            @Override
//            public void onNaviExit() {
//                Log.d(TAG, "onNaviExit");
//            }
//        });
    }

    public void routePlanWithParam() {
        mNaviHelper.routePlanWithParams(mParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d(TAG, "开始算路");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d(TAG, "算路成功,跳转至诱导页面");
                Intent intent = new Intent();
                intent.setClass(PathPlanActivity.this, WNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError error) {
                Log.d(TAG, "算路失败");
            }

        });
    }


    @Override
    protected int getContentId() {
        return R.layout.activity_path_plan;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetView(0);
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        locationService.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(myListener); //注销掉监听
        locationService.stop(); //停止定位服务
        mMapView.onDestroy();
    }

    public void location() {
        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        intance.saveLat(lat);
        intance.saveLon(lon);
        setMarker();
        setUserMapCenter();
    }

    /**
     * 添加marker
     */
    private void setMarker() {
        //定义Maker坐标点
        LatLng point = new LatLng(lat, lon);
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

    private void resetView(int type) {
        if (type == mNavigationType) {//如果点击两次，直接返回
            return;
        }
        mNavigationType = type;
        drive.setTextColor(Color.parseColor("#0066ff"));
        walk.setTextColor(Color.parseColor("#0066ff"));
        bus.setTextColor(Color.parseColor("#0066ff"));
        drive.setBackgroundColor(Color.parseColor("#ffffff"));
        walk.setBackgroundColor(Color.parseColor("#ffffff"));
        bus.setBackgroundColor(Color.parseColor("#ffffff"));

        switch (type) {
            case 0:
                drive.setBackgroundColor(Color.parseColor("#0066ff"));
                drive.setTextColor(Color.parseColor("#ffffff"));
                break;
            case 1:
                walk.setBackgroundColor(Color.parseColor("#0066ff"));
                walk.setTextColor(Color.parseColor("#ffffff"));
                break;
            case 2:
                bus.setBackgroundColor(Color.parseColor("#0066ff"));
                bus.setTextColor(Color.parseColor("#ffffff"));
                break;
        }

    }


    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            location();
            locationService.setLocationOption(locationService.getSingleLocationClientOption());
            // TODO Auto-generated method stub
//            String addr = location.getAddrStr();    //获取详细地址信息
//            String country = location.getCountry();    //获取国家
//            String province = location.getProvince();    //获取省份
//            String city = location.getCity();    //获取城市
//            String district = location.getDistrict();    //获取区县
//            String street = location.getStreet();    //获取街道信息
//            int LocType = location.getLocType();    //返回码

        }

    };


    @OnClick({R.id.drive, R.id.walk, R.id.bus})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.drive:
                resetView(0);
                break;
            case R.id.walk:
                resetView(1);
                break;
            case R.id.bus:
                resetView(2);
                break;
        }
    }
}
