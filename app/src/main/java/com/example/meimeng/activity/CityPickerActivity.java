package com.example.meimeng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.util.LoginSharedUilt;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

public class CityPickerActivity extends BaseActivity {
    TextView title;
    ImageView back;

    private List<HotCity> hotCities;


    private String city="广州";
    private String province="广东省";
    private PoiSearch mPoiSearch;
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
    /**
     * 查询POI结果回调监听器，包括周边，区域，城市返回的搜索结果
     */
    City info;
    double latitude;
    double longitude;
    OnGetPoiSearchResultListener mResultListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {//获取poi检索列表
            List<PoiInfo> poiInfos = poiResult.getAllPoi();
            if (poiInfos != null) {
                PoiInfo poiInfo = poiInfos.get(0);
                LatLng location = poiInfo.location;
                if(location!=null){
                    longitude = location.longitude;//经度
                    latitude = location.latitude;//维度
                }

                Log.e("bbb",longitude+"： "+latitude);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("city", info);
                bundle.putDouble("lat",latitude);
                bundle.putDouble("lon",longitude);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }

//            List<PoiAddrInfo> poiAddrInfos = poiResult.getAllAddr();
//            if (poiAddrInfos != null) {
//                for (PoiAddrInfo poiAddrInfo : poiAddrInfos) {
//                    LatLng location = poiAddrInfo.location;
//                    double latitude = location.latitude;
//                    double longitude = location.longitude;
//                }
//            }

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {//获取某个Poi详细信息
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {//查询室内poi检索结果回调


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }
    @Override
    protected void initView() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(mResultListener);
        city = getIntent().getExtras().getString("city");
        province = getIntent().getExtras().getString("province");
        init();
    }

    private void init() {
        Double lat = LoginSharedUilt.getIntance(this).getLat();
        Double lon = LoginSharedUilt.getIntance(this).getLon();
        hotCities = new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));
        CityPicker.getInstance()
                .setFragmentManager(getSupportFragmentManager())    //此方法必须调用
                .enableAnimation(false)    //启用动画效果
                .setLocatedCity(new LocatedCity(city, province, "101210101"))
                .setHotCities(null)
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        info=data;
                        getPoiCity(data.getName(),data.getName());

                        //Toast.makeText(getApplicationContext(), data.getCode(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected int getContentId() {
        return R.layout.activity_city_picker;
    }


}
