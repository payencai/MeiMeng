package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.util.ToaskUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchPOIActivity extends BaseActivity implements View.OnClickListener {

    private SuggestionSearch mSuggestionSearch;
    private EditText keywordEdit;
    private String mCity;
    private RecyclerView poiSugRv;
    private RVBaseAdapter<AddressBean> mAdapter;
    private PoiSearch mPoiSearch;
    private static final int REQUEST_CODE = 0;

    @Override
    protected int getContentId() {
        return R.layout.activity_search_poi;
    }

    @Override
    protected boolean windowActionBar() {
        return true;
    }

    public static void startSearchPOIActivity(Activity activity, String city) {
        Intent intent = new Intent(activity, SearchPOIActivity.class);
        intent.putExtra("city", city);
//        startActivityForResult
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void initView() {
        findViewById(R.id.back).setOnClickListener(this);
        keywordEdit = (EditText) findViewById(R.id.keywordEdit);
        poiSugRv = (RecyclerView) findViewById(R.id.poiSugRv);
        initPoiSugRv();
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);
        mCity = getIntent().getStringExtra("city");
        if (TextUtils.isEmpty(mCity)) {
            ToaskUtil.showToast(this, "启动异常，请重启界面");
            finish();
        }
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(mResultListener);
        keywordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
//                requestSuggestion(mCity, keyword);
                getPoiCity(mCity, keyword);
            }
        });

    }

    private void initPoiSugRv() {

        mAdapter = new RVBaseAdapter<AddressBean>() {
            @Override
            protected void onViewHolderBound(RVBaseViewHolder holder, int position) {

            }

            @Override
            protected void onClick(RVBaseViewHolder holder, final int position) {
                holder.getView(R.id.item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddressBean addressBean = getData().get(position);
                        Intent data = new Intent();
                        data.putExtra("addressBean", addressBean);
                        setResult(REQUEST_CODE, data);
                        finish();
                    }
                });
            }
        };
        poiSugRv.setLayoutManager(new LinearLayoutManager(this));
        poiSugRv.setAdapter(mAdapter);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    //  发起在线建议查询；
    private void requestSuggestion(String city, String keyword) {
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .keyword(keyword)
                .city(city));//必选
    }

    OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
        public void onGetSuggestionResult(SuggestionResult res) {

            if (res == null || res.getAllSuggestions() == null) {
                return;
                //未找到相关结果
            }
            List<SuggestionResult.SuggestionInfo> allSuggestions = res.getAllSuggestions();
            if (allSuggestions != null) {
                List<AddressBean> list = new ArrayList<>();
                for (int i = 0; i < allSuggestions.size(); i++) {
                    SuggestionResult.SuggestionInfo info = allSuggestions.get(i);
                    String address = info.address;
                    String city = info.city;
                    String district = info.district;
                    String key = info.key;
                    LatLng pt = info.pt;
                    Log.d("initView", "onGetSuggestionResult: " + address);
//                    list.add(new AddressBean(address, address));
                }
                mAdapter.reset(list);
            }

            //获取在线建议检索结果
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
            List<PoiInfo> poiInfos = poiResult.getAllPoi();
            if (poiInfos != null) {
                List<AddressBean> list = new ArrayList<>();
                for (PoiInfo poiInfo : poiInfos) {
                    LatLng location = poiInfo.location;
                    double longitude = location.longitude;//经度
                    double latitude = location.latitude;//维度
                    Log.d("onGetPoiResult", "onGetPoiResult: 经度：" + longitude + ",维度:" + latitude);
                    String address = poiInfo.address;
                    String name = poiInfo.name;
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
                mAdapter.reset(list);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuggestionSearch.destroy();
        mPoiSearch.destroy();
    }
}
