package com.example.meimeng.bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meimeng.R;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

/**
 * 作者：凌涛 on 2018/6/8 17:21
 * 邮箱：771548229@qq..com
 */
public class FirstAidRankBean extends RVBaseCell {
    public FirstAidRankBean() {
        super(null);
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_device_bean, parent, false);
        return new RVBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position) {
        holder.setText(R.id.company,"某某某科技有限公司");
    }
}
