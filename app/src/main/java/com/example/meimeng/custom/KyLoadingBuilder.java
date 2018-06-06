package com.example.meimeng.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class KyLoadingBuilder {
    //OutsideTouchable
    private Context context;
    private BackgroundLinearLayout backgroundLayout;
    private KyLoadingView loadingView;
    //属性
    private int outsideBackgroundColor = 0x55000000;//空白区域背景色
    private boolean outsideTouchable = true;//点击空白区域是否关闭    
    private boolean backTouchable = true;//点击返回键是否关闭

    public KyLoadingBuilder(Context context) {
        this.context = context;
        loadingView = new KyLoadingView(context);
    }

    /**
     * 显示dialog
     */
    public void show() {
        //创建背景布局
        backgroundLayout = new BackgroundLinearLayout(context);
        backgroundLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        backgroundLayout.setGravity(Gravity.CENTER);
        backgroundLayout.setBackgroundColor(outsideBackgroundColor);
        if (loadingView == null) {
            Log.d("lingtao", "show: 说明玩意？？？？？");
        }
        backgroundLayout.addView(loadingView);

        //创建WindowManager
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.format = PixelFormat.RGBA_8888;
        if (!this.backTouchable)
            wlp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//不获得焦点
        wm.addView(backgroundLayout, wlp);
        loadingView.startAnimation();
    }

    /**
     * 关闭
     */
    public void dismiss() {
        try {
            if (backgroundLayout == null) {
                return;
            }
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(backgroundLayout);
            backgroundLayout = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示背景的容器
     *
     * @author pc     *
     */
    private class BackgroundLinearLayout extends LinearLayout {
        public BackgroundLinearLayout(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
                dismiss();
            return super.dispatchKeyEvent(event);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (outsideTouchable && event.getAction() == MotionEvent.ACTION_UP) {
                dismiss();
            }
            return true;
        }
    }


    public void setText(String text) {
        this.loadingView.setText(text);
    }

    public void setIcon(int icon) {
        this.loadingView.setIcon(icon);
    }

    public int getOutsideBackgroundColor() {
        return outsideBackgroundColor;
    }

    public void setOutsideBackgroundColor(int outsideBackgroundColor) {
        this.outsideBackgroundColor = outsideBackgroundColor;
    }

    public boolean isOutsideTouchable() {
        return outsideTouchable;
    }

    public void setOutsideTouchable(boolean outsideTouchable) {
        this.outsideTouchable = outsideTouchable;
    }

    public boolean isBackTouchable() {
        return backTouchable;
    }

    public void setBackTouchable(boolean backTouchable) {
        this.backTouchable = backTouchable;
    }

    public int getTextSize() {
        return this.loadingView.getTextSize();
    }

    public void setTextSize(int textSize) {
        this.loadingView.setTextSize(textSize);
    }

    public int getTextColor() {
        return this.loadingView.getTextColor();
    }

    public void setTextColor(int textColor) {
        this.loadingView.setTextColor(textColor);
    }

    public int getIconWidth() {
        return this.loadingView.getIconWidth();
    }

    public void setIconWidth(int iconWidth) {
        this.loadingView.setIconWidth(iconWidth);
    }

    public int getIconHeight() {
        return this.loadingView.getIconHeight();
    }

    public void setIconHeight(int iconHeight) {
        this.loadingView.setIconHeight(iconHeight);
    }

    public int getCornerRadius() {
        return this.loadingView.getCornerRadius();
    }

    public void setCornerRadius(int cornerRadius) {
        this.loadingView.setCornerRadius(cornerRadius);
    }

    public int getBackgroundColor() {
        return this.loadingView.getBackgroundColor();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.loadingView.setBackgroundColor(backgroundColor);
    }

    public int getSpacing() {
        return this.loadingView.getSpacing();
    }

    public void setSpacing(int spacing) {
        this.loadingView.setSpacing(spacing);
    }

    public int getPaddingLeft() {
        return this.loadingView.getPaddingLeft();
    }

    public void setPaddingLeft(int paddingLeft) {
        this.loadingView.setPaddingLeft(paddingLeft);
    }

    public int getPaddingTop() {
        return this.loadingView.getPaddingTop();
    }

    public void setPaddingTop(int paddingTop) {
        this.loadingView.setPaddingTop(paddingTop);
    }

    public int getPaddingRight() {
        return this.loadingView.getPaddingRight();
    }

    public void setPaddingRight(int paddingRight) {
        this.loadingView.setPaddingRight(paddingRight);
    }

    public int getPaddingBottom() {
        return this.loadingView.getPaddingBottom();
    }

    public void setPaddingBottom(int paddingBottom) {
        this.loadingView.setPaddingBottom(paddingBottom);
    }

    public int getCycle() {
        return this.loadingView.getCycle();
    }

    public void setCycle(int cycle) {
        this.loadingView.setCycle(cycle);
    }

    public void setPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        this.loadingView.setPaddingLeft(paddingLeft);
        this.loadingView.setPaddingTop(paddingTop);
        this.loadingView.setPaddingRight(paddingRight);
        this.loadingView.setPaddingBottom(paddingBottom);
    }
}
