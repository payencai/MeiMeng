package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.adapter.PictureAdapter;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddGaojiActivity extends BaseActivity {
    Button commit;

    ImageView upload;
    private GridView gaoji_show_pic;
    private PictureAdapter mAdapter;
    private ArrayList<String> selected = new ArrayList<>();
    TextView title;
    String imgUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        commit=findViewById(R.id.btn_commit);
        upload=findViewById(R.id.iv_gaoji_add);
        gaoji_show_pic=findViewById(R.id.gaoji_show_pic);
        title=findViewById(R.id.title);
        title.setText("高级急救人员");
        ImageView back;
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageSelectorUtils.openPhoto(AddGaojiActivity.this,2,false,1,selected);

            }
        });
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected.size()!=0){
                    for(String filepath:selected){
                        upImage(PlatformContans.Image.sUpdateImage,filepath);
                    }
                }
                else{
                    Toast.makeText(AddGaojiActivity.this,"请选择图片",Toast.LENGTH_LONG).show();
                }
            }
        });
        initPictureAdapter();
    }

    private void initPictureAdapter() {
        mAdapter = new PictureAdapter(this, selected);
        gaoji_show_pic.setAdapter(mAdapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && data != null) {
            //获取选择器返回的数据
            ArrayList<String> images = data.getStringArrayListExtra(
                    ImageSelectorUtils.SELECT_RESULT);
            selected.clear();
            selected.addAll(images);
            mAdapter.updata(images);
        }
    }
    @Override
    protected int getContentId() {
        return R.layout.server_gaoji_jiuyuan;
    }



    private void upImage(String url, String filePath) {
        Log.e("tag",url+filePath);
        OkHttpClient mOkHttpClent = new OkHttpClient();
        File file = new File(filePath);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddGaojiActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.e("tag", "onResponse: " + string);
                try {
                    JSONObject object = new JSONObject(string);
                    int resultCode = object.getInt("resultCode");
                    imgUrl = object.getString("data");
                    if (resultCode == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commit(imgUrl);
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    public void commit(String imgUrl){
        Map<String,Object> params=new HashMap<>();
        params.put("certificateImages",imgUrl);
        params.put("isCertificate",1);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String data = gson.toJson(params);
        HttpProxy.obtain().post(PlatformContans.Serveruser.sServerUserUpgrade, APP.getInstance().getServerUserInfo().getToken(), data, new ICallBack() {
            @Override
            public void OnSuccess(String result)
            {
                try {
                        JSONObject object = new JSONObject(result);
                        int resultCode = object.getInt("resultCode");
                        Log.e("tag",result);
                        if (resultCode == 0) {
                                Toast.makeText(AddGaojiActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        else{
                           Toast.makeText(AddGaojiActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        }
                   }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("tag", "onResponse: " + error);
            }
        });
    }
}
