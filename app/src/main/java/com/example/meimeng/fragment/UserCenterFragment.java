package com.example.meimeng.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.example.meimeng.activity.ServerCenterActivity;
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
import com.example.meimeng.util.ImageSelectPopWindow;
import com.example.meimeng.util.ServerUserInfoSharedPre;
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
                        JSONObject object=jsonObject.getJSONObject("data");
                        String image=object.getString("image");
                        Glide.with(getActivity()).load(image).into(client_head);
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
    public void upImage(String url,  File file) {
        OkHttpClient mOkHttpClent = new OkHttpClient();
        //clientimgurl=imgurl;
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
                    clientimgurl=data;
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

    private void setPopupWindow() {
        final Window window = getActivity().getWindow();

        MainActivity activity= (MainActivity) getActivity();
        final ImageSelectPopWindow mPop = new ImageSelectPopWindow(getActivity());
        final WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = (float) 0.3;
        window.setAttributes(params);
        mPop.showAtLocation(getActivity().findViewById(R.id.server_userinfo_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
                        if (isCameraPermission(getActivity(), 0x007))
                            startCamera();
                        break;
                    case R.id.tv_select_gallery:
                        //upImage(PlatformContans.Image.sUpdateImage,);
                        mPop.dismiss();
                        Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        mIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        mIntent.setType("image/*");
                        getActivity().startActivityForResult(mIntent, 2);
                        break;
                    case R.id.layout_empty:
                        mPop.dismiss();

                }
            }
        });
    }
    public void cropPhoto(Uri inputUri) {
        // 调用系统裁剪图片的 Action
        Intent cropPhotoIntent = new Intent("com.android.camera.action.CROP");
        // 设置数据Uri 和类型
        cropPhotoIntent.setDataAndType(inputUri, "image/*");
        // 授权应用读取 Uri，这一步要有，不然裁剪程序会崩溃
        cropPhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 设置图片的最终输出目录
        cropPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                photoOutputUri = Uri.parse("file:////sdcard/image_output.jpg"));
        getActivity().startActivityForResult(cropPhotoIntent, 3);
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public Uri getPhotoOutputUri() {
        return photoOutputUri;
    }

    public void setPhotoOutputUri(Uri photoOutputUri) {
        this.photoOutputUri = photoOutputUri;
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
            photoUri = FileProvider.getUriForFile(getContext(), "com.example.meimeng.fileprovider", file);
        } else {
            photoUri = Uri.fromFile(file); // Android 7.0 以前使用原来的方法来获取文件的 Uri
        }
        // 打开系统相机的 Action，等同于："android.media.action.IMAGE_CAPTURE"
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 设置拍照所得照片的输出目录
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        getActivity().startActivityForResult(takePhotoIntent, 1);
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
                    Toast.makeText(getContext(), "拍照权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    Uri photoUri;
    Uri photoOutputUri;
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
                setPopupWindow();
                //ImageSelectorUtils.openPhoto(getActivity(),0,false,1,client_selected);
                break;
            case R.id.layout_qrcode:
                startActivity(new Intent(getActivity(), QRCodeActivity.class));
                break;
        }
    }
}
