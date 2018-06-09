package com.example.meimeng.fragment.device;

import com.example.meimeng.bean.FirstAidRankBean;
import com.example.meimeng.bean.TrainBean;
import com.example.meimeng.common.rv.absRv.AbsBaseFragment;
import com.example.meimeng.common.rv.base.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：凌涛 on 2018/6/8 17:23
 * 邮箱：771548229@qq..com
 */
public class FirstAidRankFragment extends AbsBaseFragment<FirstAidRankBean> {
    @Override
    public void onRecyclerViewInitialized() {
        load();
    }

    @Override
    public void onPullRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    protected List<Cell> getCells(List<FirstAidRankBean> list) {
        return null;
    }

    private void load() {
        List<FirstAidRankBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new FirstAidRankBean());
        }
        mBaseAdapter.addAll(list);
    }
}
