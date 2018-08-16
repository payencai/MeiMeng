package com.example.meimeng.fragment.device;

import android.util.Log;

import com.example.meimeng.APP;
import com.example.meimeng.bean.FirstAidRankBean;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：凌涛 on 2018/6/8 17:23
 * 邮箱：771548229@qq..com
 */
public class FirstAidRankFragment extends AbsBaseFragment<FirstAidRankBean> {
    @Override
    public void onRecyclerViewInitialized() {
        load();
    }
    private int page=1;
    private boolean isRefresh=false;
    @Override
    public void onPullRefresh() {
        page=1;
        isRefresh=true;
        load();
    }

    @Override
    public void onLoadMore() {
        page++;
        load();
    }

    @Override
    protected List<Cell> getCells(List<FirstAidRankBean> list) {
        return null;
    }

    private void load() {
        Map<String,Object> params=new HashMap<>();
        String token="";
        if(APP.sUserType==0){
            token=APP.getInstance().getUserInfo().getToken();
        }else{
            token=APP.getInstance().getServerUserInfo().getToken();
        }
        params.put("page",page);
        HttpProxy.obtain().get(PlatformContans.AedController.sGetFacility, params,token, new ICallBack() {
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
                        List<TrainBean> list = new ArrayList<>();
                        int length = beanList.length();
                        Gson gson = new Gson();
                        for (int i = 0; i < length; i++) {
                            JSONObject item = beanList.getJSONObject(i);
                            //TrainBean bean = gson.fromJson(item.toString(), TrainBean.class);
                            TrainBean bean=new TrainBean();
                            bean.setAddress(item.getString("address"));
                            bean.setContent(item.getString("content"));
                            bean.setPrice(item.getString("price"));
                            bean.setTel(item.getString("tel"));
                            bean.setUsername(item.getString("username"));
                            bean.setId(item.getInt("id"));
                            bean.setIsCancel(item.getInt("isCancel"));
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
}
