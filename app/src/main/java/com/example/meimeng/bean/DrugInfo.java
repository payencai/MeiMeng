package com.example.meimeng.bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meimeng.R;
import com.example.meimeng.activity.SystemMsgDetailedActivity;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

/**
 * 作者：凌涛 on 2018/5/31 14:25
 * 邮箱：771548229@qq..com
 */
public class DrugInfo extends RVBaseCell {

    private String name;
    private String address;
    private String telephone;
    private String medicine;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    private double distance;
    private String longitude;
    private String latitude;
    public DrugInfo(Object o, String name, String address, String telephone, String medicine, int distance) {
        super(o);
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.medicine = medicine;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public DrugInfo() {
        super(null);
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_system_bean, parent, false);
        return new RVBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RVBaseViewHolder holder, int position) {
        holder.setText(R.id.userTel,name.substring(0,1)+"**");
        holder.setText(R.id.drugInfo,"药品信息： "+medicine);
        holder.setText(R.id.address,address);
        int d= (int) (distance);
        holder.setText(R.id.distance,"距离你"+d+"米");
    }


}
