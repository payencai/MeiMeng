package com.example.meimeng.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.ServerUserInfoSharedPre;
import com.example.meimeng.util.ToaskUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerCenterActivity extends BaseActivity {
    @BindView(R.id.server_cv_head)
    CircleImageView server_head;
    @BindView(R.id.iv_server_settings)
    ImageView mServerSetting;
    @BindView(R.id.tv_server_username)
    TextView mServerUsername;
    @BindView(R.id.userinfo_server_layout)
    LinearLayout mServerUserinfo;
    @BindView(R.id.shengji_server_layout)
    LinearLayout mServerShengji;
    @BindView(R.id.addaed_server_layout)
    LinearLayout mServerAddAed;
    @BindView(R.id.reback_server_layout)
    LinearLayout mServerReback;
    @BindView(R.id.record_server_layout)
    LinearLayout mServerRecord;
    String serveriamge = "";
    private ArrayList<String> server_selected = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        serverInitView();
        // serverInitEvent();
    }

    @Override
    protected int getContentId() {
        return R.layout.server_usercenter;
    }

    private void serverInitView() {

        if (APP.getInstance().getServerUserInfo().getNickname() == null) {
            mServerUsername.setText("朵雪花,你好");
        } else {
            mServerUsername.setText(APP.getInstance().getServerUserInfo().getNickname() + ",你好");
        }

        Log.e("image", APP.getInstance().getServerUserInfo().getImage());
        Glide.with(this).load(APP.getInstance().getServerUserInfo().getImage()).into(server_head);

    }
    @OnClick({R.id.shengji_server_layout,R.id.addaed_server_layout,R.id.reback_server_layout,R.id.iv_server_settings,
            R.id.userinfo_server_layout,R.id.record_server_layout,R.id.server_cv_head})
    void onClick(View view){
        switch (view.getId()){
            case R.id.shengji_server_layout:
                startActivity(new Intent(this, ShengjiActivity.class));
                break;
            case R.id.addaed_server_layout:
                startActivity(new Intent(this, AddAEDActivity.class));
                break;
            case R.id.reback_server_layout:
                startActivity(new Intent(this, RebackActivity.class));
                break;
            case R.id.userinfo_server_layout:
                startActivity(new Intent(this, ServerUserInfoActivity.class));
                break;
            case R.id.record_server_layout:
                startActivity(new Intent(this, ServerRecordActivity.class));
                break;
            case R.id.iv_server_settings:
                ServerUserInfoSharedPre.getIntance(this).clearUserInfo();
                ActivityManager.getInstance().finishAllActivity();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.server_cv_head:
                ToaskUtil.showToast(ServerCenterActivity.this,"上传头像");
                if (isCameraPermission(ServerCenterActivity.this, 0x007))
                    byCamera();
                break;
        }
    }


    public void updateServerUserInfo(String image) {
        String token = APP.getInstance().getServerUserInfo().getToken();
        String data = returnServer(image);
        HttpProxy.obtain().post(PlatformContans.Serveruser.sUpdateServerUser, token, data, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("resultCode");
                    if (code == 0) {
                        File file = new File(serveriamge);
                        Glide.with(ServerCenterActivity.this).load(file).into(server_head);
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

    public void upImage(String url, File file, String imgurl) {
        OkHttpClient mOkHttpClent = new OkHttpClient();
        serveriamge = imgurl;
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
                        Toast.makeText(ServerCenterActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateServerUserInfo(data);
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private String returnServer(String image) {
        Map<String, Object> params = new HashMap<>();
        params.put("image", image);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(params);
    }
    public void takePhoto() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(captureIntent, 1);
    }


    private String imagePath;
    public void byCamera() {
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/camera/";
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (savePath == null || "".equals(savePath)) {
            System.out.println("无法保存照片，请检查SD卡是否挂载");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        //照片命名
        String fileName = timeStamp + ".jpg";
        File out = new File(savePath, fileName);
        Uri uri = Uri.fromFile(out);
        //该照片的绝对路径
        imagePath = savePath + fileName;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 0x008);
    }
    private static String[] PERMISSIONS_CAMERA_AND_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    public static boolean isCameraPermission(Activity context, int requestCode){
        if (Build.VERSION.SDK_INT >= 23) {
            int storagePermission = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            if (storagePermission != PackageManager.PERMISSION_GRANTED || cameraPermission!= PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(context, PERMISSIONS_CAMERA_AND_STORAGE,
                        requestCode);
                return false;
            }
        }
        return true;
    }
    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0x007:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    byCamera();
                } else {
                    Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
