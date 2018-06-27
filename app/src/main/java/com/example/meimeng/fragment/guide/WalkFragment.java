package com.example.meimeng.fragment.guide;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseFragment;
import com.example.meimeng.service.LocationService;
import com.example.meimeng.util.LoginSharedUilt;

/**
 * 作者：凌涛 on 2018/6/20 14:44
 * 邮箱：771548229@qq..com
 */
public class WalkFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "WalkFragment";

    private double lat;
    private double lon;
    private BaiduMap mBaiduMap;
    private LocationService locationService;
    private MapView mMapView;

    private WalkNavigateHelper mWNaviHelper;//步行
    WalkNaviLaunchParam walkParam;//步行导航

    private LatLng endPt;

    public void setEndPt(LatLng endPt) {
        this.endPt = endPt;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walk, container, false);
        initView(view);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        locationService.start();
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", "西二旗地铁站");
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", "百度科技园");

    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(myListener); //注销掉监听
        locationService.stop(); //停止定位服务
        mMapView.onDestroy();
    }

    private void initView(View view) {

        locationService = APP.getInstance().locationService;
        locationService.registerListener(myListener);
        LoginSharedUilt intance = LoginSharedUilt.getIntance(getContext());
        lat = intance.getLat();
        lon = intance.getLon();
        LatLng startPt = new LatLng(lat, lon);
        view.findViewById(R.id.begin).setOnClickListener(this);
        mMapView = ((MapView) view.findViewById(R.id.bmapView));
        initMapStatus(startPt);

        try {
            mWNaviHelper = WalkNavigateHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        double latitude = endPt.latitude;
        double longitude = endPt.longitude;
        Log.d("initView", "initView: " + latitude + "," + longitude);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.begin:
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
            String addr = location.getAddrStr();    //获取详细地址信息
            String city = location.getCity();
            location(city, addr);
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

    public void location(String city,String addr) {
        LoginSharedUilt intance = LoginSharedUilt.getIntance(getContext());
        intance.saveLat(lat);
        intance.saveLon(lon);
        intance.saveCity(city);
        intance.saveAddr(addr);
    }


    private void initMapStatus(LatLng startPt) {
        mBaiduMap = mMapView.getMap();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(startPt).zoom(19);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

}
