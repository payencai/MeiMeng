package com.example.meimeng.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.service.LocationService;

public class ShowAEDActivity extends BaseActivity {
    private LocationService locationService;

    @Override
    protected int getContentId() {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        locationService = APP.getInstance().locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，
        // 然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(myListener);
        return R.layout.activity_show_aed;
    }

    @Override
    protected void initView() {

    }


    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
//            locationcity = location.getCity();
//            locationprovince = location.getProvince();
//            String addr = location.getAddrStr();    //获取详细地址信息
//            String country = location.getCountry();    //获取国家
//            String province = location.getProvince();    //获取省份
//            String city = location.getCity();    //获取城市
//            String district = location.getDistrict();    //获取区县
//            String street = location.getStreet();    //获取街道信息
//            int LocType = location.getLocType();    //返回码
//            lat = location.getLatitude();
//            lon = location.getLongitude();
//            Log.d("onReceiveLocation", "onReceiveLocation: 定位");
//            location(city, addr);
//            locationService.setLocationOption(locationService.getSingleLocationClientOption());

        }


    };

    private void location(String city, String addr) {

    }
}
