package com.example.meimeng.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.meimeng.R;

/**
 * 作者：凌涛 on 2018/7/6 17:38
 * 邮箱：771548229@qq..com
 */
public class UnReadMsgTextView extends View {

    private Paint mPaint;
    private Paint mTextPaint;
    private int mWidthSize;
    private int mHeightSize;
    private int showNumber;
    private int mTextSize = 24;


    public UnReadMsgTextView(Context context) {
        this(context, null);
    }

    public UnReadMsgTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnReadMsgTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UnReadMsgTextView);
        mTextSize = ta.getInt(R.styleable.UnReadMsgTextView_unreadTextSize, 24);
        ta.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
//        mTextPaint.setStrokeWidth(4);//设置画笔宽度
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true); //指定是否使用抗锯齿功能，如果使用，会使绘图速度变慢
//        mTextPaint.setStyle(Paint.Style.FILL);//绘图样式，对于设文字和几何图形都有效
        mTextPaint.setTextAlign(Paint.Align.CENTER);//设置文字对齐方式，取值：align.CENTER、align.LEFT或align.RIGHT
        mTextPaint.setTextSize(mTextSize);//设置文字大小


    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        mHeightSize = MeasureSpec.getSize(heightMeasureSpec);
    }

    public void setShowNumber(int showNumber) {
        this.showNumber = showNumber;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawCircle(mWidthSize / 2, mHeightSize / 2, mWidthSize / 2, mPaint);
        String text = showNumber + "";
        Rect textBounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, getWidth() / 2, getHeight() / 2 + textBounds.height() / 2, mTextPaint);

    }
}
