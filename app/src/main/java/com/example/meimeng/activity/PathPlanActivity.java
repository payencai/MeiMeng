package com.example.meimeng.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
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
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.test.BNaviGuideActivity;
import com.example.meimeng.test.TestWNaviGuideActivity;
import com.example.meimeng.util.LoginSharedUilt;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PathPlanActivity extends BaseActivity implements OnGetRoutePlanResultListener {


    @BindView(R.id.drive)
    TextView drive;
    @BindView(R.id.walk)
    TextView walk;
    @BindView(R.id.bus)
    TextView bus;


    private static final String TAG = "PathPlanActivity";
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    private int mNavigationType = -1;//导航类型 0为驾车，1为步行，2为公交
    private double mLatNumber;
    private double mLonNumber;

    private BikeNavigateHelper mNaviHelper;//骑行
    private WalkNavigateHelper mWNaviHelper;//步行
    WalkNaviLaunchParam walkParam;//步行导航
    BikeNaviLaunchParam param;//骑行导航

    private RoutePlanSearch mSearch;

    private static boolean isPermissionRequested = false;
    private double lat;
    private double lon;

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        //开启定位图层
        mBaiduMap = mMapView.getMap();
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        try {
            mWNaviHelper = WalkNavigateHelper.getInstance();
            mNaviHelper = BikeNavigateHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        mLatNumber = bundle.getDouble("latNumber");
        mLonNumber = bundle.getDouble("lonNumber");
        requestPermission();
//
        initFragment();
    }

    private void initFragment() {
        LatLng endPt = new LatLng(mLatNumber, mLonNumber);
        LoginSharedUilt intance = LoginSharedUilt.getIntance(this);
        lat = intance.getLat();
        lon = intance.getLon();
        LatLng startPt = new LatLng(lat, lon);
        if (lat != 0 && lon != 0) {
            setMarker();
            setUserMapCenter();
        }
        walkParam = new WalkNaviLaunchParam().stPt(startPt).endPt(endPt);
        param = new BikeNaviLaunchParam().stPt(startPt).endPt(endPt);

        //显示主页
        resetView(0);
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissions = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (permissions.size() == 0) {
                return;
            } else {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
            }
        }
    }

    private void startWalkNavi() {
        Log.d("View", "startBikeNavi");
        try {
            mWNaviHelper.initNaviEngine(this, new IWEngineInitListener() {
                @Override
                public void engineInitSuccess() {
                    Log.d("View", "engineInitSuccess");
                    routePlanWithWalkParam();
                }

                @Override
                public void engineInitFail() {
                    Log.d("View", "engineInitFail");
                }
            });
        } catch (Exception e) {
            Log.d("Exception", "startBikeNavi");
            e.printStackTrace();
        }
    }

    /**
     * 开始算路
     */
    private void routePlanWithWalkParam() {
        mWNaviHelper.routePlanWithParams(walkParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d(TAG, "开始算路");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d(TAG, "算路成功,跳转至诱导页面");
                Intent intent = new Intent();
                intent.setClass(PathPlanActivity.this, TestWNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError error) {
                Log.d(TAG, "算路失败");
            }

        });
    }


    private void startBikeNavi() {
        Log.d("View", "startBikeNavi");
        try {
            mNaviHelper.initNaviEngine(this, new IBEngineInitListener() {
                @Override
                public void engineInitSuccess() {
                    Log.d("View", "engineInitSuccess");
                    routePlanWithParam();
                }

                @Override
                public void engineInitFail() {
                    Log.d("View", "engineInitFail");
                }
            });
        } catch (Exception e) {
            Log.d("Exception", "startBikeNavi");
            e.printStackTrace();
        }
    }

    private void routePlanWithParam() {
        mNaviHelper.routePlanWithParams(param, new IBRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d("View", "onRoutePlanStart");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d("View", "onRoutePlanSuccess");
                Intent intent = new Intent();
                intent.setClass(PathPlanActivity.this, BNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(BikeRoutePlanError error) {
                Log.d("View", "onRoutePlanFail");
            }

        });
    }


    @Override
    protected int getContentId() {
        return R.layout.activity_path_plan;
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

    @Override
    protected void onResume() {
        super.onResume();
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName("广州", "马场路南");
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName("广州", "冼村路北");
//        mSearch.transitSearch(new TransitRoutePlanOption().from(stNode).to(enNode).city("广州"));
//        mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
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
        mSearch.destroy();
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
//                startBikeNavi();
                break;
            case 1:
                walk.setBackgroundColor(Color.parseColor("#0066ff"));
                walk.setTextColor(Color.parseColor("#ffffff"));
//                startWalkNavi();
                break;
            case 2:
                bus.setBackgroundColor(Color.parseColor("#0066ff"));
                bus.setTextColor(Color.parseColor("#ffffff"));
                break;
        }

    }


    @OnClick({R.id.drive, R.id.walk, R.id.bus, R.id.begin})
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
            case R.id.begin:
                switch (mNavigationType) {
                    case 0:
                        startBikeNavi();
                        break;
                    case 1:
                        startWalkNavi();
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

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
}
