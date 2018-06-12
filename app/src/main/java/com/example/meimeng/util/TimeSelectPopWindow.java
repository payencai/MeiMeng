package com.example.meimeng.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.meimeng.R;

public class TimeSelectPopWindow extends PopupWindow implements View.OnClickListener{
    private View mPopView;
    private OnItemClickListener mListener;
    private TextView cancel;
    private TextView queding;
    private TextView dan;
    private TextView shuang;
    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.setOnItemClick(view);
        }
    }

    public TimeSelectPopWindow(Context context) {
        super(context);
        init(context);
        setPopupWindow();
    }
    private void init(Context context){
        LayoutInflater inflater=LayoutInflater.from(context);
        mPopView = inflater.inflate(R.layout.bottom_time_select, null);
        cancel=mPopView.findViewById(R.id.pop_cancel);
        queding=mPopView.findViewById(R.id.pop_right);
        dan=mPopView.findViewById(R.id.tv_dan);
        shuang=mPopView.findViewById(R.id.tv_shuang);
        cancel.setOnClickListener(this);
        queding.setOnClickListener(this);
        dan.setOnClickListener(this);
        shuang.setOnClickListener(this);
    }
    @SuppressLint("InlinedApi")
    private void setPopupWindow() {
        this.setContentView(mPopView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(420);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(0xffffffff));// 设置背景透明
        mPopView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = mPopView.findViewById(R.id.id_pop_layout).getTop();
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
