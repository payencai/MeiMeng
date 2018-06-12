package com.example.meimeng.fragment;

import android.content.Intent;
import android.text.TextUtils;

import com.example.meimeng.APP;
import com.example.meimeng.activity.LoginActivity;
import com.example.meimeng.bean.FirstAidSkillOption;
import com.example.meimeng.common.rv.absRv.AbsBaseFragment;
import com.example.meimeng.common.rv.base.Cell;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.ToaskUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：凌涛 on 2018/6/11 17:13
 * 邮箱：771548229@qq..com
 */
public class FirstAidSkillragment extends AbsBaseFragment<FirstAidSkillOption> {

    private int page = 1;
    private boolean isRefresh = false;

    @Override
    public void onRecyclerViewInitialized() {
        loadHead();
        loadData();
    }

    private void loadHead() {
        FirstAidSkillOption option = new FirstAidSkillOption();
        option.layoutType = 0;
        mBaseAdapter.add(option);
    }

    @Override
    public void onPullRefresh() {
        page = 1;
        isRefresh = true;
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(false);
                loadData();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        page++;
        loadData();
    }

    @Override
    protected List<Cell> getCells(List<FirstAidSkillOption> list) {
        return null;
    }

    private void loadData() {
        String token = APP.getInstance().getUserInfo().getToken();
        if (TextUtils.isEmpty(token)) {
            ToaskUtil.showToast(getContext(), "登录异常");
            startActivity(new Intent(getContext(), LoginActivity.class));
            ActivityManager.getInstance().finishAllActivity();
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("type", 1);
        HttpProxy.obtain().get(PlatformContans.AidKnowController.sGetAidKnowByManage, params, token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                mBaseAdapter.hideLoadMore();
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    if (resultCode == 0) {
                        JSONObject data = object.getJSONObject("data");
                        JSONArray beanList = data.getJSONArray("beanList");
                        int length = beanList.length();
                        Gson gson = new Gson();
                        List<FirstAidSkillOption> list = new ArrayList<>();
                        for (int i = 0; i < length; i++) {
                            JSONObject item = beanList.getJSONObject(i);
                            FirstAidSkillOption bean = gson.fromJson(item.toString(), FirstAidSkillOption.class);
                            bean.layoutType = 1;
                            list.add(bean);
                        }
                        if (isRefresh) {
                            isRefresh = false;
                            mBaseAdapter.clear();
                            loadHead();
                            mBaseAdapter.addAll(list);

                        } else {
                            mBaseAdapter.addAll(list);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                mBaseAdapter.hideLoadMore();
                ToaskUtil.showToast(getContext(), "请检查网络");
            }
        });


    }
}
