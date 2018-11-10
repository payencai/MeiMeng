package com.example.meimeng.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.ImageSelectPopWindow;
import com.example.meimeng.util.ServerUserInfoSharedPre;
import com.example.meimeng.util.TimeSelectPopWindow;
import com.example.meimeng.util.ToaskUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


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
    @BindView(R.id.comeBack)
    ImageView comeBack;
    @BindView(R.id.tv_server_username)
    TextView mServerUsername;
    @BindView(R.id.userinfo_server_layout)
    LinearLayout mServerUserinfo;
    @BindView(R.id.yaopin_server_layout)
    LinearLayout mServerMedicine;
    @BindView(R.id.shengji_server_layout)
    LinearLayout mServerShengji;
    @BindView(R.id.addaed_server_layout)
    LinearLayout mServerAddAed;
    @BindView(R.id.reback_server_layout)
    LinearLayout mServerReback;
    @BindView(R.id.record_server_layout)
    LinearLayout mServerRecord;
    private ImageSelectPopWindow mImageSelectPopWindow;
    String name;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        serverInitView();
    }
    private void showDialog() {
        final Dialog dialog = new Dialog(this, R.style.dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_photo, null);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        dialog.setContentView(dialogView);
        dialog.findViewById(R.id.tv_select_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tv_select_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (isCameraPermission(ServerCenterActivity.this, 0x007))
                    startCamera();
            }
        });
        dialog.findViewById(R.id.tv_select_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
                mIntent.addCategory(Intent.CATEGORY_OPENABLE);
                mIntent.setType("image/*");
                startActivityForResult(mIntent, 2);
            }
        });
        dialog.show();
    }
    private void setPopupWindow() {
        final Window window = this.getWindow();
        final ImageSelectPopWindow mPop = new ImageSelectPopWindow(this);
        final WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = (float) 0.3;
        window.setAttributes(params);
        //mPop.getAnimationStyle()
        mPop.showAtLocation(ServerCenterActivity.this.findViewById(R.id.server_popup_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        //window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                final WindowManager.LayoutParams params = window.getAttributes();
                params.alpha = (float) 1.0;
                window.setAttributes(params);
                //mPop.getBackground().
                //mPop.getBackground().setAlpha(255);
            }
        });
        mPop.setOnItemClickListener(new ImageSelectPopWindow.OnItemClickListener() {
            @Override
            public void setOnItemClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_select_cancel:
                        mPop.dismiss();
                        break;
                    case R.id.tv_select_camera:
                        mPop.dismiss();
                        if (isCameraPermission(ServerCenterActivity.this, 0x007))
                            startCamera();
                        break;
                    case R.id.tv_select_gallery:
                        //upImage(PlatformContans.Image.sUpdateImage,);
                        mPop.dismiss();
                        Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        mIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        mIntent.setType("image/*");
                        startActivityForResult(mIntent, 2);
                        break;


                }
            }
        });
    }

    @Override
    protected int getContentId() {
        return R.layout.server_usercenter;
    }

    public void getServerUser() {
        HttpProxy.obtain().get(PlatformContans.Serveruser.sGetServerUser, APP.getInstance().getServerUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("resultCode");
                    if (code == 0) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        name = object.getString("nickname");
                        image = object.getString("image");
                        if (!TextUtils.equals("null",name)&&!TextUtils.isEmpty(name)) {
                            mServerUsername.setText(name + " 你好");
                            Log.e("ggg",image+name);
                        } else {
                            mServerUsername.setText("用户"+APP.getInstance().getServerUserInfo().getAccount().substring(7,11)+" 你好");
                        }
                        RequestOptions requestOptions = new RequestOptions()
                                .placeholder(R.mipmap.ic_me_head) //加载中图片
                                .error(R.mipmap.ic_me_head) //加载失败图片
                                .fallback(R.mipmap.ic_me_head) //url为空图片
                                .centerCrop() ;// 填充方式
                        //Log.e("ggg",image+name);
                        Glide.with(ServerCenterActivity.this).load(image).apply(requestOptions).into(server_head);


//                        if (!TextUtils.isEmpty(image)) {
//                            if (server_head != null) {
//                                Glide.with(ServerCenterActivity.this).load(image).into(server_head);
//                            }
//                        }
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

    private void serverInitView() {

        getServerUser();
    }

    @OnClick({R.id.shengji_server_layout, R.id.addaed_server_layout, R.id.reback_server_layout, R.id.iv_server_settings,
            R.id.userinfo_server_layout, R.id.record_server_layout, R.id.server_cv_head, R.id.comeBack,R.id.yaopin_server_layout})
    void onClick(View view) {
        switch (view.getId()) {
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
                startActivityForResult(new Intent(this, ServerUserInfoActivity.class), 4);
                break;
            case R.id.record_server_layout:
                startActivity(new Intent(this, ServerRecordActivity.class));
                break;
            case R.id.iv_server_settings:
                ServerUserInfoSharedPre.getIntance(this).clearUserInfo();
                ActivityManager.getInstance().finishAllActivity();
                Intent intent =new Intent(this, LoginActivity.class);
                Bundle b=new Bundle();
                b.putString("phone",APP.getInstance().getServerUserInfo().getAccount());
                b.putString("pwd",APP.getInstance().getServerUserInfo().getPassword());
                intent.putExtras(b);
                startActivity(intent);
                break;
            case R.id.server_cv_head:
                showDialog();
                break;
            case R.id.comeBack:
                Intent data = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("image", updateimage);
                data.putExtras(bundle);
                setResult(1, data);
                finish();
                break;
            case R.id.yaopin_server_layout:
                startActivity(new Intent(ServerCenterActivity.this,MedicineActivity.class));
                break;
        }
    }

    private void lightoff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("image", updateimage);
        data.putExtras(bundle);
        setResult(1, data);
        super.onBackPressed();
    }

    int count = 0;
    String updateimage = "";

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
                        JSONObject object = jsonObject.getJSONObject("data");
                        String image = object.getString("image");
                        updateimage = image;
                        count++;
                        Glide.with(ServerCenterActivity.this).load(image).into(server_head);
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

    public void upImage(String url, File file) {
        OkHttpClient mOkHttpClent = new OkHttpClient();
        //serveriamge = imgurl;
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


    private String imagePath;

    /**
     * 拍照
     */
    private void startCamera() {
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/";
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }
        if (savePath == null || "".equals(savePath)) {
            System.out.println("无法保存照片，请检查SD卡是否挂载");
            return;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        //照片命名
        String fileName = timeStamp + ".png";
        File file = new File(savePath, fileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /** * 因 Android 7.0 开始，不能使用 file:// 类型的 Uri 访问跨应用文件，否则报异常， * 因此我们这里需要使用内容提供器，FileProvider 是 ContentProvider 的一个子类， * 我们可以轻松的使用 FileProvider 来在不同程序之间分享数据(相对于 ContentProvider 来说) */
        if (Build.VERSION.SDK_INT >= 24) {
            photoUri = FileProvider.getUriForFile(this, "com.example.meimeng.fileprovider", file);
        } else {
            photoUri = Uri.fromFile(file); // Android 7.0 以前使用原来的方法来获取文件的 Uri
        }
        // 打开系统相机的 Action，等同于："android.media.action.IMAGE_CAPTURE"
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 设置拍照所得照片的输出目录
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takePhotoIntent, 1);
    }

    private static String[] PERMISSIONS_CAMERA_AND_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    public static boolean isCameraPermission(Activity context, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            int storagePermission = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            if (storagePermission != PackageManager.PERMISSION_GRANTED || cameraPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, PERMISSIONS_CAMERA_AND_STORAGE,
                        requestCode);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0x007:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // byCamera();
                    startCamera();
                } else {
                    Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    Uri photoUri;
    Uri photoOutputUri;

    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri inputUri) {
        // 调用系统裁剪图片的 Action
        Intent cropPhotoIntent = new Intent("com.android.camera.action.CROP");
        // 设置数据Uri 和类型
        cropPhotoIntent.setDataAndType(inputUri, "image/*");
        // 授权应用读取 Uri，这一步要有，不然裁剪程序会崩溃
        cropPhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 设置图片的最终输出目录
        cropPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                photoOutputUri = Uri.parse("file:////sdcard/image_output.jpg"));
        startActivityForResult(cropPhotoIntent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cropPhoto(photoUri);
            //File file=new File(imagePath);
            //upImage(PlatformContans.Image.sUpdateImage,file);
            //Log.e("TAG", "---------" + FileProvider.getUriForFile(this, "com.xykj.customview.fileprovider", file));
            // server_head.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            cropPhoto(uri);
//            File file=new File(imagePath);
//            upImage(PlatformContans.Image.sUpdateImage,file);
//            //Log.e("TAG", "---------" + FileProvider.getUriForFile(this, "com.xykj.customview.fileprovider", file));
            // server_head.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
        if (requestCode == 3 && data != null) {
            File file = new File(photoOutputUri.getPath());
            if (file.exists()) {
                //Bitmap bitmap = BitmapFactory.decodeFile(photoOutputUri.getPath());
                //server_head.setImageBitmap(bitmap);
                upImage(PlatformContans.Image.sUpdateImage, file);
            } else {
                Toast.makeText(this, "找不到照片", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 4 && data != null) {
            if (!TextUtils.isEmpty(data.getExtras().getString("name"))) {
                mServerUsername.setText(data.getExtras().getString("name") + ",你好");
            }
        }
    }
}
