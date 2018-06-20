package com.example.meimeng.bean;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.meimeng.R;
import com.example.meimeng.activity.ServerCenterActivity;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentHelpInfo extends RVBaseCell {
    private String image;
    private String useUserName;
    private int distance;
    private String userAddress;
    private String createTime;
    private int helpNum;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUseUserName() {
        return useUserName;
    }

    public void setUseUserName(String useUserName) {
        this.useUserName = useUserName;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getHelpNum() {
        return helpNum;
    }

    public void setHelpNum(int helpNum) {
        this.helpNum = helpNum;
    }

    public CurrentHelpInfo() {
        super(null);
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_currenthelp, parent, false);
        return new RVBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position) {
        LinearLayout nowhelp= (LinearLayout) holder.getView(R.id.nowhelp);
        holder.setText(R.id.rv_name, "姓名：" + useUserName);
        holder.setText(R.id.rv_address, "位置：" + userAddress);
        holder.setText(R.id.rv_distance, "距离：与你" +distance+"米范围内" );
        if(helpNum>0){
            holder.setText(R.id.help_number,"已有"+helpNum+"人前往");
        }else{
            holder.setText(R.id.help_number,"");
        }
        holder.setText(R.id.rv_time,createTime.substring(0,10));
        CircleImageView imageView= (CircleImageView) holder.getImageView(R.id.rv_image);
        final Context context = holder.getItemView().getContext();
        Glide.with(context).load(image).into(imageView);
        nowhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ServerCenterActivity.class));
            }
        });
    }
}
