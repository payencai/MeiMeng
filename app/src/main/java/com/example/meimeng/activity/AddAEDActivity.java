package com.example.meimeng.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.donkingliang.imageselector.ImageSelectorActivity;
import com.donkingliang.imageselector.PreviewActivity;
import com.donkingliang.imageselector.entry.Folder;
import com.donkingliang.imageselector.entry.Image;
import com.donkingliang.imageselector.model.ImageModel;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.example.meimeng.R;
import com.example.meimeng.adapter.OptionAdapter;
import com.example.meimeng.adapter.PictureAdapter;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.custom.CustomDatePicker;
import com.example.meimeng.util.CustomPopWindow;
import com.example.meimeng.util.ToaskUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddAEDActivity extends BaseActivity implements View.OnClickListener {

    private TextView title;
    private TextView AEDBrand;
    private TextView deadline;
    private TextView consignSite;
    private ImageView pictureSelector;
    private CustomDatePicker customDatePicker;
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int REQUEST_PICTURE_CODE = 1;
    private ArrayList<String> selected = new ArrayList<>();
    //    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private GridView imgShowGridView;
    private PictureAdapter mAdapter;
    private static final int RQUEST_ADDRESS_CODE = 2;

    @Override
    protected void initView() {
        ImageView back;
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title = ((TextView) findViewById(R.id.title));
        AEDBrand = ((TextView) findViewById(R.id.AEDBrand));
        deadline = ((TextView) findViewById(R.id.deadline));
        consignSite = ((TextView) findViewById(R.id.consignSite));
        pictureSelector = ((ImageView) findViewById(R.id.pictureSelector));
        imgShowGridView = (GridView) findViewById(R.id.imgShowGridView);


        title.setText("添加AED");
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.option1).setOnClickListener(this);
        findViewById(R.id.option2).setOnClickListener(this);
        findViewById(R.id.option3).setOnClickListener(this);
        pictureSelector.setOnClickListener(this);
        initPictureAdapter();
        initDatePicker();
    }

    private void initPictureAdapter() {
        mAdapter = new PictureAdapter(this, selected);
        imgShowGridView.setAdapter(mAdapter);
//        imgShowGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mSelectImages.clear();
//                for (String url : selected) {
//                    File file = new File(url);
//                    long l = file.lastModified();
//                    mSelectImages.add(new Image(url, l, file.getName()));
//                }
//                toPreviewActivity(mSelectImages, position);
//            }
//        });
    }

    //弹出选择时间
    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date date = new Date();
        String now = sdf.format(date);
        deadline.setText(now);
        customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                deadline.setText(time);
            }
        }, now, "2099-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker.showSpecificTime(false); // 显示时和分
        customDatePicker.setIsLoop(true); // 允许循环滚动


    }

    @Override
    protected int getContentId() {
        return R.layout.activity_add_aed;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.option1:
                showPwBrandSelector(v);
                break;
            case R.id.option2://选择时间
                customDatePicker.show(deadline.getText().toString());
                break;
            case R.id.option3://存放地址
                SelectAddressActivity.startSelectAddressActivity(this, "address", RQUEST_ADDRESS_CODE, consignSite.getText().toString());
                break;
            case R.id.pictureSelector://图片选择器
                //限数量的多选(比喻最多9张)
                ImageSelectorUtils.openPhoto(this, REQUEST_PICTURE_CODE, false, 3, selected); // 把已选的传入。
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CODE && data != null) {
            //获取选择器返回的数据
            ArrayList<String> images = data.getStringArrayListExtra(
                    ImageSelectorUtils.SELECT_RESULT);
            selected.clear();
            selected.addAll(images);
            mAdapter.updata(images);
        }
        if (data != null) {
            if (requestCode == RQUEST_ADDRESS_CODE) {
                AddressBean address = (AddressBean) data.getSerializableExtra("address");
                String addressStr = address.getAddress();
                double lon = address.getLon();
                double lat = address.getLat();
                Log.d("onActivityResult", "onActivityResult: 经度：" + lon + ",维度:" + lat);
                if (!TextUtils.isEmpty(addressStr)) {
                    consignSite.setText(addressStr);
                }
            }
        }
    }

    private void toPreviewActivity(ArrayList<Image> images, int position) {
        if (images != null && !images.isEmpty()) {
//            PreviewActivity.openActivity(this, images, mSelectImages, false, 3, position);
        }
    }


    private void showPwBrandSelector(View view) {
        View brandView = LayoutInflater.from(this).inflate(R.layout.pw_list_selector, null);
        CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(brandView)
                .sizeByPercentage(this, 0.7f, 0.6f)
                .setOutsideTouchable(true)
                .enableBackgroundDark(true)
                .setAnimationStyle(R.style.CustomPopWindowStyle)
                .setBgDarkAlpha(0.5f)
                .create();
        handlerBrandView(this, brandView, customPopWindow);
        customPopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void handlerBrandView(final Context context, View view,
                                  final CustomPopWindow customPopWindow) {
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
            }
        });
        ListView pwList = (ListView) view.findViewById(R.id.pwList);
        List<String> list = new ArrayList<>();
        list.add("飞利浦（PHILIPS）");
        list.add("卓尔（ZOLL）");
        list.add("迈瑞（mindray）");
        list.add("普美康（PRIMEDIC）");
        list.add("日本光电（NIHON KOHDEN）");
        list.add("瑞士席勒（SCHILLER）");
        list.add("捷斯特（CHEST）");
        list.add("其它");
        OptionAdapter adapter = new OptionAdapter(context, list);
        pwList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemString = (String) parent.getItemAtPosition(position);
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
                if (position != 7) {
                    AEDBrand.setText(itemString);
                } else {
                    showOtherSelectView(view);
                }
//                if (!itemString.equals("其他")) {
//                } else {
//                    showOtherSelectView(view);
//                }

            }
        });
        pwList.setAdapter(adapter);
    }

    private void showOtherSelectView(View view) {
        View otherView = LayoutInflater.from(this).inflate(R.layout.pw_aed_other_layout, null);
        CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(otherView)
                .sizeByPercentage(this, 0.7f, 0)
                .setOutsideTouchable(true)
                .enableBackgroundDark(true)
                .setAnimationStyle(R.style.CustomPopWindowStyle)
                .setBgDarkAlpha(0.5f)
                .create();
        handlerOtherView(this, otherView, customPopWindow);
        customPopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void handlerOtherView(final Activity activity, View view,
                                  final CustomPopWindow customPopWindow) {

        final EditText otherInputView = (EditText) view.findViewById(R.id.otherInput);
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
            }
        });
        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = otherInputView.getEditableText().toString();
                if (TextUtils.isEmpty(input)) {
                    ToaskUtil.showToast(activity, "请输入品牌");
                    return;
                } else {
                    if (customPopWindow != null) {
                        customPopWindow.dissmiss();
                    }
                    AEDBrand.setText(input);
                }
            }
        });

    }


}
