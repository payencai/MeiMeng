package com.example.meimeng.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.example.meimeng.R;
import com.example.meimeng.adapter.PictureAdapter;
import com.example.meimeng.base.BaseActivity;

import java.util.ArrayList;

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
}
