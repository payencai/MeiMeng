package com.example.meimeng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.activity.AboutActivity;
import com.example.meimeng.activity.AddAEDActivity;
import com.example.meimeng.activity.CertActivity;
import com.example.meimeng.activity.ClientRecordActivity;
import com.example.meimeng.activity.ClientUserInfoActivity;
import com.example.meimeng.activity.LoginActivity;
import com.example.meimeng.activity.RebackActivity;
import com.example.meimeng.activity.ServerUserInfoActivity;
import com.example.meimeng.activity.SettingActivity;
import com.example.meimeng.activity.ShengjiActivity;
import com.example.meimeng.activity.VolunteerActivity;
import com.example.meimeng.activity.YaopinActivity;
import com.example.meimeng.base.BaseFragment;
import com.example.meimeng.bean.LoginAccount.UserInfo;

public class UserCenterFragment extends BaseFragment implements View.OnClickListener{
    private ImageView mClientSetting;
    private TextView mClientUsername;
    private LinearLayout mClientUserinfo;
    private LinearLayout mClientCert;
    private LinearLayout mClientVolunteer;
    private LinearLayout mClientYaopin;
    private LinearLayout mClientRecord;
    private LinearLayout mClientReback;
    private LinearLayout mClientAboutus;


    private ImageView mServerSetting;
    private TextView mServerUsername;
    private LinearLayout mServerUserinfo;
    private LinearLayout mServerShengji;
    private LinearLayout mServerAddAed;
    private LinearLayout mServerReback;
    private LinearLayout mServerRecord;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=null;

        if (APP.sUserType == 0) {
            view = inflater.inflate(R.layout.fragment_usercenter, container, false);
            clientInitView(view);
            clientInitEvent();
        } else {
            view = inflater.inflate(R.layout.server_usercenter, container, false);
            serverInitView(view);
            serverInitEvent();
        }
        return view;
    }

    private void serverInitEvent() {
        mServerSetting.setOnClickListener(this);
        mServerAddAed.setOnClickListener(this);
        mServerReback.setOnClickListener(this);
        mServerRecord.setOnClickListener(this);
        mServerShengji.setOnClickListener(this);
        mServerUserinfo.setOnClickListener(this);
    }

    private void serverInitView(View view) {
        mServerUsername=view.findViewById(R.id.tv_server_username);
        mServerSetting=view.findViewById(R.id.iv_server_settings);
        mServerAddAed=view.findViewById(R.id.addaed_server_layout);
        mServerReback=view.findViewById(R.id.reback_server_layout);
        mServerRecord=view.findViewById(R.id.record_server_layout);
        mServerShengji=view.findViewById(R.id.shengji_server_layout);
        mServerUserinfo=view.findViewById(R.id.userinfo_server_layout);
        mServerUsername.setText(APP.getInstance().getServerUserInfo().getName()+",你好");
    }

    private void clientInitEvent() {
        mClientSetting.setOnClickListener(this);
        mClientAboutus.setOnClickListener(this);
        mClientUserinfo.setOnClickListener(this);
        mClientCert.setOnClickListener(this);
        mClientVolunteer.setOnClickListener(this);
        mClientYaopin.setOnClickListener(this);
        mClientRecord.setOnClickListener(this);
        mClientReback.setOnClickListener(this);
    }

    private void clientInitView(View view) {

        mClientSetting=view.findViewById(R.id.iv_client_settings);
        mClientUsername=view.findViewById(R.id.tv_client_username);
        mClientUserinfo=view.findViewById(R.id.userinfo_client_layout);
        mClientCert=view.findViewById(R.id.certification_client_layout);
        mClientVolunteer=view.findViewById(R.id.volunteer_client_layout);
        mClientYaopin=view.findViewById(R.id.yaopin_client_layout);
        mClientRecord=view.findViewById(R.id.record_client_layout);
        mClientReback=view.findViewById(R.id.reback_client_layout);
        mClientAboutus=view.findViewById(R.id.aboutus_client_layout);
        mClientUsername.setText(APP.getInstance().getUserInfo().getName()+",你好");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.record_client_layout:
                startActivity(new Intent(getActivity(), ClientRecordActivity.class));
                break;
            case R.id.iv_client_settings:
                startActivity(new Intent(getActivity(),SettingActivity.class));
                break;
            case R.id.userinfo_client_layout:
                startActivity(new Intent(getActivity(), ClientUserInfoActivity.class));
                break;
            case R.id.certification_client_layout:
                startActivity(new Intent(getActivity(), CertActivity.class));
                break;
            case R.id.volunteer_client_layout:
                startActivity(new Intent(getActivity(), VolunteerActivity.class));
                break;
            case R.id.yaopin_client_layout:
                startActivity(new Intent(getActivity(), YaopinActivity.class));
                break;
            case R.id.reback_client_layout:
                startActivity(new Intent(getActivity(), RebackActivity.class));
                break;
            case R.id.aboutus_client_layout:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.shengji_server_layout:
                startActivity(new Intent(getActivity(), ShengjiActivity.class));
                break;
            case R.id.addaed_server_layout:
                startActivity(new Intent(getActivity(), AddAEDActivity.class));
                break;
            case R.id.reback_server_layout:
                startActivity(new Intent(getActivity(), RebackActivity.class));
                break;
            case R.id.userinfo_server_layout:
                startActivity(new Intent(getActivity(), ServerUserInfoActivity.class));
                break;
            case R.id.record_server_layout:
                break;
            case R.id.iv_server_settings:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
        }
    }
}
