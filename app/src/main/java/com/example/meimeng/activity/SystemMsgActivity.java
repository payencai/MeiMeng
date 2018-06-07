package com.example.meimeng.activity;

import android.view.LayoutInflater;
import android.view.View;

import com.example.meimeng.R;
import com.example.meimeng.bean.SystemMsgBean;
import com.example.meimeng.common.rv.absRv.AbsBaseActivity;
import com.example.meimeng.common.rv.base.Cell;

import java.util.ArrayList;
import java.util.List;

public class SystemMsgActivity extends AbsBaseActivity<SystemMsgBean> {

    @Override
    public void onRecyclerViewInitialized() {
        findViewById(R.id.back).setOnClickListener(this);
        addDividerItem(0);
        loadData();

    }

    @Override
    public void onPullRefresh() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(false);
            }
        }, 2000);
    }


    @Override
    public void onLoadMore() {


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
        List<SystemMsgBean> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            SystemMsgBean bean = new SystemMsgBean();
            list.add(bean);
        }
        mBaseAdapter.addAll(list);
    }

}
