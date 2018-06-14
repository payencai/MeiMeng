package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.meimeng.util.TimeSelectPopWindow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AskVolunteerActivity extends BaseActivity {
    TextView title;
    ImageView upload;
    private GridView ask_show_pic;
    private PictureAdapter mAdapter;
    private ArrayList<String> selected = new ArrayList<>();
    @BindView(R.id.et_volunteer_name)
    EditText tv_name;
    @BindView(R.id.et_volunteer_number)
    EditText number;
    @BindView(R.id.et_volunteer_home)
    LinearLayout home;
    @BindView(R.id.et_volunteer_work)
    LinearLayout work;
    @BindView(R.id.et_volunteer_time)
    LinearLayout time;
    @BindView(R.id.rg_vol_sex)
    RadioGroup sex;
    @BindView(R.id.rb_vol_nan)
    RadioButton man;
    @BindView(R.id.rb_vol_nv)
    RadioButton nv;
    @BindView(R.id.vol_detail_home)
    TextView detailhome;
    @BindView(R.id.vol_detail_work)
    TextView detailwork;
    @BindView(R.id.vol_detail_time)
    TextView detailtime;
    @BindView(R.id.btn_vol_commit)
    Button commit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        title=findViewById(R.id.title);
        upload=findViewById(R.id.ask_upload);
        ask_show_pic=findViewById(R.id.ask_show_pic);
        title.setText("志愿者招募");
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
                ImageSelectorUtils.openPhoto(AskVolunteerActivity.this,2,false,3,selected);
            }
        });
        initPictureAdapter();
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
        if ( requestCode == 4 && data != null){
            detailhome.setText(data.getExtras().getString("address"));
            detailhome.setTextColor(ContextCompat.getColor(this,R.color.text_333));

        }
        if ( requestCode == 5 && data != null){
            detailwork.setText(data.getExtras().getString("address"));
            detailwork.setTextColor(ContextCompat.getColor(this,R.color.text_333));
        }
    }
    @Override
    protected int getContentId() {
        return R.layout.show_ask_volunteer;
    }

    private void initPictureAdapter() {
        mAdapter = new PictureAdapter(this, selected);
        ask_show_pic.setAdapter(mAdapter);
    }
    String urls="";
    private void upImage(String url, String filePath) {
       // Log.e("tag",url+filePath);
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
                        Toast.makeText(AskVolunteerActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
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
                    urls = urls+object.getString("data")+",";
                    if (resultCode == 0) {


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    @OnClick({R.id.et_volunteer_work,R.id.et_volunteer_home,R.id.et_volunteer_time,R.id.btn_vol_commit})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.et_volunteer_home:
                startActivityForResult(new Intent(AskVolunteerActivity.this,SelectAddressActivity.class),4);
                break;
            case R.id.et_volunteer_work:
                startActivityForResult(new Intent(AskVolunteerActivity.this,SelectAddressActivity.class),5);
                break;
            case R.id.et_volunteer_time:
                final Window window=this.getWindow();
                final TimeSelectPopWindow mPop=new TimeSelectPopWindow(this);
                final WindowManager.LayoutParams params=window.getAttributes();
                params.alpha= (float) 0.5;
                window.setAttributes(params);
                mPop.showAtLocation(AskVolunteerActivity.this.findViewById(R.id.vol_sc), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                mPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        final WindowManager.LayoutParams params=window.getAttributes();
                        params.alpha= (float) 1.0;
                        window.setAttributes(params);

                    }
                });

                mPop.setOnItemClickListener(new TimeSelectPopWindow.OnItemClickListener() {
                    @Override
                    public void setOnItemClick(View v) {
                        switch (v.getId()){
                            case R.id.pop_cancel:
                                mPop.dismiss();
                                detailtime.setText("");
                                break;
                            case R.id.pop_right:
                                detailtime.setText("单休");
                                mPop.dismiss();
                                break;
                            case R.id.tv_dan:
                                detailtime.setText("单休");
                                mPop.dismiss();
                                break;
                            case R.id.tv_shuang:
                                detailtime.setText("双休");
                                mPop.dismiss();
                                break;

                        }
                    }
                });
                break;
            case R.id.btn_vol_commit:
                commit();
                break;
        }

    }
    private void commitImage(){
        if(selected.size()!=0){
            for(String filepath:selected){
                upImage(PlatformContans.Image.sUpdateImage,filepath);
            }
        }
        else{
            Toast.makeText(AskVolunteerActivity.this,"请选择图片",Toast.LENGTH_LONG).show();
        }
    }
    private void commitAll(){

        commitImage();
    }
    public void commit(){
         String token= APP.getInstance().getUserInfo().getToken();
         String data= returnJsonString();
        HttpProxy.obtain().post(PlatformContans.Serveruser.sAddServerUserByUseUser, token, data, new ICallBack() {
            @Override
            public void OnSuccess(String result) {

                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(result);
                    int code=jsonObject.getInt("resultCode");
                    if(code==0){
                        Toast.makeText(AskVolunteerActivity.this,"申请成功",Toast.LENGTH_LONG).show();
                        finish();
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
    public String returnJsonString(){

        Map<String,Object> params=new HashMap<>();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

        String name =tv_name.getEditableText().toString();
        String idNumber =number.getEditableText().toString();
        String homeAddress =detailhome.getText().toString();
        String workAddress =detailwork.getText().toString();
        String workTime ="24:05";
        String sex="女";
        String certificateImages="上传/2018060119125539";
        String isCertificate="1";
        String workLatitude ="23.05177879333496";
        String workLongitude="113.4032974243164";
        String homeLatitude="113.4033126831055";
        String homeLongitude="23.05184555053711";
        String medicineIds="33012750-787c-4880-adad-0c2a8e653ac3,2ff9460d-dbbe-42f5-b8f0-a66ce53cf046,11199602-9e63-40d9-808b-ad467d58e2b5";
        if(man.isChecked()){
            sex="男";
        }
        params.put("name",name);
        params.put("idNumber",idNumber);
        params.put("homeAddress",homeAddress);
        params.put("workAddress",workAddress);
        params.put("workTime",workTime);
        params.put("sex",sex);
        params.put("certificateImages",certificateImages);
        params.put("isCertificate",isCertificate);
        params.put("workLatitude",workLatitude);
        params.put("workLongitude",workLongitude);
        params.put("homeLatitude",homeLatitude);
        params.put("homeLongitude",homeLongitude);
        params.put("medicineIds",medicineIds);
        return gson.toJson(params);
    }
}
