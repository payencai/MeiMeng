package com.example.meimeng.bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meimeng.R;
import com.example.meimeng.activity.SystemMsgDetailedActivity;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

/**
 * 作者：凌涛 on 2018/6/7 10:12
 * 邮箱：771548229@qq..com
 */
public class SystemMsgBean extends RVBaseCell {


    public SystemMsgBean() {
        super(null);
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_msg_list_layout, parent, false);
        return new RVBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RVBaseViewHolder holder, int position) {
        holder.getView(R.id.item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemMsgDetailedActivity.startSystemMsgDetailedActivity(holder.getItemView().getContext(), "系统信息", "系统信息");
            }
        });
    }
}
