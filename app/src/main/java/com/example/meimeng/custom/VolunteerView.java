package com.example.meimeng.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private static final int mWidth = 114;
    private static final int mHeight = 138;
    private Bitmap mBitmap;

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        invalidate();
    }

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
        mPaint.setColor(Color.RED);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        if (mBitmap == null) {
            return;
        }
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }
}
