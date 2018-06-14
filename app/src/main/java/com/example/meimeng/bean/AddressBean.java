package com.example.meimeng.bean;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

/**
 * 作者：凌涛 on 2018/6/13 16:45
 * 邮箱：771548229@qq..com
 */
public class AddressBean extends RVBaseCell {

    private String title;
    private String detailAddress;

    public AddressBean() {
        super(null);
    }

    public AddressBean(String title, String detailAddress) {
        super(null);
        this.title = title;
        this.detailAddress = detailAddress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_addressbean_layout, parent, false);
        return new RVBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position) {
        TextView addressTitleView = holder.getTextView(R.id.addressTitle);
        if (position == 0) {
            addressTitleView.setTextColor(Color.parseColor("#ffa31c"));
        }
        addressTitleView.setText(title);
        holder.setText(R.id.detailAddress, detailAddress);
    }
}
