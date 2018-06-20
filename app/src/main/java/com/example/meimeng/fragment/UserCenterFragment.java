package com.example.meimeng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.activity.AboutActivity;
import com.example.meimeng.activity.AddAEDActivity;
import com.example.meimeng.activity.AddGaojiActivity;
import com.example.meimeng.activity.CertActivity;
import com.example.meimeng.activity.ClientRecordActivity;
import com.example.meimeng.activity.ClientUserInfoActivity;
import com.example.meimeng.activity.LoginActivity;
import com.example.meimeng.activity.MainActivity;
import com.example.meimeng.activity.QRCodeActivity;
import com.example.meimeng.activity.RebackActivity;
import com.example.meimeng.activity.ServerRecordActivity;
import com.example.meimeng.activity.ServerUserInfoActivity;
import com.example.meimeng.activity.SettingActivity;
import com.example.meimeng.activity.ShengjiActivity;
import com.example.meimeng.activity.VolunteerActivity;
import com.example.meimeng.activity.MedicineActivity;
import com.example.meimeng.adapter.PictureAdapter;
import com.example.meimeng.base.BaseFragment;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.ServerUserInfoSharedPre;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserCenterFragment extends BaseFragment implements View.OnClickListener {
    private ImageView mClientSetting;
    private TextView mClientUsername;
    private LinearLayout mClientUserinfo;
    private LinearLayout mClientCert;
    private LinearLayout mClientVolunteer;
    private LinearLayout mClientYaopin;
    private LinearLayout mClientRecord;
    private LinearLayout mClientReback;
    private LinearLayout mClientAboutus;
    private LinearLayout mClientQrcode;

    private CircleImageView client_head;

    private ArrayList<String> client_selected = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=null;

        if (APP.sUserType == 0) {
            view = inflater.inflate(R.layout.fragment_usercenter, container, false);
            clientInitView(view);
            clientInitEvent();
        } else {
        }
        return view;
    }



    private void clientInitEvent() {
        mClientQrcode.setOnClickListener(this);
        mClientSetting.setOnClickListener(this);
        mClientAboutus.setOnClickListener(this);
        mClientUserinfo.setOnClickListener(this);
        mClientCert.setOnClickListener(this);
        mClientVolunteer.setOnClickListener(this);
        mClientYaopin.setOnClickListener(this);
        mClientRecord.setOnClickListener(this);
        mClientReback.setOnClickListener(this);
        client_head.setOnClickListener(this);
    }

    private void clientInitView(View view) {
        mClientQrcode=view.findViewById(R.id.layout_qrcode);
        client_head=view.findViewById(R.id.client_cv_head);
        mClientSetting = view.findViewById(R.id.iv_client_settings);
        mClientUsername = view.findViewById(R.id.tv_client_username);
        mClientUserinfo = view.findViewById(R.id.userinfo_client_layout);
        mClientCert = view.findViewById(R.id.certification_client_layout);
        mClientVolunteer = view.findViewById(R.id.volunteer_client_layout);
        mClientYaopin = view.findViewById(R.id.yaopin_client_layout);
        mClientRecord = view.findViewById(R.id.record_client_layout);
        mClientReback = view.findViewById(R.id.reback_client_layout);
        mClientAboutus = view.findViewById(R.id.aboutus_client_layout);
        UserInfo userInfo = APP.getInstance().getUserInfo();
        if (userInfo != null) {
            if (userInfo.getNickname()!=null)
            mClientUsername.setText(userInfo.getNickname() + ",你好");
            else{
                mClientUsername.setText("朵雪花，你好");
            }
        }
        //mAdapter = new PictureAdapter(getActivity(), client_selected);
        Glide.with(this).load(userInfo.getImage()).into(client_head);
    }
    String clientimgurl="";
    private void updateClientUserInfo(String image){
        Map<String,Object> params=new HashMap<>();
        UserInfo userInfo=APP.getInstance().getUserInfo();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String token=APP.getInstance().getUserInfo().getToken();
        //params.put("accountType ",userInfo.getAccountType()+"");
        params.put("province",userInfo.getProvince()+"");
        params.put("area",userInfo.getArea()+"");
        params.put("city",userInfo.getCity()+"");
        params.put("image",image);
        params.put("address",userInfo.getAddress());
        params.put("longitude",userInfo.getLongitude());
        params.put("latitude",userInfo.getLatitude());
        String data=gson.toJson(params);
        Log.e("json",data);
        HttpProxy.obtain().post(PlatformContans.UseUser.sUpdateUseUser, token, data, new ICallBack() {
            @Override
            public void OnSuccess(String result) {

                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(result);
                    String message=jsonObject.getString("message");
                    Log.e("error",message);
                    int code=jsonObject.getInt("resultCode");
                    if(code==0){
                        File file = new File(clientimgurl);
                        Glide.with(getActivity()).load(file).into(client_head);
                    }
                    if(code==9999){
                        //Log.e("error","error");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                 Log.e("error",error);
            }
        });
    }
    public void upImage(String url,  File file,String imgurl) {
        OkHttpClient mOkHttpClent = new OkHttpClient();
        clientimgurl=imgurl;
       // serveriamge=imgurl;
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image",
                        RequestBody.create(MediaType.parse("image/png"), file));
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.e("upload", "onResponse: " + string);
                try {
                    JSONObject object = new JSONObject(string);
                    int resultCode = object.getInt("resultCode");
                     final String data = object.getString("data");
                    if (resultCode == 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(APP.sUserType==0)
                                   updateClientUserInfo(data);
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record_client_layout:
                startActivity(new Intent(getActivity(), ClientRecordActivity.class));
                break;
            case R.id.iv_client_settings:
                startActivity(new Intent(getActivity(), SettingActivity.class));
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
                startActivity(new Intent(getActivity(), MedicineActivity.class));
                break;
            case R.id.reback_client_layout:
                startActivity(new Intent(getActivity(), RebackActivity.class));
                break;
            case R.id.aboutus_client_layout:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;

            case R.id.client_cv_head:
                ImageSelectorUtils.openPhoto(getActivity(),0,false,1,client_selected);
                break;
            case R.id.layout_qrcode:
                startActivity(new Intent(getActivity(), QRCodeActivity.class));
                break;
        }
    }
}
