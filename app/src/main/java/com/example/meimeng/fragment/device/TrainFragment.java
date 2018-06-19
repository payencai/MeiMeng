package com.example.meimeng.fragment.device;

import android.util.Log;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：凌涛 on 2018/6/7 18:22
 * 邮箱：771548229@qq..com
 */
public class TrainFragment extends AbsBaseFragment<TrainBean> {
    private int page=1;
    private boolean isRefresh=false;
    @Override
    public void onRecyclerViewInitialized() {
        load();
    }

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
    protected List<Cell> getCells(List<TrainBean> list) {


        return null;
    }

    private void load() {
        Map<String,Object> params=new HashMap<>();
        params.put("page",page);
        HttpProxy.obtain().get(PlatformContans.AedController.sGetContent, params, new ICallBack() {
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
                            bean.setCompany(item.getString("company"));
                            bean.setAddress(item.getString("address"));
                            bean.setContent(item.getString("content"));
                            bean.setPrice(item.getString("price"));
                            bean.setTel(item.getString("tel"));
                            bean.setUsername(item.getString("username"));
                            bean.setId(item.getInt("id"));
                            bean.setIsCancel(item.getInt("isCancel"));
                            bean.setFlag(0);
                            list.add(bean);
                            //list.add(bean);
                           // list.add(bean);
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
//        List<TrainBean> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            list.add(new TrainBean());
//
//        }
//        mBaseAdapter.addAll(list);
    }

}
