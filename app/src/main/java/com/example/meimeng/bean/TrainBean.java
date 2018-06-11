package com.example.meimeng.bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meimeng.R;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

/**
 * 作者：凌涛 on 2018/6/7 18:24
 * 邮箱：771548229@qq..com
 */
public class TrainBean extends RVBaseCell {

    public TrainBean() {
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
        holder.setText(R.id.company, "培训内容培训内容培训内容培训内容培训内容培训内容培培训培训内");

    }
}