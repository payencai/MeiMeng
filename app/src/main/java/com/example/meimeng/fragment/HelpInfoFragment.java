package com.example.meimeng.fragment;

import android.util.Log;

import com.example.meimeng.APP;
import com.example.meimeng.activity.ServerMainActivity;
import com.example.meimeng.bean.CurrentHelpInfo;
import com.example.meimeng.common.rv.absRv.AbsBaseFragment;
import com.example.meimeng.common.rv.base.Cell;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：凌涛 on 2018/6/29 15:58
 * 邮箱：771548229@qq..com
 */
public class HelpInfoFragment extends AbsBaseFragment {

    private int page = 1;
    private boolean isRefresh = false;

    @Override
    public void onRecyclerViewInitialized() {

    }

    @Override
    public void onPullRefresh() {
    }

    @Override
    public void onLoadMore() {

    }


    @Override
    protected List<Cell> getCells(List list) {
        return null;
    }

}
