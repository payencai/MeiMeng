package com.example.meimeng.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.adapter.PictureAdapter;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
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
    boolean isHomeEmpty=false;
    boolean isWorkEmpty=false;
    private GridView ask_show_pic;
    private PictureAdapter mAdapter;
    private ArrayList<String> selected = new ArrayList<>();
    private String homelon;
    private String homelat;
    private String worklat;
    private String worklon;
    @BindView(R.id.et_volunteer_name)
    EditText tv_name;
    @BindView(R.id.et_volunteer_number)
    EditText number;
    @BindView(R.id.et_volunteer_home)
    RelativeLayout home;
    @BindView(R.id.et_volunteer_work)
    RelativeLayout work;
    @BindView(R.id.et_volunteer_time)
    RelativeLayout time;
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
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private String isAskServerUser(){
        String value="";
        String servertype=APP.getInstance().getUserInfo().getServerType();
        //String server=APP.getInstance().getUserInfo().getServerUser();
        Log.e("server",APP.getInstance().getUserInfo().getToken()+"");
        switch (servertype){
            case "1":
                value="你还没有申请支援者";
                break;
            case "2":
                value="你的申请审核中,请耐心等候";
                break;
            case "3":
                value="你已经是志愿者";
                break;
            case "4":
                value="你的申请被驳回";
                break;
            default:
                value="你的申请已通过";
                break;
        }
       return value;
    }
    private void showAskDialog(String val) {
        Log.e("val",val);
        final Dialog dialog = new Dialog(this, R.style.dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tishi, null);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        //window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp=window.getAttributes();
        Display display=getWindowManager().getDefaultDisplay();
        //android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = (int) (display.getWidth()*0.7);

        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        dialog.setContentView(dialogView);
        TextView textView=(TextView) dialog.findViewById(R.id.dialog_value);
        textView.setText(val);
        dialog.findViewById(R.id.dialog_iknow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void getUserInfo(){
        HttpProxy.obtain().get(PlatformContans.UseUser.sGetUseUser, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.e("result",result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("resultCode");
                    if (code == 0) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        JSONObject server=object.getJSONObject("serverUser");
                        if (server!=null){
                            String name=server.getString("name");
                            String idNumber=server.getString("idNumber");
                            String wordaddr=server.getString("workAddress");
                            String homeaddr=server.getString("homeAddress");
                            String sex=server.getString("sex");
                            String worktime=server.getString("workTime");
                            Log.e("idNumber",idNumber);
                            tv_name.setText(name);
                            number.setText(idNumber);
                            detailhome.setText(homeaddr);
                            detailwork.setText(wordaddr);
                            if(TextUtils.isEmpty(worktime)){
                                detailtime.setText("单休");
                            }else
                                detailtime.setText(worktime);
                            if(sex.equals("男")){
                                man.setChecked(true);
                                nv.setChecked(false);
                            }

                        }


                    }
                    if (code == 9999) {

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

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        title=findViewById(R.id.title);
        upload=findViewById(R.id.ask_upload);
        ask_show_pic=findViewById(R.id.ask_show_pic);
        title.setText("志愿者招募");
        Drawable drawable= getResources().getDrawable(R.drawable.sex_selector);
        drawable.setBounds(0,0,30,30);//将drawable设置为宽100 高100固定大小
        man.setCompoundDrawables(drawable,null,null,null);
        Drawable drawable2= getResources().getDrawable(R.drawable.sex_selector);
        drawable2.setBounds(0,0,30,30);//将drawable设置为宽100 高100固定大小
        nv.setCompoundDrawables(drawable2,null,null,null);
        nv.setChecked(true);
        showAskDialog(isAskServerUser());
        if(APP.getInstance().getUserInfo().getServerType().equals("3")||APP.getInstance().getUserInfo().getServerType().equals("2")){
            getUserInfo();
        }
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
        if (data != null)
        {
            if (requestCode == 4) {
                AddressBean address = (AddressBean) data.getSerializableExtra("address");
                if(address!=null){
                    String addressStr = address.getAddress();
                    double lon = address.getLon();
                    double lat = address.getLat();
                    homelon=""+lon;
                    homelat=""+lat;
                    Log.d("onActivityResult", "onActivityResult: 经度：" + lon + ",维度:" + lat);
                    if (!TextUtils.isEmpty(addressStr)) {
                        detailhome.setText(addressStr);
                        detailhome.setTextColor(ContextCompat.getColor(this,R.color.text_9));
                    }
                }
                else{
                     isHomeEmpty=true;
                }

            }

        }
        if(data!=null){
            if (requestCode == 5) {
                AddressBean address = (AddressBean) data.getSerializableExtra("address");
                if (address!=null){
                    String addressStr = address.getAddress();

                    double lon = address.getLon();
                    double lat = address.getLat();
                    worklon=""+lon;
                    worklat=""+lat;
                    Log.d("onActivityResult", "onActivityResult: 经度：" + lon + ",维度:" + lat);
                    if (!TextUtils.isEmpty(addressStr)) {
                        detailwork.setText(addressStr);
                        detailwork.setTextColor(ContextCompat.getColor(this,R.color.text_9));
                    }
                }else{
                    isWorkEmpty=true;
                }

            }
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
                    final String msg=object.getString("message");
                    urls = urls+object.getString("data")+",";
                    if (resultCode == 0) {
                        count++;
                        if(count==selected.size()){
                            Log.e("commit",count+"");
                            commit();
                        }
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AskVolunteerActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
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
                SelectAddressActivity.startSelectAddressActivity(this, "address", 4,"");
                //startActivityForResult(new Intent(AskVolunteerActivity.this,SelectAddressActivity.class),4);
                break;
            case R.id.et_volunteer_work:
                SelectAddressActivity.startSelectAddressActivity(this, "address", 5,"");
               // startActivityForResult(new Intent(AskVolunteerActivity.this,SelectAddressActivity.class),5);
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
                if(APP.getInstance().getUserInfo().getServerType().equals("3")||APP.getInstance().getUserInfo().getServerType().equals("2")){
                    Toast.makeText(this,"你已经是志愿者或者已经申请志愿者，请不要重复操作！",Toast.LENGTH_LONG).show();
                    commit.setEnabled(false);
                }else{
                    if(!isInputEmpty()){
                        commitAll();
                    }
                    else{
                        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
                    }

                }
                break;

        }

    }
    private void commitImage(){
            if(selected.size()!=0) {
                for (String filepath : selected) {
                    Log.e("aaa","aaa");
                    upImage(PlatformContans.Image.sUpdateImage, filepath);
                }
            }


    }
    private void commitAll(){
        if(selected.size()==0)
        {
            commit();
        }
        else{
            commitImage();
        }

    }
    String msg="";
    public boolean isInputEmpty(){
        if(TextUtils.equals(detailhome.getText().toString(),"详细家庭地址")){
            msg="家庭地址不能为空";
            return true;
        }
        if(TextUtils.equals(detailwork.getText().toString(),"详细工作地址")){
            msg="工作地址不能为空";
            return true;
        }
        //if("")
        if(TextUtils.isEmpty(tv_name.getText().toString())){
            msg="姓名不能为空";
            return true;
        }
        if(TextUtils.isEmpty(number.getText().toString())){
            msg="身份证号码不能为空";
            return true;
        }

        return false;
    }
    public void commit(){
         String token= APP.getInstance().getUserInfo().getToken();
         String data= returnJsonString();
          HttpProxy.obtain().post(PlatformContans.Serveruser.sAddServerUserByUseUser, token, data, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.e("success",result);
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(result);
                    int code=jsonObject.getInt("resultCode");
                    if(code==0){
                        Toast.makeText(AskVolunteerActivity.this,"申请成功",Toast.LENGTH_LONG).show();
                        count=0;
                        finish();
                    }
                    if(code==2001){
                        Toast.makeText(AskVolunteerActivity.this,"您已申请成为志愿者或已经是志愿者，请勿重复操作",Toast.LENGTH_LONG).show();
                        count=0;
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
    public String returnJsonString(){

        Map<String,Object> params=new HashMap<>();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String name =tv_name.getEditableText().toString();
        String idNumber =number.getEditableText().toString();
        String homeAddress =detailhome.getText().toString();
        String workAddress =detailwork.getText().toString();

        String workTime =detailtime.getText().toString()+"";
        String sex="女";
        String certificateImages="";
        int isCertificate;
        if(selected.size()==0){
            isCertificate=0;
        }else{
            isCertificate=1;
            certificateImages=urls.substring(0,urls.length()-1);
        }
        Log.e("url",urls);
        String workLatitude =worklat;
        String workLongitude=worklon;
        String homeLatitude=homelat;
        String homeLongitude=homelon;
        //String medicineIds="33012750-787c-4880-adad-0c2a8e653ac3,2ff9460d-dbbe-42f5-b8f0-a66ce53cf046,11199602-9e63-40d9-808b-ad467d58e2b5";
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
        params.put("medicineIds","");
        return gson.toJson(params);
    }
}
