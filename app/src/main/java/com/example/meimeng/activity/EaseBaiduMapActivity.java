package com.example.meimeng.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
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
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.service.LocationService;
import com.example.meimeng.util.ToaskUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class EaseBaiduMapActivity extends BaseActivity {



    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private TextView title;
    private TextView saveText;
    private ImageView back;

    public static void onStartEaseBaiduMap(Context context, double lat, double lon) {
        Intent intent = new Intent(context, EaseBaiduMapActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        context.startActivity(intent);
    }


    @Override
    protected int getContentId() {
        return R.layout.activity_ease_baidu_map;
    }

    @Override
    protected void initView() {
        title = (TextView) findViewById(R.id.title);
        title.setText("位置信息");

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        //开启定位图层
        mBaiduMap = mMapView.getMap();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat", 0);
        double lon = intent.getDoubleExtra("lon", 0);
        if (lat != 0 && lon != 0) {
            LatLng point = new LatLng(lat, lon);
            setMarker(point);
            setUserMapCenter(point);
        } else {
            finish();
            ToaskUtil.showToast(this, "位置获取异常");
            return;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    /**
     * 添加marker
     *
     * @param latLng
     */
    private void setMarker(LatLng latLng) {
        //定义Maker坐标点
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_location);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    /**
     * 设置中心点
     *
     * @param latLng
     */
    private void setUserMapCenter(LatLng latLng) {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(latLng)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

    }

}
