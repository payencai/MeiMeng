package com.example.meimeng.bean;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

import java.io.Serializable;

/**
 * 作者：凌涛 on 2018/6/7 18:24
 * 邮箱：771548229@qq..com
 */
public class TrainBean extends RVBaseCell implements Serializable {
    private String address;
    private String company;
    private int distance;
    private int id;
    private int isCancel;
    private String latitude;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    private String longitude;
    private String price;
    private int submitTime;
    private String tel;
    private String username;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return company;
    }

    public void setContent(String content) {
        this.company = content;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(int isCancel) {
        this.isCancel = isCancel;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(int submitTime) {
        this.submitTime = submitTime;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public TrainBean() {
        super(null);
    }

    @Override
    public int getItemType() {
        return 0;
    }

    private boolean isNum(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_device_bean, parent, false);
        return new RVBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        if (!TextUtils.isEmpty(company)&&!TextUtils.equals("null",company)) {
            TextView con = holder.getTextView(R.id.content);
            con.setVisibility(View.VISIBLE);
            holder.setText(R.id.content, company);
        }
        RelativeLayout addr = (RelativeLayout) holder.getView(R.id.addr_layout);
        holder.setText(R.id.company, username.trim() + "  " + tel);
        if (TextUtils.isEmpty(tel)||TextUtils.equals("null",tel))
            holder.setText(R.id.company, username.trim());
        if (TextUtils.isEmpty(username)||TextUtils.equals("null",username))
            holder.setText(R.id.company, tel);
        if (TextUtils.equals("null",username) && TextUtils.equals("null",tel)) {
            holder.getView(R.id.company).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(address) && !TextUtils.equals("null", address))
            holder.setText(R.id.address, address.replace(" ", ""));
        else {
            addr.setVisibility(View.GONE);
        }
        if (isNum(price))
            holder.setText(R.id.money, "￥" + price + "元");
        else {
            holder.getView(R.id.money).setVisibility(View.GONE);

        }
    }
}
