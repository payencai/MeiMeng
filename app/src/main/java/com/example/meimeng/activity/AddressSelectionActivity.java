package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
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
    private PoiSearch mPoiSearch;
    private GeoCoder mSearch;
    private TextView cityShowView;

    private boolean isShowCity = true;

    @Override
    protected void initView() {
        locationService = (APP.getInstance().locationService);
        locationService.registerListener(myListener);

        title = (TextView) findViewById(R.id.title);
        siteRv = (RecyclerView) findViewById(R.id.siteRv);
        cityShowView = (TextView) findViewById(R.id.cityShowView);
        mMapView = (MapView) findViewById(R.id.locationMapView);
        findViewById(R.id.searchItemContent).setOnClickListener(this);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapStatusChangeListener(listener);

        title.setText("选择地址");
        searchTypeSelect = (LinearLayout) findViewById(R.id.searchTypeSelect);

        initSiteList();

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.locationImg).setOnClickListener(this);

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(mResultListener);

        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(mGeoCoderListener);
        locationService.start();

    }

    public static void startAddressSelectionActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, AddressSelectionActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_address_selection;
    }

    private void setCircle() {
        setMarker();
        setUserMapCenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 0) {
                AddressBean addressBean = (AddressBean) data.getSerializableExtra("addressBean");
                lat = addressBean.getLat();
                lon = addressBean.getLon();
                setCircle();
                LatLng latLng = new LatLng(lat, lon);
                reverseGeoCode(latLng);
            }
        }
    }

    //发起地理编码检索；
    private void findGeocode(String city, String addressStr) {
        GeoCodeOption geoCodeOption = new GeoCodeOption();
        geoCodeOption
                .city(city)
                .address(addressStr);
        mSearch.geocode(geoCodeOption);
    }

    //发起地理编码检索；
    private void reverseGeoCode(LatLng lng) {
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(lng));
    }

    //搜索位置点周边poi
    private void getPoiNearby(String keyword, LatLng latLng, int radius) {
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.keyword(keyword);//关键字
        nearbySearchOption.location(latLng);//搜索的位置点
        nearbySearchOption.radius(radius);//搜索覆盖半径
        nearbySearchOption.sortType(PoiSortType.distance_from_near_to_far);//搜索类型， 从近至远
        nearbySearchOption.pageNum(1);//查询第几页：POI量可能会很多，会有分页查询;
        nearbySearchOption.pageCapacity(10);//设置每页查询的个数，默认10个
        mPoiSearch.searchNearby(nearbySearchOption);//查询
    }


    /**
     * 搜索指定区域POI
     * LatLngBounds searchbound：地理范围数据结构，两个点就可以确定一个矩形
     * southwest：西南点
     * northeast：东北点
     */
    private void getPoiBoundSearch(LatLng southwest, LatLng northeast, String keyword) {
        PoiBoundSearchOption boundSearchOption = new PoiBoundSearchOption();
        LatLngBounds searchbound = new LatLngBounds.Builder().include(southwest).include(northeast).build();
        boundSearchOption.bound(searchbound);//设置边界
        boundSearchOption.keyword(keyword);
        boundSearchOption.pageNum(1);
        mPoiSearch.searchInBound(boundSearchOption);//查询
    }

    /**
     * 搜索城市内POI
     */
    private void getPoiCity(String citystr, String keyword) {
        PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
        citySearchOption.city(citystr);//城市名称,最小到区级单位
        citySearchOption.keyword(keyword);
        citySearchOption.isReturnAddr(true);//是否返回门址类信息：xx区xx路xx号
        citySearchOption.pageNum(1);
        mPoiSearch.searchInCity(citySearchOption);//查询
    }

    //再介绍一个推荐查询，应用场景：百度地图搜索输入：
    // 银行，百度会给你推荐各大银行供你选择，点击后显示具体位置及POI信息
    private void getSuggestion(String citystr, String keyword, LatLng latLng) {
        SuggestionSearch mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(suggestionlistener);
        SuggestionSearchOption sSption = new SuggestionSearchOption();
        sSption.city(citystr);//指定城市
        sSption.keyword(keyword);
        sSption.location(latLng);//指定位置，选填，设置位置之后，返回结果按距离该位置的远近进行排序
        sSption.citylimit(true);//设置限制城市范围，仅返回指定城市检索结果，默认为false
        mSuggestionSearch.requestSuggestion(sSption);
    }


    private void initSiteList() {

        mAdapter = new RVBaseAdapter<AddressBean>() {
            @Override
            protected void onViewHolderBound(RVBaseViewHolder holder, int position) {

            }

            @Override
            protected void onClick(RVBaseViewHolder holder, final int position) {
                holder.getView(R.id.item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddressBean addressBean = mData.get(position);
                        Intent intent = new Intent();
                        intent.putExtra("address", addressBean);
                        setResult(SelectAddressActivity.REQUEST_CODE_FROM_SELECTADDRESSACTIVITY, intent);
                        finish();
                    }
                });
            }
        };
        siteRv.setLayoutManager(new LinearLayoutManager(this));
        siteRv.setAdapter(mAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

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
        mSearch.destroy();
        mPoiSearch.destroy();
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
            case R.id.searchItemContent:
                String city = cityShowView.getText().toString();
                if (TextUtils.isEmpty(city)) {
                    ToaskUtil.showToast(this, "正在定位，请稍等...");
                    return;
                }
                SearchPOIActivity.startSearchPOIActivity(this, city);
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
//            List<AddressBean> list = new ArrayList<>();
//            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
//                String addr = location.getAddrStr();    //获取详细地址信息
//                String street = location.getStreet();    //获取街道信息
//                AddressBean addressBean = new AddressBean(street, addr);
//                list.add(addressBean);
//                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
//                    for (int i = 0; i < location.getPoiList().size(); i++) {
//                        Poi poi = (Poi) location.getPoiList().get(i);
//                        String name = poi.getName();
//                        AddressBean bean = new AddressBean(name, addr);
//                        list.add(bean);
//                    }
//                }
//                mAdapter.reset(list);
//            }

            double latitude = location.getLatitude();//纬度
            double longitude = location.getLongitude();//经度
            LatLng latLng = new LatLng(latitude, longitude);
            reverseGeoCode(latLng);
        }

    };

    private void location(BDLocation location) {
        //经纬度
        lon = location.getLongitude();//经度
        lat = location.getLatitude();//纬度
        setCircle();
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
            reverseGeoCode(target);
        }
    };

    /**
     * 查询POI结果回调监听器，包括周边，区域，城市返回的搜索结果
     */
    OnGetPoiSearchResultListener mResultListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {//获取poi检索列表
            /**
             * PoiInfo中包含了经纬度、城市、地址信息、poi名称、uid、邮编、电话等等信息；
             有了这些，你是不是可以可以在这里画一个自定义的图层了，然后添加点击事件，做一些操作了呢
             */
            Log.d("onGetPoiResult", "onGetPoiResult: 我去。。。");

            List<PoiInfo> poiInfos = poiResult.getAllPoi();
            if (poiInfos != null) {
                for (PoiInfo poiInfo : poiInfos) {
                    String address = poiInfo.address;
                    String name = poiInfo.name;
                    String uid = poiInfo.uid;
                    String province = poiInfo.province;
                    String city = poiInfo.city;
                    String area = poiInfo.area;
                    String street_id = poiInfo.street_id;
                    String phoneNum = poiInfo.phoneNum;
                    String postCode = poiInfo.postCode;

                    Log.d("onGetPoiResult", "onGetPoiResult: address:" + address + ",name:" + name + ",uid:" + uid + ",province:" + province + ",city:" + city + ",area:" + area
                            + ",streed_id:" + street_id + ",phoneNum:" + phoneNum + ",postCode:" + postCode);
                    Log.d("onGetPoiResult", "onGetPoiResult: --------------");


                }
            }

            /**
             * PoiAddrInfo：只包含地址、经纬度、名称
             */
            List<PoiAddrInfo> poiAddrInfos = poiResult.getAllAddr();
            if (poiAddrInfos != null) {
                for (PoiAddrInfo poiAddrInfo : poiAddrInfos) {
                    LatLng location = poiAddrInfo.location;
                    double latitude = location.latitude;
                    double longitude = location.longitude;
                }
            }

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {//获取某个Poi详细信息

            /** * 当执行以下请求时，此方法回调
             *  PoiDetailSearchOption detailSearchOption = new PoiDetailSearchOption();
             *  detailSearchOption.poiUid(poiInfo.uid);//设置要查询的poi的uid
             *  mPoiSearch.searchPoiDetail(detailSearchOption);
             *  //查询poi详细信息
             * */
            //poiDetailResult里面包含poi的巨多信息，你想要的都有，这里不列举了
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {//查询室内poi检索结果回调

            /** * 当执行以下请求时，此方法回调
             *  PoiIndoorOption indoorOption = new PoiIndoorOption();
             *  indoorOption.poiFloor(floor);//楼层
             *  mPoiSearch.searchPoiDetail(indoorOption);
             *  */

        }
    };

    OnGetSuggestionResultListener suggestionlistener = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult result) {//获取推荐POI列表
            //SuggestionInfo中包含的信息有限，只包含：联想词，坐标点，POI的uid等
            //如果想要POI的详细信息，还得利用uid通过mPoiSearch.searchPoiDetail获取 */
            List<SuggestionResult.SuggestionInfo> suggestionInfos = result.getAllSuggestions();
        }
    };

    OnGetGeoCoderResultListener mGeoCoderListener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
                //没有检索到结果
            }
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
                    if (isShowCity) {
                        isShowCity = false;
                        cityShowView.setText(city);
                    }
                }
                isShowCity = true;
                if (list.size() > 0) {
                    mAdapter.reset(list);
                }
            }
        }
    };
}