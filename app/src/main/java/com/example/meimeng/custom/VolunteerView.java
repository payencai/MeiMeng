package com.example.meimeng.custom;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 作者：凌涛 on 2018/7/9 15:46
 * 邮箱：771548229@qq..com
 */
public class VolunteerView extends View {

    private Paint mPaint;

    public VolunteerView(Context context) {
        this(context, null);
    }

    public VolunteerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolunteerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
//        mPaint.
    }
}
