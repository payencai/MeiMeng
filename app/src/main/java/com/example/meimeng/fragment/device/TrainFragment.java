package com.example.meimeng.fragment.device;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.meimeng.APP;
import com.example.meimeng.bean.FirstAidSkillOption;
import com.example.meimeng.bean.TrainBean;
import com.example.meimeng.common.rv.absRv.AbsBaseFragment;
import com.example.meimeng.common.rv.base.Cell;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：凌涛 on 2018/6/7 18:22
 * 邮箱：771548229@qq..com
 */
public class TrainFragment extends AbsBaseFragment<TrainBean> {
    private int page = 1;
    private boolean isRefresh = false;
    public LocationClient mLocationClient = null;

    @Override
    public void onRecyclerViewInitialized() {
        mLocationClient = new LocationClient(getContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );
        locate();
    }

  private void locate(){
      LocationClientOption option = new LocationClientOption();
      option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
      );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
      option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
      option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
      option.setOpenGps(true);//可选，默认false,设置是否使用gps
      option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
      option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
      option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
      option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
      option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
      option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
      option.setNeedDeviceDirect(true); //返回的定位结果包含手机机头方向
      mLocationClient.setLocOption(option);
      mLocationClient.start(); //启动位置请求
      mLocationClient.requestLocation();//发送请求
  }
    @Override
    public void onPullRefresh() {
        page = 1;
        isRefresh = true;
        load();

    }

    @Override
    public void onLoadMore() {
        page++;
        load();
    }
    private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            APP.lon=location.getLongitude();
            APP.lat=location.getLatitude();
            load();
        }

    };


    @Override
    protected List<Cell> getCells(List<TrainBean> list) {


        return null;
    }

    private void load() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("latitude", APP.lat);
        params.put("longitude", APP.lon);
        Log.e("long",APP.getInstance().getUserInfo().getLongitude()+":"+APP.getInstance().getUserInfo().getLatitude());
        HttpProxy.obtain().get(PlatformContans.AedController.sGetContent, params, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                mBaseAdapter.hideLoadMore();
                Log.d("caihuaqing", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("resultCode");
                    //Log.e("bbbbb",result);
                    final JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray beanList = data.getJSONArray("beanList");
                    List<TrainBean> list = new ArrayList<>();
                    int length = beanList.length();
                    Gson gson = new Gson();
                    for (int i = 0; i < length; i++) {
                       // Log.e("num",length+"");
                        JSONObject item = beanList.getJSONObject(i);
                        Log.e("item",item.toString()+"");
                        //TrainBean bean = gson.fromJson(item.toString(), TrainBean.class);
                        TrainBean bean = new TrainBean();
                        bean.setAddress(item.getString("address"));
                        bean.setContent(item.getString("content"));
                        bean.setPrice(item.getString("price"));
                        bean.setTel(item.getString("tel"));
                        bean.setUsername(item.getString("username"));
                        bean.setId(item.getInt("id"));
                        bean.setIsCancel(item.getInt("isCancel"));
                       // bean.setFlag(0);
                        list.add(bean);
                       /// Log.e("bbbbb", bean.getCompany());
                        //list.add(bean);
                        // list.add(bean);
                    }
//                    Collections.sort(list, new Comparator<TrainBean>() {
//                        @Override
//                        public int compare(TrainBean trainBean, TrainBean t1) {
//                            if(trainBean.getDistance()>t1.getDistance())
//                                return 1;
//                            else if()
//                            return 0;
//                        }
//                    });
                    if (isRefresh) {
                        isRefresh = false;
                        mBaseAdapter.clear();
                        mBaseAdapter.addAll(list);
                    } else {
                        mBaseAdapter.addAll(list);
                    }
                    mSwipeRefreshLayout.setRefreshing(isRefresh);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
//        List<TrainBean> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            list.add(new TrainBean());
//
//        }
//        mBaseAdapter.addAll(list);
    }

}
