package com.example.meimeng.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.DrugInfo;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.util.ToaskUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private int intoType = 0;//进入搜索界面的类型，0为地区搜索，1为药品搜索
    private EditText searchEdit;
    private RecyclerView drugRv;
    RVBaseAdapter<DrugInfo> adapter;
    List<DrugInfo> list = new ArrayList<>();
    public static void startSearchActivity(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.searchBtn).setOnClickListener(this);
        searchEdit = (EditText) findViewById(R.id.searchEdit);

        drugRv = (RecyclerView) findViewById(R.id.drugRv);

        Intent intent = getIntent();
        intoType = intent.getIntExtra("type", 0);
        if (intoType == 0) {
            searchEdit.setHint("请输入城市名");
        } else {
            searchEdit.setHint("请输入药品");
        }

        adapter = new RVBaseAdapter<DrugInfo>() {
            @Override
            protected void onViewHolderBound(RVBaseViewHolder holder, int position) {
                 //holder.setText(R.id.);
            }
        };


    }

    @Override
    protected int getContentId() {
        return R.layout.activity_search;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.searchBtn:
                ToaskUtil.showToast(this, "搜索");
                String name=searchEdit.getEditableText().toString();
               // if(!TextUtils.isEmpty(name))
                    search(name,intoType);
//                else{
//                    ToaskUtil.showToast(this, "请输入搜索字段");
//                }
                break;
        }
    }
    private void search(String name,int type){
        if(type==0) {
            searchCity(name);
        }else{
            searchMedicine(name);
        }
    }
    private void searchCity(String cityname){

    }
    private void searchMedicine(String medname){
        Map<String,Object> params=new HashMap<>();
        String latitude="23.05184555053711";
        String longitude="113.4033126831055";
        String token="";
        if(APP.sUserType==0){
            token=APP.getInstance().getUserInfo().getToken();
        }else{
            token=APP.getInstance().getServerUserInfo().getToken();
        }
        params.put("latitude",APP.getInstance().getUserInfo().getLatitude());
        params.put("longitude",APP.getInstance().getUserInfo().getLongitude());
        params.put("name","救心丸");
        HttpProxy.obtain().get(PlatformContans.ForHelp.sGetServerMedicine, params, token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {

                Log.e("search",result);

                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(result);
                    int code=jsonObject.getInt("resultCode");
                    if(code==0){
                         JSONArray beanlist=jsonObject.getJSONArray("data");
                         if(beanlist.length()==0){
                             for(int i=0;i<20;i++){
                                 DrugInfo drugInfo=new DrugInfo();
                                 drugInfo.setName("小明");
                                 drugInfo.setAddress("广东省广州市番禺区");
                                 drugInfo.setTelephone("13202908144");
                                 drugInfo.setMedicine("救心丸/啊托品");
                                 drugInfo.setDistance(300);
                                 list.add(drugInfo);
                             }
                             adapter.setData(list);
                             drugRv.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                             drugRv.setAdapter(adapter);
                         }else{
                             for(int i=0;i<beanlist.length();i++){
                                 DrugInfo drugInfo=new DrugInfo();
                                 JSONObject object= (JSONObject) beanlist.get(i);
                                 drugInfo.setName(object.getString("name"));
                                 drugInfo.setAddress(object.getString("address"));
                                 drugInfo.setTelephone(object.getString("telephone"));
                                 drugInfo.setMedicine(object.getString("medicine"));
                                 drugInfo.setDistance(object.getInt("distance"));
                                 list.add(drugInfo);
                             }
                             adapter.setData(list);
                             drugRv.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                             drugRv.setAdapter(adapter);
                         }
                        //Toast.makeText(ClientUserInfoActivity.this,"更新成功",Toast.LENGTH_LONG).show();
                        //finish();
                    }
                    if(code==9999){

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
}
