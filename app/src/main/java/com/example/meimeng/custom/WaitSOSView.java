package com.example.meimeng.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 作者：凌涛 on 2018/7/7 16:32
 * 邮箱：771548229@qq..com
 */
public class WaitSOSView extends View {

    private Paint mPaint;

    private int smallCircleRadius = 50;
    private int mStrokeWidth = 24;

    public WaitSOSView(Context context) {
        this(context, null);
    }

    public WaitSOSView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaitSOSView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#DB241A"));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - smallCircleRadius, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(smallCircleRadius, getHeight() / 2, smallCircleRadius, mPaint);
    }
}
