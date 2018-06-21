package com.example.meimeng.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.meimeng.R;

public class ImageSelectPopWindow extends PopupWindow implements View.OnClickListener{
    private View mPopView;
    private OnItemClickListener mListener;
    private TextView cancel;
    private TextView camery;
    private LinearLayout empty;
    public View getPopView() {
        return mPopView;
    }

    public void setPopView(View popView) {
        mPopView = popView;
    }

    private TextView gallery;
    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.setOnItemClick(view);
        }
    }

    public ImageSelectPopWindow(Context context) {
        super(context);
        init(context);
        setPopupWindow();
    }
    private void init(Context context){
        LayoutInflater inflater=LayoutInflater.from(context);

        mPopView = inflater.inflate(R.layout.dialog_select_photo, null);
        cancel=mPopView.findViewById(R.id.tv_select_cancel);
        camery=mPopView.findViewById(R.id.tv_select_camera);
        gallery=mPopView.findViewById(R.id.tv_select_gallery);
        empty=mPopView.findViewById(R.id.layout_empty);
        empty.setOnClickListener(this);
        cancel.setOnClickListener(this);
        camery.setOnClickListener(this);
        gallery.setOnClickListener(this);
    }
    @SuppressLint("InlinedApi")
    private void setPopupWindow() {
        this.setContentView(mPopView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(0x4D000000));// 设置背景透明
        mPopView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = mPopView.findViewById(R.id.layout_image_select).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
    public interface OnItemClickListener{
        void setOnItemClick(View v);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }



}
