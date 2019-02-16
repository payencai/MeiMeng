package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.donkingliang.imageselector.entry.Image;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.adapter.OptionAdapter;
import com.example.meimeng.adapter.PictureAdapter;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.AddressBean;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.custom.CustomDatePicker;
import com.example.meimeng.custom.KyLoadingBuilder;
import com.example.meimeng.custom.Notepad;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.util.CustomPopWindow;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddAEDActivity extends BaseActivity implements View.OnClickListener {
    private Button submit;
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
    KyLoadingBuilder upload;
    private static final int RQUEST_ADDRESS_CODE = 2;
    private EditText phone;
    private Notepad note;
    @Override
    protected void initView() {
        start=getDate();
        ImageView back;
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        note=findViewById(R.id.note);
        phone=findViewById(R.id.phone);
        submit = findViewById(R.id.submit);
        title = ((TextView) findViewById(R.id.title));
        AEDBrand = ((TextView) findViewById(R.id.AEDBrand));
        deadline = ((TextView) findViewById(R.id.deadline));
        consignSite = ((TextView) findViewById(R.id.consignSite));
        pictureSelector = ((ImageView) findViewById(R.id.pictureSelector));
        imgShowGridView = (GridView) findViewById(R.id.imgShowGridView);
        submit.setOnClickListener(this);

        title.setText("添加AED");
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.option1).setOnClickListener(this);
        findViewById(R.id.option2).setOnClickListener(this);
        findViewById(R.id.option3).setOnClickListener(this);
        pictureSelector.setOnClickListener(this);
        initPictureAdapter();
        initDatePicker();
    }
   private boolean isEmpty(){
      String address=consignSite.getText().toString();
      if(TextUtils.equals(address,"存放详细地址")){
          ToaskUtil.showToast(this,"地址为必填");
          return false;}
      if(selected.size()!=3)
      {
          ToaskUtil.showToast(this,"必须上传三张图片");
          return false;
      }
      return true;
   }
    private void initPictureAdapter() {
        mAdapter = new PictureAdapter(this, selected);
        mAdapter.setOnItemDelListener(new PictureAdapter.OnItemDelListener() {
            @Override
            public void onClick(int position, View view) {
                //imgShowGridView.removeViewAt(position);

                selected.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
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
    private String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = new Date();
        start = sdf.format(date);
        return start;
    }
    String start;
    //弹出选择时间
    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date date = new Date();
        String now = sdf.format(date);
        deadline.setText("请选择AED电池有效期");
        customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                start=time.substring(0,10);
                deadline.setText(time.substring(0,10));
            }
        }, now.substring(0,16), "2099-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        //deadline.setText("请选择AED有效期");
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
                customDatePicker.show(start);
                break;
            case R.id.option3://存放地址
                //startActivityForResult(new Intent(AddAEDActivity.this,ChooseAddressWebActivity.class),3);
                SelectAddressActivity.startSelectAddressActivity(this, "address", RQUEST_ADDRESS_CODE, consignSite.getText().toString(),null);
                break;
            case R.id.pictureSelector://图片选择器
                //限数量的多选(比喻最多9张)
                ImageSelectorUtils.openPhoto(this, REQUEST_PICTURE_CODE, false, 3, selected); // 把已选的传入。
                break;
            case R.id.submit:
                if(isEmpty())
                {
                    upload=openLoadView("提交中");
                    commit();}
                else{

                }
                break;
        }
    }

    private int count = 0;

    private void commit() {

            commitImage();



    }

    private void commitImage() {

        if (selected.size() != 0) {
            for (String filepath : selected) {
               // Log.e("aaa", "aaa");
                upImage(PlatformContans.Image.sUpdateImage, filepath);
            }
        }
    }

    String urls = "";

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
                        Toast.makeText(AddAEDActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                //Log.e("tag", "onResponse: " + string);
                try {
                    JSONObject object = new JSONObject(string);
                    int resultCode = object.getInt("resultCode");
                    urls = urls + object.getString("data") + ",";
                    if (resultCode == 0) {
                        count++;
                        if (count == selected.size()) {
                         //   Log.e("commit", count + "");
                            addAed();
                        }
                    } else {
                        Toast.makeText(AddAEDActivity.this, "你已经上传过改图片", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void addAed() {
        String token = "";
        String data;
        if (APP.sUserType == 0) {
            token = APP.getInstance().getUserInfo().getToken();
            data = returnJson(0);
        } else {
            token = APP.getInstance().getServerUserInfo().getToken();
            data = returnJson(1);
        }
        Log.e("param",data);
        HttpProxy.obtain().post(PlatformContans.AedController.sAddAed, token, data, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                closeLoadView(upload);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("resultCode");
                    String msg = jsonObject.getString("message");
                    if (code == 0) {
                        Toast.makeText(AddAEDActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                        count = 0;
                        finish();

                    }
                    if (code == 2001) {
                        Toast.makeText(AddAEDActivity.this, msg, Toast.LENGTH_LONG).show();
                        finish();
                    }
                    if (code == 9999) {
                        Toast.makeText(AddAEDActivity.this, msg, Toast.LENGTH_LONG).show();
                        finish();
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

    private String returnJson(int type) {
        Map<String, Object> params = new HashMap<>();
        params.put("address", addressName+"("+address.getAddress()+")");
        params.put("brank", AEDBrand.getText().toString());
        params.put("expiryDate", deadline.getText().toString());
        params.put("addressPoint",note.getEditableText().toString());
        params.put("image", urls);
        params.put("tel", phone.getEditableText().toString());
        params.put("isPass", 1);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("role", type);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(params);
    }
    AddressBean address;
    private String longitude;
    private String latitude;
    String addressName;
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
                 address = (AddressBean) data.getSerializableExtra("address");
                if(address!=null){
                    addressName=address.getName();
                    Log.e("name",address.getName());
                    if (address != null) {
                        String addressStr = address.getAddress();
                        double lon = address.getLon();
                        double lat = address.getLat();
                        longitude = lon + "";
                        latitude = lat + "";
                        if (!TextUtils.isEmpty(addressStr)) {
                            consignSite.setText(addressName+addressStr);
                        }
                    }
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
