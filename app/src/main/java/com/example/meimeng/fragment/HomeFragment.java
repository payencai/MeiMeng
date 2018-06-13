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

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
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
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.activity.AddAEDActivity;
import com.example.meimeng.activity.SearchActivity;
import com.example.meimeng.activity.SystemMsgActivity;
import com.example.meimeng.base.BaseFragment;
import com.example.meimeng.service.LocationService;
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

    private LocationService locationService;
    public LocationClient mLocationClient = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        locationService = ((APP) getActivity().getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，
        // 然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(myListener);
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
                locationService.start();
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
        locationService.unregisterListener(myListener); //注销掉监听
        locationService.stop(); //停止定位服务
        mMapView.onDestroy();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    /**
     * 显示请求字符串
     *
     * @param str
     */
    public void logMsg(String str) {
        final String s = str;
        ToaskUtil.showToast(getContext(), s);
    }


    /**
     * @param bdLocation 百度定位监听
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            int LocType = location.getLocType();    //返回码


            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                logMsg(sb.toString());
            }
        }

    };


}
