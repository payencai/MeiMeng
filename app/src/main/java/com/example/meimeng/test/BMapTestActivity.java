package com.example.meimeng.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.example.meimeng.R;

public class BMapTestActivity extends AppCompatActivity {

    private MapView baiduMapView;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmap_test);
        baiduMapView = (MapView) findViewById(R.id.baiduMap);
        mBaiduMap = baiduMapView.getMap();
    }

    public void location(View view) {
//        mBaiduMap.setMyLocationEnabled(true);
//        new MyLocationData.Builder()
    }
}
