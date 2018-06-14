package com.example.meimeng.activity;

import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.baidu.mapapi.model.LatLngBounds;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.service.LocationService;
import com.example.meimeng.util.ToaskUtil;

import java.util.ArrayList;
import java.util.List;

public class AddressSelectionActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private LinearLayout searchTypeSelect;
    private LocationService locationService;
    private double lat;
    private double lon;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private RecyclerView siteRv;
    private RVBaseAdapter<AddressBean> mAdapter;

    @Override
    protected void initView() {
        locationService = (APP.getInstance().locationService);
        locationService.registerListener(myListener);

        title = (TextView) findViewById(R.id.title);
        siteRv = (RecyclerView) findViewById(R.id.siteRv);
        mMapView = (MapView) findViewById(R.id.locationMapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapStatusChangeListener(listener);

        title.setText("选择地址");
        searchTypeSelect = (LinearLayout) findViewById(R.id.searchTypeSelect);

        initSiteList();

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.locationImg).setOnClickListener(this);

    }

    private void initSiteList() {

        mAdapter = new RVBaseAdapter<AddressBean>() {
            @Override
            protected void onViewHolderBound(RVBaseViewHolder holder, int position) {

            }
        };
        siteRv.setLayoutManager(new LinearLayoutManager(this));
        siteRv.setAdapter(mAdapter);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_address_selection;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        locationService.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
            case R.id.back:
                finish();
                break;
            case R.id.locationImg:
                locationService.start();
                break;
        }
    }

    /**
     * 添加marker
     */
    private void setMarker() {
        //定义Maker坐标点
        LatLng point = new LatLng(lat, lon);
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

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            location(location);
            locationService.setLocationOption(locationService.getSingleLocationClientOption());
            List<AddressBean> list = new ArrayList<>();
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                String addr = location.getAddrStr();    //获取详细地址信息
                String street = location.getStreet();    //获取街道信息
                AddressBean addressBean = new AddressBean(street, addr);
                list.add(addressBean);
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        String name = poi.getName();
                        AddressBean bean = new AddressBean(name, addr);
                        list.add(bean);
                    }
                }
                mAdapter.reset(list);
            }
        }

    };

    private void location(BDLocation location) {
        //经纬度
        lon = location.getLongitude();//经度
        lat = location.getLatitude();//纬度
        setMarker();
        setUserMapCenter();
    }

    BaiduMap.OnMapStatusChangeListener listener = new BaiduMap.OnMapStatusChangeListener() {
        /**
         * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
         * @param status 地图状态改变开始时的地图状态
         */
        public void onMapStatusChangeStart(MapStatus status) {
        }

        /** 因某种操作导致地图状态开始改变。
         * @param status 地图状态改变开始时的地图状态
         *  reason表示地图状态改变的原因，取值有：
         * 1：用户手势触发导致的地图状态改变,比如双击、拖拽、滑动底图
         * 2：SDK导致的地图状态改变, 比如点击缩放控件、指南针图标
         * 3：开发者调用,导致的地图状态改变
         */
        public void onMapStatusChangeStart(MapStatus status, int reason) {
        }

        /**
         * 地图状态变化中
         * @param status 当前地图状态
         */
        public void onMapStatusChange(MapStatus status) {


        }

        /**
         * 地图状态改变结束
         * @param status 地图状态改变结束后的地图状态
         */
        public void onMapStatusChangeFinish(MapStatus status) {
            LatLng target = status.target;
            double longitude = target.longitude;//经度
            double latitude = target.latitude;//维度


        }
    };


}
