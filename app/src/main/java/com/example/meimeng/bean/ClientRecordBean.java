package com.example.meimeng.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.meimeng.R;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClientRecordBean extends RVBaseCell {

    private String completeTime;
    private String address;
    private List<String> imgList;
    public ClientRecordBean() {
        super(null);
    }

    public ClientRecordBean( String completeTime, String address, List<String> imgList) {
        super(null);
        this.completeTime = completeTime;
        this.address = address;
        this.imgList = imgList;
    }

    @Override
    public int getItemType() {
        return 0;
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

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
        return new RVBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position) {
        holder.setText(R.id.tv_complete,"已完成");
        holder.setText(R.id.tv_sendhelp,"你发起了求救");
        holder.setText(R.id.tv_canjia,"参与本次救助人员");
        holder.setImageView(R.id.iv_record_location,R.mipmap.ic_location);
        holder.setText(R.id.tv_createhelp_time,completeTime);
        holder.setText(R.id.tv_help_address,address);
        addGroupImg(holder,position,imgList);

        //holder.setText(R.id.tv_help_people,name);
    }

    private void addGroupImg(RVBaseViewHolder holder, int position,List<String> imgList) {
        LinearLayout linearLayout= (LinearLayout) holder.getView(R.id.addImg);

        for (String s:imgList){
            Context context=holder.getItemView().getContext();
            ImageView imageView=new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(45, 45));
            Glide.with(context).load(s).into(imageView);
            linearLayout.addView(imageView);
        }
    }
}
