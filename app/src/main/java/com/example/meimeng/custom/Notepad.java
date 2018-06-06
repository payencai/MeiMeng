package com.example.meimeng.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

/**
 * 作者：凌涛 on 2018/6/1 15:14
 * 邮箱：771548229@qq..com
 */
@SuppressLint("AppCompatCustomView")
public class Notepad extends EditText {

    //横线的线宽
    private int lineWidth = 1;
    //横线的颜色
    private int lineColor = Color.BLACK;
    //行间距
    private int spacing_line = 10;
    //内边距
    private int padding = 10;
    //画笔
    private Paint mPaint;

    public Notepad(Context context) {
        this(context, null);
    }

    public Notepad(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Notepad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusableInTouchMode(true);
        setGravity(Gravity.TOP);
        setLineSpacing(spacing_line, 1);
        setPadding(25, 10, padding, 10);
        mPaint = new Paint();
        mPaint.setColor(lineColor);
        mPaint.setStrokeWidth(lineWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取屏幕的高度
        int viewHeight = getHeight();
        //获取每行文本的高度
        int lineHeight = getLineHeight();
        //计算每页一共有多少行
        int pageCount = viewHeight / lineHeight;
        //循环绘制横线
//        for (int i = 0; i < pageCount; i++) {
//            canvas.drawLine(padding, (i + 1) * lineHeight, getWidth() - padding, (i + 1) * lineHeight, mPaint);
//        }
        //获取当前文本的总行数
//        int lineCount = getLineCount();
        //文本的行数减去每页的行数，剩余的值就是我还要继续绘制的行数
//        int extraCount = lineCount - pageCount;
//        if (extraCount > 0) {
//            for (int i = pageCount; i < lineCount; i++) {
//                canvas.drawLine(padding, (i + 1) * lineHeight, getWidth() - padding, (i + 1) * lineHeight, mPaint);
//            }
//        }
    }
}
