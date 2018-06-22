package com.example.meimeng.activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.bean.SystemMsgBean;
import com.example.meimeng.bean.TrainBean;
import com.example.meimeng.common.rv.absRv.AbsBaseActivity;
import com.example.meimeng.common.rv.base.Cell;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemMsgActivity extends AbsBaseActivity<SystemMsgBean> {
    private int page=31;
    private boolean isRefresh=false;
    private TextView mTitle;

    @Override
    public void onRecyclerViewInitialized() {
        findViewById(R.id.back).setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText("系统信息");
        addDividerItem(0);
        loadData();

    }

    @Override
    public void onPullRefresh() {
        page=1;
        isRefresh=true;
        loadData();
    }


    @Override
    public void onLoadMore() {
        page++;
        loadData();

    }

    @Override
    public View addToolbar() {
        View view = LayoutInflater.from(this).inflate(R.layout.toobar_head_layout, null);
        return view;
    }

    @Override
    protected List<Cell> getCells(List<SystemMsgBean> list) {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private void loadData() {
        addSysInfo();
        Map<String,Object> params=new HashMap<>();
        String token="";
        if(APP.sUserType==0){
            token=APP.getInstance().getUserInfo().getToken();
        }else{
            token=APP.getInstance().getServerUserInfo().getToken();
        }
        params.put("type",1);
        params.put("page",page);
        HttpProxy.obtain().get(PlatformContans.MessageController.sGetSysInfoBytype, params,token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                mBaseAdapter.hideLoadMore();
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    int code=jsonObject.getInt("resultCode");
                    Log.e("tag",result);
                    if(code==0){
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray beanList = data.getJSONArray("beanList");
                        List<SystemMsgBean> list = new ArrayList<>();
                        int length = beanList.length();
                        Gson gson = new Gson();
                        for (int i = 0; i < length; i++) {
                            JSONObject item = beanList.getJSONObject(i);
                            //TrainBean bean = gson.fromJson(item.toString(), TrainBean.class);
                            SystemMsgBean bean=new SystemMsgBean();
                            bean.setArticle(item.getString("article"));
                            long time=item.getLong("createTime");
                            bean.setCreateTime(time);
                            bean.setTitle(item.getString("title"));
                            bean.setId(item.getInt("id"));
                            list.add(bean);
                        }
                        if (isRefresh) {
                            isRefresh = false;
                            mBaseAdapter.clear();
                            mBaseAdapter.addAll(list);

                        } else {
                            mBaseAdapter.addAll(list);
                        }
                        mSwipeRefreshLayout.setRefreshing(isRefresh);
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
    private void addSysInfo(){
        Map<String,Object> params=new HashMap<>();
        String token="";
        if(APP.sUserType==0){
            token=APP.getInstance().getUserInfo().getToken();
        }else{
            token=APP.getInstance().getServerUserInfo().getToken();
        }
        params.put("title","欢迎你");
        params.put("article","我是系统信息的内容体");
        HttpProxy.obtain().post(PlatformContans.MessageController.sAddSysInfo, token,params, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    int code=jsonObject.getInt("resultCode");
                    Log.e("tag",result);
                    if(code==0){

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
