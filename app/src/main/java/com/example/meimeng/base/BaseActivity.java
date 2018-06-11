package com.example.meimeng.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.meimeng.R;
import com.example.meimeng.custom.KyLoadingBuilder;
import com.example.meimeng.manager.ActivityManager;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseOnCreate(savedInstanceState);
        if (isFullShow()) {
            //去除标题栏
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //去除状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }
        if (setStatusBarTransparency()) {
            //只有5.1以上的系统支持
            if (Build.VERSION.SDK_INT >= 21) {
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
        int contentId = getContentId();
        setContentView(contentId);
        ActivityManager.getInstance().pushActivity(this);
        initView();
    }

    protected void setBackgroundDrakValue(float value) {
        if (value > 1) {
            value = 1;
        }
        if (value < 0) {
            value = 0;
        }
        Window mWindow = getWindow();
        WindowManager.LayoutParams params = mWindow.getAttributes();
        params.alpha = value;
        mWindow.setAttributes(params);

    }

    /**
     * activity 初始化view
     */
    protected abstract void initView();


    /**
     * @return 每个activity的布局id
     */
    protected abstract int getContentId();

    //需要savedInstanceState的子类自己去重写
    protected void baseOnCreate(Bundle savedInstanceState) {

    }

    /**
     * @return 是否全屏显示
     */
    protected boolean isFullShow() {
        return false;
    }

    /**
     * @return 是否使状态栏透明，默认是false
     */
    protected boolean setStatusBarTransparency() {
        return false;
    }

    /**
     * 添加自定义分割线
     *
     * @param id
     */
    protected void addDividerItem(RecyclerView recyclerView, @DrawableRes int id) {
        if (id == 0) {
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            return;
        }
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.custom_divider));
        divider.setDrawable(ContextCompat.getDrawable(this, id));
        recyclerView.addItemDecoration(divider);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().finishActivity(this);
    }

    protected View setPopupWindow(View view) {


        return view;
    }

    /**
     * 打开loading
     */
    protected KyLoadingBuilder openLoadView(String showText) {
        KyLoadingBuilder mLoadingBuilder = new KyLoadingBuilder(this);
        mLoadingBuilder.setIcon(R.mipmap.loading_32dp);
        if (TextUtils.isEmpty(showText)) {
            mLoadingBuilder.setText("正在加载中...");
        } else {
            mLoadingBuilder.setText(showText);
        }
        //builder.setOutsideTouchable(false);
        //builder.setBackTouchable(true);
        mLoadingBuilder.show();
        return mLoadingBuilder;
    }

    protected void closeLoadView(KyLoadingBuilder mLoadingBuilder) {
        if (mLoadingBuilder != null) {
            mLoadingBuilder.dismiss();
        }
    }

}
