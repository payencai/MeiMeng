package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.CurrentHelpInfo;
import com.example.meimeng.bean.DrugInfo;
import com.example.meimeng.bean.HelpMsg;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ServerMainActivity extends BaseActivity {
    @BindView(R.id.tv_help_time)
    TextView time;
    @BindView(R.id.tv_help_count)
    TextView count;
    @BindView(R.id.tv_help_distance)
    TextView distance;
    @BindView(R.id.tv_juli_date)
    TextView juli_date;
    @BindView(R.id.tv_or_help)
    TextView or_help;
    @BindView(R.id.iv_main_head)
    CircleImageView iv_head;
    @BindView(R.id.enter_main)
    LinearLayout enter_main;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.saveImg)
    ImageView saveImg;
    @BindView(R.id.getcurrenthelp)
    RecyclerView mRecyclerView;
    RVBaseAdapter<CurrentHelpInfo> adapter;
    List<CurrentHelpInfo> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        ServerUserInfo userInfo=APP.getInstance().getServerUserInfo();
        juli_date.setText("距离升星还有"+userInfo.getLevelMessage()+"日");
        or_help.setText("或救援"+userInfo.getLevelHelp()+"次");
        title.setText("美盟全民救援");
        time.setText(userInfo.getOnlineTime()+"");
        count.setText(userInfo.getHelpNum()+"");
        distance.setText(userInfo.getHelpDistance()+"");
        back.setVisibility(View.GONE);
        saveImg.setImageResource(R.mipmap.ic_common_nav_normal_messag);
        saveImg.setVisibility(View.VISIBLE);
        Glide.with(this).load(APP.getInstance().getServerUserInfo().getImage()).into(iv_head);
        enter_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ServerMainActivity.this,ServerCenterActivity.class));
            }
        });

        adapter= new RVBaseAdapter<CurrentHelpInfo>() {
            @Override
            protected void onViewHolderBound(RVBaseViewHolder holder, int position) {
                //holder.setText(R.id.);
            }
        };getCurrentHelp();
        saveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ServerMainActivity.this, HelpMsgActivity.class));
            }
        });
    }

    @Override
    protected int getContentId() {
        return R.layout.show_server_main;
    }
    public void getCurrentHelp(){
        Map<String ,Object> params=new HashMap<>();
        params.put("latitude",APP.getInstance().getServerUserInfo().getHomeLatitude());
        params.put("longitude",APP.getInstance().getServerUserInfo().getHomeLongitude());
        String token= APP.getInstance().getServerUserInfo().getToken();
        HttpProxy.obtain().get(PlatformContans.ForHelp.sGetCurrentHelp, params,token,new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.e("msg",result);
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(result);
                    int code=jsonObject.getInt("resultCode");
                    if(code==0){
                        JSONArray beanlist=jsonObject.getJSONArray("data");
                        if(beanlist.length()==0){
                            for(int i=0;i<20;i++){
                                CurrentHelpInfo currentHelpInfo=new CurrentHelpInfo();
                                currentHelpInfo.setUseUserName("小明");
                                currentHelpInfo.setUserAddress("广东省云浮跟复古风格市大小县福30号01户");
                                currentHelpInfo.setCreateTime("2018-12-03 21:23:12");
                                currentHelpInfo.setDistance(500);
                                currentHelpInfo.setHelpNum(3);
                                currentHelpInfo.setImage("http://seopic.699pic.com/photo/40005/1721.jpg_wh1200.jpg");
                                list.add(currentHelpInfo);
                            }
                            adapter.setData(list);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(ServerMainActivity.this));
                            mRecyclerView.setAdapter(adapter);
                        }else{
                            for(int i=0;i<beanlist.length();i++){
                                CurrentHelpInfo currentHelpInfo=new CurrentHelpInfo();
                                JSONObject object= (JSONObject) beanlist.get(i);
                                currentHelpInfo.setImage(object.getString("image"));
                                currentHelpInfo.setHelpNum(object.getInt("helpNum"));
                                currentHelpInfo.setDistance(object.getInt("distance"));
                                currentHelpInfo.setCreateTime(object.getString("createTime"));
                                currentHelpInfo.setUseUserName(object.getString("useUserName"));
                                currentHelpInfo.setUserAddress(object.getString("userAddress"));
                                list.add(currentHelpInfo);
                            }
                            adapter.setData(list);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(ServerMainActivity.this));
                            mRecyclerView.setAdapter(adapter);
                        }
                        //Toast.makeText(ClientUserInfoActivity.this,"更新成功",Toast.LENGTH_LONG).show();
                        //finish();
                    }
                    if(code==9999){

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
}
