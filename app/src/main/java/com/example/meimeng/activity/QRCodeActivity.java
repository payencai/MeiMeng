package com.example.meimeng.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.util.QRCodeUtil;
import com.example.meimeng.util.ToaskUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class QRCodeActivity extends BaseActivity {
    ImageView qrcode;
    TextView title;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        String token="";
        if(APP.sUserType==0){
            token=APP.getInstance().getUserInfo().getToken();
        }else{
            token=APP.getInstance().getServerUserInfo().getToken();
        }
        back =findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title=findViewById(R.id.title);
        title.setText("我的二维码");
        String url="http://47.106.164.34/meiMeng_webui/qrcode_detail.html?toke="+token;
        qrcode=findViewById(R.id.qr_code);
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(url, 480, 480);
        qrcode.setImageBitmap(mBitmap);
        qrcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                saveBitmap(qrcode, Environment.getExternalStorageDirectory() + "/" + new Date().toString() + ".jpg");
                ToaskUtil.showToast(QRCodeActivity.this,"保存成功");
                return false;
            }
        });
    }

    @Override
    protected int getContentId() {
        return R.layout.show_qrcode_content;
    }
    public static void saveBitmap(ImageView view, String filePath) {
        Drawable drawable = view.getDrawable();
        if (drawable == null) {
            return;
        }
        FileOutputStream outStream = null;
        File file = new File(filePath);
        if (file.isDirectory()) {// 如果是目录不允许保存
            return;
        }
        try {
            outStream = new FileOutputStream(file);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
