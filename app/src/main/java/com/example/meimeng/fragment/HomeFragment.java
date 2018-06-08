package com.example.meimeng.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.example.meimeng.R;
import com.example.meimeng.activity.AddAEDActivity;
import com.example.meimeng.activity.SearchActivity;
import com.example.meimeng.activity.SystemMsgActivity;
import com.example.meimeng.base.BaseFragment;
import com.example.meimeng.util.ToaskUtil;

public class HomeFragment extends BaseFragment implements View.OnClickListener, BDLocationListener {


    private LinearLayout addAED;
    private LinearLayout firstAidSite;
    private LinearLayout positioning;
    private PopupWindow mTypeSelectPw;

    private LinearLayout searchTypeSelect;
    private TextView selectorText;
    private TextView commonFont;
    private ImageView ring;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private static final int BAIDU_READ_PHONE_STATE = 100;
    private int searchType = 0;//搜索类型，0为城市搜索，1为药品搜索

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        return view;
    }

    private void initView(View view) {
        addAED = (LinearLayout) view.findViewById(R.id.addAED);
        view.findViewById(R.id.searchBar).setOnClickListener(this);
        firstAidSite = (LinearLayout) view.findViewById(R.id.firstAidSite);
        positioning = (LinearLayout) view.findViewById(R.id.positioning);
        searchTypeSelect = (LinearLayout) view.findViewById(R.id.searchTypeSelect);
        selectorText = (TextView) view.findViewById(R.id.selectorText);
        commonFont = (TextView) view.findViewById(R.id.searchHint);
        ring = (ImageView) view.findViewById(R.id.ring);
        //获取地图控件引用
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        //开启定位图层
        mBaiduMap = mMapView.getMap();


        ring.setOnClickListener(this);
        addAED.setOnClickListener(this);
        firstAidSite.setOnClickListener(this);
        positioning.setOnClickListener(this);
        searchTypeSelect.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addAED:
                getActivity().startActivity(new Intent(getContext(), AddAEDActivity.class));
                break;
            case R.id.firstAidSite:
                ToaskUtil.showToast(getActivity(), "急救站");
                break;
            case R.id.positioning:
                showContacts();
                break;
            case R.id.searchTypeSelect:
                showTypeSelect(v);
                break;
            case R.id.ring:
                startActivity(new Intent(getContext(), SystemMsgActivity.class));
                break;
            case R.id.searchBar:
                SearchActivity.startSearchActivity(getContext(), searchType);
                break;
        }
    }

    private void showTypeSelect(View view) {
        if (mTypeSelectPw != null) {
            mTypeSelectPw = null;
        }

        View showView = LayoutInflater.from(getContext()).inflate(R.layout.pw_home_type_select_layout, null);
        handleView(showView);
        mTypeSelectPw = new PopupWindow(showView,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 94, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics()));
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
//        );
        mTypeSelectPw.setFocusable(true);
        mTypeSelectPw.setBackgroundDrawable(new BitmapDrawable());
        mTypeSelectPw.setOutsideTouchable(true);
        mTypeSelectPw.showAsDropDown(view, -57, 15);

    }

    private void handleView(View view) {

        view.findViewById(R.id.citySelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTypeSelectPw.dismiss();
                selectorText.setText("城市");
                commonFont.setText("输入城市名");
                searchType = 0;
            }
        });
        view.findViewById(R.id.drugSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTypeSelectPw.dismiss();
                selectorText.setText("药品");
                commonFont.setText("输入药品名");
                searchType = 1;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();

    }

    private void location() {
        LocationClient client = new LocationClient(getActivity().getApplicationContext());
        //注册监听
        client.registerLocationListener(new BDLocationListener() {
            //当定位成功时回调该方法
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                Log.d("google.sang", "onReceiveLocation: " + bdLocation.getLocType());
                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                    Toast.makeText(getContext(), "Gps", Toast.LENGTH_SHORT).show();
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                    Toast.makeText(getContext(), "网络定位", Toast.LENGTH_SHORT).show();
                } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {
                    Toast.makeText(getContext(), "离线定位", Toast.LENGTH_SHORT).show();
                }

                //坐标转化
                CoordinateConverter converter = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.GPS);
                // sourceLatLng待转换坐标
                converter.coord(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                LatLng desLatLng = converter.convert();
                showLocation(desLatLng);
            }
        });
        //开始定位
        client.start();
    }

    private void showLocation(LatLng desLatLng) {
        //获取定位结果中的纬度
//        double latitude = bdLocation.getLatitude();
//        double longitude = bdLocation.getLongitude();

        //构造一个MyLocationData
        MyLocationData myLocationData = new MyLocationData.Builder()
                .latitude(desLatLng.latitude).longitude(desLatLng.longitude).build();
        mBaiduMap.setMyLocationData(myLocationData);
        MapStatus mapStatus = new MapStatus.Builder()
                .zoom(18f)
                //手机屏幕正中央显示的点
                .target(desLatLng)
                .build();
        //通过动画让地图滑动到当前定位结果处
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.mark_pic);
        OverlayOptions overlay = new MarkerOptions()
                //设置覆盖物的图标
                .icon(bitmap)
                //设置覆盖物的位置
                .position(desLatLng);
        mBaiduMap.addOverlay(overlay);
    }

    public void showContacts() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext().getApplicationContext(), "没有权限,请手动开启定位权限", Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE},
                    BAIDU_READ_PHONE_STATE);
        } else {
            location();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    location();
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(getContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    /**
     * @param bdLocation 百度定位监听
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

    }
}
