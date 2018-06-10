package com.example.meimeng.bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meimeng.R;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

public class ServerRecordBean extends RVBaseCell {

    private String completeTime;
    private String address;
    private String name;
    public ServerRecordBean( ) {
        super(null);
    }

    public ServerRecordBean(String completeTime, String address, String name) {
        super(null);
        this.completeTime = completeTime;
        this.address = address;
        this.name = name;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
        return new RVBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position) {
        holder.setText(R.id.tv_complete,"已完成");
        holder.setText(R.id.tv_createhelp_time,completeTime);
        holder.setText(R.id.tv_sendhelp,"用户"+name+"'发起了请求");
        holder.setText(R.id.tv_help_address,address);
        holder.setText(R.id.tv_canjia,"你参与了本次救援");
    }
}
