package com.example.meimeng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
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



    @Override
    protected void initView() {
        init();
    }
    private  void init(){
        Double lat=LoginSharedUilt.getIntance(this).getLat();
        Double lon=LoginSharedUilt.getIntance(this).getLon();
        hotCities=new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));
        CityPicker.getInstance()
                .setFragmentManager(getSupportFragmentManager())	//此方法必须调用
                .enableAnimation(false)	//启用动画效果
                .setLocatedCity(new LocatedCity("杭州", "浙江", "101210101"))
                .setHotCities(null)
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                       // finish();
                        Intent intent=new Intent();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("city",data);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK,intent);
                        finish();
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
