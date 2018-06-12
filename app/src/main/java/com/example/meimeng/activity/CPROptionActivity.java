package com.example.meimeng.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;

import java.lang.ref.WeakReference;

public class CPROptionActivity extends BaseActivity implements View.OnClickListener {


    private ImageView bodyImg;
    private static final int UPDATA_IMAGE = 0;
    private static final int UPDATA_IMAGE1 = 1;

    private MyHandler mHandler = new MyHandler(this);

    @Override
    protected void initView() {
        bodyImg = (ImageView) findViewById(R.id.bodyImg);
        findViewById(R.id.back).setOnClickListener(this);
        mHandler.sendEmptyMessage(UPDATA_IMAGE1);

    }

    @Override
    protected int getContentId() {
        return R.layout.activity_cproption;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    public static class MyHandler extends Handler {
        private WeakReference<Activity> activity;
        private boolean isUpdata = false;

        public MyHandler(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CPROptionActivity activity = (CPROptionActivity) this.activity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case UPDATA_IMAGE:
                    if (isUpdata) {
                        Glide.with(activity).load(R.mipmap.human_body1).into(activity.bodyImg);
                        isUpdata = false;
                    } else {
                        Glide.with(activity).load(R.mipmap.human_body2).into(activity.bodyImg);
                        isUpdata = true;
                    }
                    sendEmptyMessageDelayed(UPDATA_IMAGE, 250);
                    break;
                case UPDATA_IMAGE1:
                    Glide.with(activity).load(R.mipmap.human_body1).into(activity.bodyImg);
                    isUpdata = false;
                    sendEmptyMessage(UPDATA_IMAGE);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
        mHandler = null;
    }
}
