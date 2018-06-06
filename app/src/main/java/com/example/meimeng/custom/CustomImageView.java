package com.example.meimeng.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.meimeng.R;

@SuppressLint("AppCompatCustomView")
public class CustomImageView extends ImageView {
    private int shape;
    private int rotate;
    private final static int CIRCLE = 0;
    private final static int ROUNDRECT = 1;
    private final static int RECT = 2;
    private final static int OVAL = 3;
    private final static int RHOMBUS = 4;
    private final static int HEXAGON = 5;
    private Paint paint;

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView);
        //获取用户设置的shape属性的值
        shape = ta.getInt(R.styleable.CustomImageView_shape, RECT);
        rotate = ta.getInt(R.styleable.CustomImageView_rotate, 0);
        //回收资源
        ta.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //不必调用ImageView自身的方法取绘制图像，否则会出现图像的重叠
//        super.onDraw(canvas);
        //获取用户设置的图片
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        //重置Paint的属性
        paint.reset();
        //获取用户所设置图片对应的Bitmap
        Bitmap srcBitmap = ((BitmapDrawable) drawable).getBitmap();
        //获取控件的宽高
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        //获取原图的宽和高
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = Math.max(measuredWidth * 1f / width, measuredHeight * 1f / height);
        matrix.postScale(scale, scale);
        Bitmap bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, width, height, matrix, true);

        //创建一个和控件宽高大小一致的空白的Bitmap
        Bitmap blankBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //canvas所绘制的所有图像都将出现在blankBitmap
        Canvas mCanvas = new Canvas(blankBitmap);

        mCanvas.save();
        mCanvas.rotate(rotate, measuredWidth / 2, measuredHeight / 2);
        switch (shape) {
            case CIRCLE:
                mCanvas.drawCircle(measuredWidth / 2, measuredHeight / 2, measuredWidth / 2, paint);
                break;
            case ROUNDRECT:
                //绘制一个圆角矩形
                mCanvas.drawRoundRect(new RectF(0, 0, measuredWidth, measuredHeight), 10, 10, paint);
                break;
            case RECT:
                mCanvas.drawRect(0, 0, measuredWidth, measuredHeight, paint);
                break;
            case OVAL:
                //画椭圆
                if (Build.VERSION.SDK_INT > 20) {
                    mCanvas.drawOval(0, 0, measuredWidth, measuredHeight, paint);
                } else {
                    mCanvas.drawOval(new RectF(0, 0, measuredWidth, measuredHeight), paint);
                }
                break;
            case RHOMBUS: {
                Path path = new Path();
                path.moveTo(measuredWidth / 2, 0);
                path.lineTo(measuredWidth, measuredHeight / 2);
                path.lineTo(measuredWidth / 2, measuredHeight);
                path.lineTo(0, measuredHeight / 2);
                path.close();
                mCanvas.drawPath(path, paint);
            }
            break;
            case HEXAGON: {
                Path path = new Path();
                path.moveTo(measuredWidth / 2, 0);
                path.lineTo(measuredWidth, measuredHeight / 4);
                path.lineTo(measuredWidth, measuredHeight * 0.75f);
                path.lineTo(measuredWidth / 2, measuredHeight);
                path.lineTo(0, measuredHeight * 0.75f);
                path.lineTo(0, measuredHeight / 4);
                path.close();
                mCanvas.drawPath(path, paint);
            }

            break;
        }
        mCanvas.restore();
        //设置paint，使之取图像1和图像2的交集
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //绘制一个Bitmap图像
        mCanvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawBitmap(blankBitmap, 0, 0, null);
//        if (blankBitmap != null && !blankBitmap.isRecycled()) {
//            blankBitmap.recycle();
//        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
