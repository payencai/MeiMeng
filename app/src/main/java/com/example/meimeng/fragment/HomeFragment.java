package com.example.meimeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.baidu.location.LocationClient;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
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
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.activity.AddAEDActivity;
import com.example.meimeng.activity.CityPickerActivity;
import com.example.meimeng.activity.SearchActivity;
import com.example.meimeng.activity.SystemMsgActivity;
import com.example.meimeng.activity.VolunteerActivity;
import com.example.meimeng.activity.WNaviGuideActivity;
import com.example.meimeng.base.BaseFragment;
import com.example.meimeng.bean.AEDInfo;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.bean.ServerUser;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.custom.VolunteerView;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.service.LocationService;
import com.example.meimeng.test.RoutePlanDemo;
import com.example.meimeng.util.CustomPopWindow;
import com.example.meimeng.util.LoginSharedUilt;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;
import com.google.gson.Gson;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends BaseFragment implements View.OnClickListener {


    private LinearLayout addAED;
    private LinearLayout firstAidSite;
    private LinearLayout positioning;
    private PopupWindow mTypeSelectPw;

    private LinearLayout searchTypeSelect;
    private TextView selectorText;
    private TextView commonFont;
    private ImageView ring;
    private ImageView volunteerRecruiting;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private static final int BAIDU_READ_PHONE_STATE = 100;
    private int searchType = 1;//搜索类型，0为城市搜索，1为药品搜索

    private LocationService locationService;
    public LocationClient mLocationClient = null;
    private BitmapDescriptor mCurrentMarker;

    private BikeNavigateHelper mNaviHelper;//骑行
    private WalkNavigateHelper mWNaviHelper;//步行
    BikeNaviLaunchParam param;
    WalkNaviLaunchParam walkParam;
    String startNodeStr = "";//起始点
    String mCurrentCity = "";//当前城市

    private GeoCoder mGeoCoderSearch;
    private LruCache<String, Bitmap> mMemoryCache;

    private double lat;
    private double lon;
    private double selectlat;
    private double selectlon;
    private boolean isSelect = false;
    private int count = 0;
    //    private Snackbar snackbar;
    private TextView locationName;

    /**
     * 记录所有正在下载或等待下载的任务。
     */

    private Set<BitmapWorkerTask> taskCollection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        locationService = ((APP) getActivity().getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，
        // 然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(myListener);
        initCache();
        initView(view);
        return view;
    }

    private void initCache() {
        taskCollection = new HashSet<BitmapWorkerTask>();
        //获取系统给每一个应用所分配的内存大小
        long maxMemory = Runtime.getRuntime().maxMemory();
        int maxSize = (int) (maxMemory / 8);
        //参数表示LruCache中可缓存的图片总大小
        mMemoryCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //返回图片的大小，默认返回图片的数量
                return value.getByteCount();
            }
        };
    }

    private void initView(View view) {
        locationName = view.findViewById(R.id.actionBarAreaName);
        addAED = (LinearLayout) view.findViewById(R.id.addAED);
        view.findViewById(R.id.searchBar).setOnClickListener(this);
        firstAidSite = (LinearLayout) view.findViewById(R.id.firstAidSite);
        positioning = (LinearLayout) view.findViewById(R.id.positioning);
        searchTypeSelect = (LinearLayout) view.findViewById(R.id.searchTypeSelect);
        selectorText = (TextView) view.findViewById(R.id.selectorText);
        commonFont = (TextView) view.findViewById(R.id.searchHint);
        ring = (ImageView) view.findViewById(R.id.ring);
        volunteerRecruiting = (ImageView) view.findViewById(R.id.volunteerRecruiting);
        //获取地图控件引用
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mGeoCoderSearch = GeoCoder.newInstance();
        mGeoCoderSearch.setOnGetGeoCodeResultListener(mGeoCoderListener);
        //开启定位图层
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMarkerClickListener(onMarkerClicklistener);
        ring.setOnClickListener(this);
        volunteerRecruiting.setOnClickListener(this);
        addAED.setOnClickListener(this);
        firstAidSite.setOnClickListener(this);
        positioning.setOnClickListener(this);
        searchTypeSelect.setOnClickListener(this);

        locationService.start();
        licationCircle();

    }

    //发起地理编码检索；
    private void reverseGeoCode(LatLng lng) {
        mGeoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption().location(lng));
    }

    OnGetGeoCoderResultListener mGeoCoderListener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
                //没有检索到结果
            }
            String address = result.getAddress();
            Log.d("onGetGeoCodeResult", "onGetGeoCodeResult: " + address);
            //获取地理编码结果

        }

        @Override

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
                return;
            }
            String address1 = result.getAddress();
            //获取反向地理编码结果
            List<PoiInfo> poiList = result.getPoiList();
            List<AddressBean> list = new ArrayList<>();
            if (poiList != null) {
                for (PoiInfo poiInfo : poiList) {
                    String address = poiInfo.address;
                    String name = poiInfo.name;
                    LatLng location = poiInfo.location;
                    double longitude = location.longitude;//经度
                    double latitude = location.latitude;//维度
                    String uid = poiInfo.uid;
                    String province = poiInfo.province;
                    String city = poiInfo.city;
                    String area = poiInfo.area;
                    String street_id = poiInfo.street_id;
                    String phoneNum = poiInfo.phoneNum;
                    String postCode = poiInfo.postCode;
                    AddressBean bean = new AddressBean();
                    bean.setLon(longitude);//经度
                    bean.setLat(latitude);//维度
                    bean.setAddress(address);
                    bean.setName(name);
                    bean.setUid(uid);
                    bean.setProvince(province);
                    bean.setCity(city);
                    bean.setArea(area);
                    bean.setStreet_id(street_id);
                    bean.setPhoneNum(phoneNum);
                    bean.setPostCode(postCode);
                    list.add(bean);
                }

            }
            if (list.size() > 0) {
                AddressBean bean = list.get(0);
                String name = bean.getName();
                startNodeStr = name;
                mCurrentCity = bean.getCity();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        getServerUserByUser();

    }

    private void licationCircle() {
        LoginSharedUilt intance = LoginSharedUilt.getIntance(getContext());
        lat = intance.getLat();
        lon = intance.getLon();
        if (lat != 0 && lon != 0) {
            setMarker();
            setUserMapCenter();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();

    }

    private String locationcity;
    private String locationprovince;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addAED:
                getActivity().startActivity(new Intent(getContext(), AddAEDActivity.class));
                break;
            case R.id.firstAidSite:
                getAEDController();
                break;
            case R.id.positioning:
                locationService.start();
                break;
            case R.id.searchTypeSelect:
                showTypeSelect(v);
                break;
            case R.id.ring:
                startActivity(new Intent(getContext(), SystemMsgActivity.class));
//                closeAEDHint();
                break;
            case R.id.searchBar:
                if (searchType == 0) {
                    //init();
                    Intent intent = new Intent(getActivity(), CityPickerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("city", locationcity);
                    bundle.putString("province", locationprovince);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 5);
                } else
                    SearchActivity.startSearchActivity(getContext(), searchType);
                break;
            case R.id.volunteerRecruiting:
                startActivity(new Intent(getActivity(), VolunteerActivity.class));
//                openAEDLocation(v);
                break;
        }
    }

    private void setSelectMarker() {
        //定义Maker坐标点
        LatLng point = new LatLng(selectlat, selectlon);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5 && data != null) {
            City city = (City) data.getSerializableExtra("city");
            locationName.setText(city.getName());
            Log.e("tag", city.getName());
            selectlat = data.getExtras().getDouble("lat");
            selectlon = data.getExtras().getDouble("lon");
            Log.e("bbb", selectlat + "： " + selectlon);
            setSelectMarker();
            setSelectMapCenter();
            lat = selectlat;
            lon = selectlon;
            //count++;
            //Log.e("tag",count+"");

            getServerUserByUser();
        }

    }

    private void setSelectMapCenter() {
        LatLng cenpt = new LatLng(selectlat, selectlon);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    private void init() {

        List<HotCity> hotCities = new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));
        CityPicker.getInstance()
                .setFragmentManager(getActivity().getSupportFragmentManager())    //此方法必须调用
                .enableAnimation(false)    //启用动画效果
                .setLocatedCity(new LocatedCity("杭州", "浙江", "101210101"))
                .setHotCities(hotCities)
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        // finish();
                        Toast.makeText(getContext(), data.getCode(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLocate() {
                        //开始定位，这里模拟一下定位
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                CityPicker.getInstance()
                                        .locateComplete(new LocatedCity("深圳", "广东", "101280601"), LocateState.SUCCESS);
                            }
                        }, 2000);
                    }
                })
                .show();
    }

    /**
     * 获取自愿者位置信息
     */
    private void getServerUserByUser() {

        for (BitmapWorkerTask task : taskCollection) {
            task.cancel(true);
        }
        taskCollection.clear();
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

        HttpProxy.obtain().get(PlatformContans.UseUser.sGetServerUserByUser, paramr, token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                MLog.log("ServerUserByUser", result);
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    String message = object.getString("message");
                    if (resultCode == 0) {
                        JSONArray data = object.getJSONArray("data");
                        int length = data.length();
                        Gson gson = new Gson();
                        List<ServerUser> list = new ArrayList<>();
                        for (int i = 0; i < length; i++) {
                            JSONObject item = data.getJSONObject(i);
                            ServerUser serverUser = gson.fromJson(item.toString(), ServerUser.class);
                            list.add(serverUser);
                        }
                        batchAddMarker(list);
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
        if (mGeoCoderSearch != null) {
            mGeoCoderSearch.destroy();
        }
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        locationService.unregisterListener(myListener); //注销掉监听
        locationService.stop(); //停止定位服务
        mMapView.onDestroy();
        for (BitmapWorkerTask task : taskCollection) {
            task.cancel(true);
        }
        taskCollection.clear();

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

    public void location(String city, String addr) {
        LoginSharedUilt intance = LoginSharedUilt.getIntance(getContext());
        intance.saveLat(lat);
        intance.saveLon(lon);
        intance.saveCity(city);
        intance.saveAddr(addr);
        reverseGeoCode(new LatLng(lat, lon));
        locationName.setText(city);
        setMarker();
        setUserMapCenter();
        getServerUserByUser();
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
     * @param list 批量添加 覆盖物
     */
    private void batchAddMarker(List<ServerUser> list) {
        mBaiduMap.clear();//清除地图上所有覆盖物，无法分成批删除
        setMarker();//从新设置marker
        LatLng pointcur = new LatLng(lat, lon);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.avator_view, null);
        Bitmap bitmap = getViewBitmap(view);
        BitmapDescriptor bitmap3 = BitmapDescriptorFactory.fromBitmap(bitmap);

        for (ServerUser serverUser : list) {
            addSingleMarker(serverUser, 0);

            //isCertificate 1为高级，2为普通
            //workLongitude  经度
            //workLatitude 维度
//            int isCertificate = serverUser.getIsCertificate();
//            String workLatitude = serverUser.getWorkLatitude();
//            String workLongitude = serverUser.getWorkLongitude();
//            double latNumber = 0;
//            double lonNumber = 0;
//            LatLng point;
//            try {
//                latNumber = Double.parseDouble(workLatitude);
//                lonNumber = Double.parseDouble(workLongitude);
//            } catch (Exception e) {
//
//            }
//            point = new LatLng(latNumber, lonNumber);
//            MarkerOptions position = new MarkerOptions().position(point);
//            OverlayOptions option;
//            if (isCertificate == 1) {
//                option = position.icon(bitmap1);
//            } else {
//                option = position.icon(bitmap3);
//            }
////            options.add(option);
//            int distance = (int) DistanceUtil.getDistance(pointcur, point);
//            Marker marker = (Marker) mBaiduMap.addOverlay(option);
//            Bundle bundle = new Bundle();
//            bundle.putInt("type", 1);//0为AED 1为志愿者覆盖物
//            bundle.putInt("distance", distance);
//            bundle.putString("telephone", serverUser.getAccount());
//            bundle.putDouble("latNumber", latNumber);
//            bundle.putDouble("lonNumber", lonNumber);
//            marker.setExtraInfo(bundle);
        }
    }

    private void addSingleMarker(ServerUser serverUser, int type) {
        LatLng pointcur = new LatLng(lat, lon);
        int isCertificate = serverUser.getIsCertificate();
        String workLatitude = serverUser.getWorkLatitude();
        String workLongitude = serverUser.getWorkLongitude();
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.avator_view, null);
        ImageView backgroundSex = (ImageView) view.findViewById(R.id.background_sex);
        ImageView friendHead = (ImageView) view.findViewById(R.id.friend_touxiang);

        Bitmap chacheBitmap = getBitmapFromMemoryCache(serverUser.getImage());
        if (chacheBitmap == null) {
            if (type == 0) {
                BitmapWorkerTask task = new BitmapWorkerTask(serverUser);
                taskCollection.add(task);
                task.execute();
                return;
            } else {
                if (isCertificate == 1) {
                    chacheBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.volunteer1);
                } else {
                    chacheBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.volunteer2);
                }
                friendHead.setImageBitmap(chacheBitmap);
            }
        } else {
            friendHead.setImageBitmap(chacheBitmap);
        }
        if (isCertificate == 1) {
            backgroundSex.setImageResource(R.drawable.jijiurenyuan3x);
        } else {
            backgroundSex.setImageResource(R.drawable.xiezhurenyuan3x);
        }
        Bitmap bitmap = getViewBitmap(view);
        BitmapDescriptor bitmap3 = BitmapDescriptorFactory.fromBitmap(bitmap);
        option = position.icon(bitmap3);

//            options.add(option);
        int distance = (int) DistanceUtil.getDistance(pointcur, point);
        Marker marker = (Marker) mBaiduMap.addOverlay(option);
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);//0为AED 1为志愿者覆盖物
        bundle.putInt("distance", distance);
        bundle.putString("telephone", serverUser.getAccount());
        bundle.putDouble("latNumber", latNumber);
        bundle.putDouble("lonNumber", lonNumber);
        marker.setExtraInfo(bundle);
    }

    private Bitmap getViewBitmap(View addViewContent) {
        addViewContent.setDrawingCacheEnabled(true);
        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(114, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(138, View.MeasureSpec.EXACTLY));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());
        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        return bitmap;
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

    private void showPwAEDInfo(View view, int distance, String address, String tel, double lonNumber, double latNumber) {
        View brandView = LayoutInflater.from(getContext()).inflate(R.layout.pw_aed_info, null);
        CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(brandView)
                .sizeByPercentage(getContext(), 1f, 0f)
                .setOutsideTouchable(true)
                .enableBackgroundDark(false)
                .setAnimationStyle(R.style.CustomPopWindowStyle)
                .setBgDarkAlpha(0.5f)
                .create();
        handlerBrandView(getContext(), brandView, customPopWindow, distance, address, tel, lonNumber, latNumber);
        customPopWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    private void handlerBrandView(Context context, View view, final CustomPopWindow customPopWindow,
                                  int distance, String address, String tel, final double lonNumber, final double latNumber) {
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
            }
        });
        view.findViewById(R.id.beginNavigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginSharedUilt intance = LoginSharedUilt.getIntance(getContext());
                double lat = intance.getLat();
                double lon = intance.getLon();
                if (lat == 0 || lon == 0) {
                    ToaskUtil.showToast(getContext(), "正在定位中...");
                    return;
                }
                if (TextUtils.isEmpty(startNodeStr) || TextUtils.isEmpty(mCurrentCity)) {
                    ToaskUtil.showToast(getContext(), "正在定位中...");
                    return;
                }

                if (lon == 0 || lat == 0 || latNumber == 0 || lonNumber == 0) {
                    ToaskUtil.showToast(getContext(), "位置获取失败!");
                    return;
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                intent.putExtra("bundle", bundle);
                bundle.putDouble("latNumber", latNumber);
                bundle.putDouble("lonNumber", lonNumber);
                bundle.putString("currentCity", mCurrentCity);
                bundle.putString("startNodeStr", startNodeStr);
                bundle.putDouble("startLat", lat);
                bundle.putDouble("startLon", lon);
                intent.setClass(getActivity(), RoutePlanDemo.class);
                startActivity(intent);
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
            }
        });
        ((TextView) view.findViewById(R.id.distance)).setText("AED距离" + distance + "米");
        ((TextView) view.findViewById(R.id.address)).setText(address);
        TextView telNumber = (TextView) view.findViewById(R.id.phoneNumber);
        if (!TextUtils.isEmpty(tel)) {
            telNumber.setText("电话号码:" + tel);
        }
    }

    private void startWalkNavi() {
        Log.d("View", "startBikeNavi");
        try {
            mWNaviHelper.initNaviEngine(getActivity(), new IWEngineInitListener() {
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

    private void routePlanWithWalkParam() {
        mWNaviHelper.routePlanWithParams(walkParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d("View", "onRoutePlanStart");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d("View", "onRoutePlanSuccess");
                Intent intent = new Intent();
                intent.setClass(getActivity(), WNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError error) {
                Log.d("View", "onRoutePlanFail");
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
                String address = bundle.getString("address");
                String expiryDate = bundle.getString("expiryDate");
                String brank = bundle.getString("brank");
                View view = LayoutInflater.from(getContext()).inflate(R.layout.marker_aed_info_layout, null);
                TextView brankText = (TextView) view.findViewById(R.id.brank);
                TextView expiryDateText = (TextView) view.findViewById(R.id.expiryDate);
                brankText.setText(brank);
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
                showPwAEDInfo(view, distance, address, telephone, lonNumber, latNumber);


            } else {//志愿者
                //创建InfoWindow展示的view
                View view = LayoutInflater.from(getContext()).inflate(R.layout.marker_service_info_layout, null);
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

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            locationcity = location.getCity();
            locationprovince = location.getProvince();
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            int LocType = location.getLocType();    //返回码
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.d("onReceiveLocation", "onReceiveLocation: 定位");
            location(city, addr);
            locationService.setLocationOption(locationService.getSingleLocationClientOption());
            // TODO Auto-generated method stub


//            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
//                StringBuffer sb = new StringBuffer(256);
//                sb.append("time : ");
//                /**
//                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
//                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
//                 */
//                sb.append(location.getTime());
//                sb.append("\nlocType : ");// 定位类型
//                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
//                sb.append("\nlatitude : ");// 纬度
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");// 经度
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");// 半径
//                sb.append(location.getRadius());
//                sb.append("\nCountryCode : ");// 国家码
//                sb.append(location.getCountryCode());
//                sb.append("\nCountry : ");// 国家名称
//                sb.append(location.getCountry());
//                sb.append("\ncitycode : ");// 城市编码
//                sb.append(location.getCityCode());
//                sb.append("\ncity : ");// 城市
//                sb.append(location.getCity());
//                sb.append("\nDistrict : ");// 区
//                sb.append(location.getDistrict());
//                sb.append("\nStreet : ");// 街道
//                sb.append(location.getStreet());
//                sb.append("\naddr : ");// 地址信息
//                sb.append(location.getAddrStr());
//                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
//                sb.append(location.getUserIndoorState());
//                sb.append("\nDirection(not all devices have value): ");
//                sb.append(location.getDirection());// 方向
//                sb.append("\nlocationdescribe: ");
//                sb.append(location.getLocationDescribe());// 位置语义化信息
//                sb.append("\nPoi: ");// POI信息
//                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
//                    for (int i = 0; i < location.getPoiList().size(); i++) {
//                        Poi poi = (Poi) location.getPoiList().get(i);
//                        sb.append(poi.getName() + ";");
//                    }
//                }
//                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                    sb.append("\nspeed : ");
//                    sb.append(location.getSpeed());// 速度 单位：km/h
//                    sb.append("\nsatellite : ");
//                    sb.append(location.getSatelliteNumber());// 卫星数目
//                    sb.append("\nheight : ");
//                    sb.append(location.getAltitude());// 海拔高度 单位：米
//                    sb.append("\ngps status : ");
//                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
//                    sb.append("\ndescribe : ");
//                    sb.append("gps定位成功");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                    // 运营商信息
//                    if (location.hasAltitude()) {// *****如果有海拔高度*****
//                        sb.append("\nheight : ");
//                        sb.append(location.getAltitude());// 单位：米
//                    }
//                    sb.append("\noperationers : ");// 运营商信息
//                    sb.append(location.getOperators());
//                    sb.append("\ndescribe : ");
//                    sb.append("网络定位成功");
//                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                    sb.append("\ndescribe : ");
//                    sb.append("离线定位成功，离线定位结果也是有效的");
//                } else if (location.getLocType() == BDLocation.TypeServerError) {
//                    sb.append("\ndescribe : ");
//                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
//                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//                }
////                logMsg(sb.toString());
//            }
        }

    };


    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key    LruCache的键，这里传入图片的URL地址。
     * @param bitmap LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        Bitmap bitmap = mMemoryCache.get(key);
        return bitmap;
//        if (bitmap == null) {
//            BitmapWorkerTask task = new BitmapWorkerTask();
//            task.execute(key);
//            return null;
//        } else {
//            return bitmap;
//        }
    }

    /**
     * 异步下载图片的任务。
     *
     * @author guolin
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        /**
         * 图片的URL地址
         */
        private String imageUrl;
        private ServerUser serverUser;

        public BitmapWorkerTask(ServerUser serverUser) {
            this.serverUser = serverUser;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (isCancelled()) {
                return null;
            }
            imageUrl = serverUser.getImage();
            // 在后台开始下载图片
            Bitmap bitmap = downloadBitmap(imageUrl);
            if (bitmap != null) {
                // 图片下载完成后缓存到LrcCache中
                addBitmapToMemoryCache(imageUrl, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (isCancelled()) {
                return;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            taskCollection.remove(this);
            addSingleMarker(serverUser, 1);
        }

        /**
         * 建立HTTP请求，并获取Bitmap对象。
         *
         * @param imageUrl 图片的URL地址
         * @return 解析后的Bitmap对象
         */
        private Bitmap downloadBitmap(String imageUrl) {
            Bitmap bitmap = null;
            HttpURLConnection con = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(10 * 1000);
                bitmap = BitmapFactory.decodeStream(con.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return bitmap;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    //回调接口
    private interface ResultListener {
        void onReturnResult(View view);
    }
}
