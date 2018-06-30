package com.example.meimeng.bean;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.meimeng.R;
import com.example.meimeng.activity.RescueActivity;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentHelpInfo extends RVBaseCell implements Serializable {


    /**
     * id : b0259267-ac5e-45d5-b6e5-b8fb586163ae
     * useUserId : ced242067b854b9f9853899f3a70ecfc
     * longitude : 113.401694
     * latitude : 23.051105
     * createTime : 2018-06-29 15:51:29
     * userAddress : 中国广东省广州市番禺区大学城广工一路
     * helpNum : 1
     * openId : null
     * type : 1
     * geohash : ws0ehs2d
     * groupId : 53330877415425
     * distance : 7659.145146992639
     * image : http://memen.oss-cn-shenzhen.aliyuncs.com/%E4%B8%8A%E4%BC%A0/2018062518055192?Expires=1530318866&OSSAccessKeyId=LTAIu2UT56nIQWZI&Signature=2ldsriI%2FGGFERmObXgqPRfJj5v0%3D
     * imageKey : 上传/2018062518055192
     * useUserName : 蔡华清
     * useUserTelephone : 13202908144
     */

    private String id;
    private String useUserId;
    private String longitude;
    private String latitude;
    private String createTime;
    private String userAddress;
    private int helpNum;
    private String openId;
    private int type;
    private String geohash;
    private String groupId;
    private double distance;
    private String image;
    private String imageKey;
    private String useUserName;
    private String useUserTelephone;

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
//        holder.getView(R.id.item).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                context.startActivity(new Intent(context, RescueActivity.class));
//            }
//        });
//        nowhelp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUseUserId() {
        return useUserId;
    }

    public void setUseUserId(String useUserId) {
        this.useUserId = useUserId;
    }

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public int getHelpNum() {
        return helpNum;
    }

    public void setHelpNum(int helpNum) {
        this.helpNum = helpNum;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getUseUserName() {
        return useUserName;
    }

    public void setUseUserName(String useUserName) {
        this.useUserName = useUserName;
    }

    public String getUseUserTelephone() {
        return useUserTelephone;
    }

    public void setUseUserTelephone(String useUserTelephone) {
        this.useUserTelephone = useUserTelephone;
    }
}
