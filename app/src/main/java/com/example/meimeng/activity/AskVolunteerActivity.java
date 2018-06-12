package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.example.meimeng.R;
import com.example.meimeng.adapter.PictureAdapter;
import com.example.meimeng.base.BaseActivity;

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

public class AskVolunteerActivity extends BaseActivity {
    TextView title;
    ImageView upload;
    private GridView ask_show_pic;
    private PictureAdapter mAdapter;
    private ArrayList<String> selected = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
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
    }
    @Override
    protected int getContentId() {
        return R.layout.show_ask_volunteer;
    }

    private void initPictureAdapter() {
        mAdapter = new PictureAdapter(this, selected);
        ask_show_pic.setAdapter(mAdapter);
    }
    private void upImage(String url, String filePath, final int id) {
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
//                        if (mLoadView != null) {
//                            mLoadView.dismiss();
//                        }
                        Toast.makeText(AskVolunteerActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d("lingtaoshiwo", "onResponse: " + string);
                try {
                    JSONObject object = new JSONObject(string);
                    int resultCode = object.getInt("resultCode");
                    final String data = object.getString("data");
                    if (resultCode == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                if (mLoadView != null) {
//                                    mLoadView.dismiss();
//                                }
                                Map<String, Object> params = new HashMap<>();
                                params.put("id", id);
                                params.put("avatar", data);
                                Toast.makeText(AskVolunteerActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                if (mLoadView != null) {
//                                    mLoadView.dismiss();
//                                }
                                Toast.makeText(AskVolunteerActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
//                    if (mLoadView != null) {
//                        mLoadView.dismiss();
//                    }
                    e.printStackTrace();
                }

            }
        });
    }
}
