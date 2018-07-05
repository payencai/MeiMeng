package com.example.meimeng.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
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
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AEDInfo;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.bean.UavAedBaen;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.custom.KyLoadingBuilder;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.overlayutil.TransitRouteOverlay;
import com.example.meimeng.overlayutil.WalkingRouteOverlay;
import com.example.meimeng.service.LocationService;
import com.example.meimeng.test.RoutePlanDemo;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowAEDActivity extends BaseActivity implements OnGetRoutePlanResultListener, View.OnClickListener {

    private LocationService locationService;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private double lat;
    private double lon;

    private double endLat;
    private double endLon;

    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    private TextView mTitle;
    private TextView mAddress;
    private TextView phoneNumber;
    private TextView mDistanceText;
    private ImageView calUAV;
    private TextView AEDText;
    private TextView uavText;
    private View view1;
    private View view2;
    private TextView beginNavigation;
    private TextView beginNavigation2;
    private LinearLayout aedLayout;
    private LinearLayout uavLayout;
    private KyLoadingBuilder mOpenLoadView;
    private TextView uavAddress;
    private TextView uavPhoneNumber;
    private TextView uavDistance;


    public static void startShowAED(Context context, double lat, double lon) {
        Intent intent = new Intent(context, ShowAEDActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        context.startActivity(intent);
    }


    @Override
    protected int getContentId() {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        locationService = APP.getInstance().locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，
        // 然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(myListener);
        return R.layout.activity_show_aed;
    }

    private void selectState(int type) {

        if (type == 0) {//AED状态
            AEDText.setTextColor(Color.parseColor("#e1181e"));
            view1.setBackgroundColor(Color.parseColor("#e1181e"));
            uavText.setTextColor(Color.parseColor("#b7b7b7"));
            view2.setBackgroundColor(Color.parseColor("#00b7b7b7"));
            calUAV.setVisibility(View.GONE);
            aedLayout.setVisibility(View.VISIBLE);
            uavLayout.setVisibility(View.GONE);

        } else {//无人机状态
            AEDText.setTextColor(Color.parseColor("#b7b7b7"));
            view1.setBackgroundColor(Color.parseColor("#00b7b7b7"));
            uavText.setTextColor(Color.parseColor("#e1181e"));
            view2.setBackgroundColor(Color.parseColor("#e1181e"));
            calUAV.setVisibility(View.VISIBLE);
            aedLayout.setVisibility(View.GONE);
            uavLayout.setVisibility(View.VISIBLE);


        }

    }

    @Override
    protected void initView() {

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0);
        lon = intent.getDoubleExtra("lon", 0);

        if (lat == 0 || lon == 0) {
            ToaskUtil.showToast(this, "未定位成功");
            finish();
            return;
        }
        locationService.start();
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        findViewById(R.id.back).setOnClickListener(this);
        beginNavigation = (TextView) findViewById(R.id.beginNavigation);
        beginNavigation2 = (TextView) findViewById(R.id.beginNavigation2);
        mTitle = (TextView) findViewById(R.id.title);
        mAddress = (TextView) findViewById(R.id.address);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        mDistanceText = (TextView) findViewById(R.id.distance);

        uavAddress = (TextView) findViewById(R.id.uavAddress);
        uavPhoneNumber = (TextView) findViewById(R.id.uavPhoneNumber);
        uavDistance = (TextView) findViewById(R.id.uavDistance);

        findViewById(R.id.AEDOption).setOnClickListener(this);
        findViewById(R.id.UAVOption).setOnClickListener(this);
        aedLayout = (LinearLayout) findViewById(R.id.aedLayout);
        uavLayout = (LinearLayout) findViewById(R.id.uavLayout);

        calUAV = (ImageView) findViewById(R.id.calUAV);
        calUAV.setOnClickListener(this);
        mTitle.setText("AED");

        AEDText = (TextView) findViewById(R.id.AEDText);
        uavText = (TextView) findViewById(R.id.uavText);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);

        //开启定位图层
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMarkerClickListener(onMarkerClicklistener);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        beginNavigation.setOnClickListener(this);
        beginNavigation2.setOnClickListener(this);

        setMarker();
        setUserMapCenter();
        getAEDController();
        selectState(0);

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
        locationService.unregisterListener(myListener); //注销掉监听
        locationService.stop(); //停止定位服务
        if (mSearch != null) {
            mSearch.destroy();
        }
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
//            locationcity = location.getCity();
//            locationprovince = location.getProvince();
//            String addr = location.getAddrStr();    //获取详细地址信息
//            String country = location.getCountry();    //获取国家
//            String province = location.getProvince();    //获取省份
//            String city = location.getCity();    //获取城市
//            String district = location.getDistrict();    //获取区县
//            String street = location.getStreet();    //获取街道信息
//            int LocType = location.getLocType();    //返回码
//            lat = location.getLatitude();
//            lon = location.getLongitude();
//            Log.d("onReceiveLocation", "onReceiveLocation: 定位");
//            location(city, addr);
//            locationService.setLocationOption(locationService.getSingleLocationClientOption());
        }


    };

    private void getAEDController() {
        String token;
        if (APP.sUserType == 0) {
            token = APP.getInstance().getUserInfo().getToken();
        } else {
            token = APP.getInstance().getServerUserInfo().getToken();
        }
        if (TextUtils.isEmpty(token)) {
            return;
        }
        Map<String, Object> paramr = new HashMap<>();
        paramr.put("longitude", lon);//经度
        paramr.put("latitude", lat);//维度
        HttpProxy.obtain().get(PlatformContans.AedController.sGetAed, paramr, token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                MLog.log("getAEDController", result);
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    String message = object.getString("message");
                    if (resultCode == 0) {
                        JSONArray data = object.getJSONArray("data");
                        int length = data.length();
                        Gson gson = new Gson();
                        List<AEDInfo> list = new ArrayList<>();
                        for (int i = 0; i < length; i++) {
                            JSONObject item = data.getJSONObject(i);
                            AEDInfo serverUser = gson.fromJson(item.toString(), AEDInfo.class);
                            list.add(serverUser);
                        }
//                        mBaiduMap.clear();//清除地图上所有覆盖物，无法分成批删除
                        batchAddAED(list);
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

    private void batchAddAED(List<AEDInfo> list) {
        List<OverlayOptions> options = new ArrayList<OverlayOptions>();
        //构建Marker图标
        //当前自己的位置
        LatLng pointcur = new LatLng(lat, lon);
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
            int distance = (int) DistanceUtil.getDistance(pointcur, point);//距离定位的距离
            Marker marker = (Marker) mBaiduMap.addOverlay(option);
            String address = key.getAddress();//广州新东方学校大学城教学区广州市番禺区大学城中六路1号广州大学城信息枢纽楼814房
            String expiryDate = key.getExpiryDate();//2018-05-23
            String brank = key.getBrank();//回家你那边
            Bundle bundle = new Bundle();
            bundle.putInt("type", 0);//0为AED 1为志愿者覆盖物
            bundle.putInt("distance", distance);
            bundle.putString("telephone", key.getTel());
            bundle.putDouble("latNumber", latNumber);
            bundle.putDouble("lonNumber", lonNumber);

            bundle.putString("address", address);
            bundle.putString("expiryDate", expiryDate);
            bundle.putString("brank", brank);
            marker.setExtraInfo(bundle);
        }
    }

    private void requestUAV() {
        ServerUserInfo userInfo = APP.getInstance().getServerUserInfo();
        if (userInfo == null) {
            ToaskUtil.showToast(this, "登录异常");
            ActivityManager.getInstance().finishAllActivity();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("longitude", lon);
        params.put("latitude", lat);
        String token = userInfo.getToken();
        HttpProxy.obtain().get(PlatformContans.AedController.sGetDrone, params, token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                MLog.log("sGetDrone", result);
                if (mOpenLoadView != null) {
                    mOpenLoadView.dismiss();
                }
                calUAV.setEnabled(true);
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    ToaskUtil.showToast(ShowAEDActivity.this, "无人机呼叫成功,请在5-10分钟内到达最近的AED站点");
                    if (resultCode == 0) {
                        JSONObject data = object.getJSONObject("data");
                        Gson gson = new Gson();
                        UavAedBaen baen = gson.fromJson(data.toString(), UavAedBaen.class);
                        if (baen != null) {
                            uavAddress.setText(baen.getParking());
                            uavPhoneNumber.setText("电话号码 : " + baen.getTel());
                            int distanceNum = 0;
                            try {
                                double lat2 = Double.parseDouble(baen.getLatitude());
                                double lon2 = Double.parseDouble(baen.getLongitude());
                                LatLng pointcur = new LatLng(lat, lon);
                                LatLng point = new LatLng(lat2, lon2);
                                distanceNum = (int) DistanceUtil.getDistance(pointcur, point);//距离定位的距离
                                endLat = lat2;
                                endLon = lon2;
                                walkProject(pointcur, point);
                            } catch (Exception e) {

                            }
                            uavDistance.setText("无人机距离" + distanceNum + "米");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                calUAV.setEnabled(true);
                if (mOpenLoadView != null) {
                    mOpenLoadView.dismiss();
                }
                ToaskUtil.showToast(ShowAEDActivity.this, "请检查网络");
            }
        });
    }


    BaiduMap.OnMarkerClickListener onMarkerClicklistener = new BaiduMap.OnMarkerClickListener() {
        /**
         * 地图 Marker 覆盖物点击事件监听函数
         * @param marker 被点击的 marker
         */
        @Override
        public boolean onMarkerClick(final Marker marker) {
            Bundle bundle = marker.getExtraInfo();
            int type = bundle.getInt("type");
            int distance = bundle.getInt("distance");
            String telephone = bundle.getString("telephone");
            double lonNumber = bundle.getDouble("lonNumber");
            double latNumber = bundle.getDouble("latNumber");
            if (type == 0) {//AED
                LatLng start = new LatLng(lat, lon);
                LatLng end = new LatLng(latNumber, lonNumber);
                endLat = latNumber;
                endLon = lonNumber;
                walkProject(start, end);
                String address = bundle.getString("address");
                String expiryDate = bundle.getString("expiryDate");
                String brank = bundle.getString("brank");
                View view = LayoutInflater.from(ShowAEDActivity.this).inflate(R.layout.marker_aed_info_layout, null);
                TextView brankText = (TextView) view.findViewById(R.id.brank);
                TextView expiryDateText = (TextView) view.findViewById(R.id.expiryDate);
                brankText.setText(brank);

                mAddress.setText(address);
                phoneNumber.setText("电话号码 " + telephone);
                mDistanceText.setText("AED距离" + distance + "米");
                expiryDateText.setText("电池有效期限:" + expiryDate);

                InfoWindow.OnInfoWindowClickListener listener = null;
                listener = new InfoWindow.OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
                        LatLng ll = marker.getPosition();
                        marker.setPosition(ll);
                        mBaiduMap.hideInfoWindow();
                    }
                };
                LatLng ll = marker.getPosition();
                InfoWindow infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(view), ll, -47, listener);
                mBaiduMap.showInfoWindow(infoWindow);

            } else {//志愿者
                //创建InfoWindow展示的view
                View view = LayoutInflater.from(ShowAEDActivity.this).inflate(R.layout.marker_service_info_layout, null);
                String disString = "该志愿者距离您" + distance + "米";
                String telString = "志愿者：134****7692";
                try {
                    String startStr = telephone.substring(0, 3);
                    String endStr = telephone.substring(telephone.length() - 4, telephone.length());
                    String showString = startStr + "****" + endStr;
                    telString = "志愿者：" + showString;
                } catch (Exception e) {

                }

                TextView markerTel = (TextView) view.findViewById(R.id.markerTel);
                TextView markerDistance = (TextView) view.findViewById(R.id.markerDistance);
                markerDistance.setText(disString);
                markerTel.setText(telString);
                //定义用于显示该InfoWindow的坐标点
//            LatLng pt = new LatLng(latNumber, lonNumber);
                InfoWindow.OnInfoWindowClickListener listener = null;
                listener = new InfoWindow.OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
                        LatLng ll = marker.getPosition();
                        marker.setPosition(ll);
                        mBaiduMap.hideInfoWindow();
                    }
                };
                LatLng ll = marker.getPosition();
                InfoWindow infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(view), ll, -47, listener);
                mBaiduMap.showInfoWindow(infoWindow);
            }
            return true;

        }
    };

    private void navigation() {
        if (lon == 0 || lat == 0 || endLat == 0 || endLon == 0) {
            ToaskUtil.showToast(this, "位置获取失败!");
            return;
        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.putExtra("bundle", bundle);
        bundle.putDouble("latNumber", endLat);
        bundle.putDouble("lonNumber", endLon);
        bundle.putDouble("startLat", lat);
        bundle.putDouble("startLon", lon);
        intent.setClass(this, RoutePlanDemo.class);
        startActivity(intent);
    }

    private void walkProject(LatLng point, LatLng point2) {
        mBaiduMap.clear();
        setMarker();
        PlanNode stNode = PlanNode.withLocation(point);
        PlanNode enNode = PlanNode.withLocation(point2);
        mSearch.walkingSearch((new WalkingRoutePlanOption())
                .from(stNode).to(enNode));
        getAEDController();
    }

    private void location(String city, String addr) {

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
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(ShowAEDActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(ShowAEDActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.beginNavigation:
            case R.id.beginNavigation2:
                navigation();
                break;
            case R.id.calUAV:
                mOpenLoadView = openLoadView("");
                calUAV.setEnabled(false);
                requestUAV();
                break;
            case R.id.AEDOption:
                selectState(0);
                break;
            case R.id.UAVOption:
                selectState(1);
                break;
        }
    }
}
