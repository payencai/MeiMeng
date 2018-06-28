package com.example.meimeng.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by 王松 on 2016/9/19.
 */
public class BitmapUtils {
    public static Bitmap getBitmap(String filePath, int destWidth, int destHeight) {
        /**********************第一次采样，目的就是为了计算缩放比例***************************************/
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置只加载图像的边界到内存中
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //获取原图的宽度
        int outWidth = options.outWidth;
        //获取原图的高度
        int outHeight = options.outHeight;
        Log.d("sang", "btnClick: width:" + outWidth + ";height:" + outHeight);
        //缩放比例，该参数的值必须为2的n次幂
        int simpleSize = 1;
        while (outWidth / simpleSize > destWidth || outHeight / simpleSize > destHeight) {
            simpleSize *= 2;
        }
        /*****************************二次采样开始*******************************/
        //设置不仅只加载图片边界
        options.inJustDecodeBounds = false;
        //设置缩略图缩放比例
        options.inSampleSize = simpleSize;
        //设置图像的色彩模式,四种取值：
        //Bitmap.Config.ALPHA_8 加载只有透明度的图片，在这种色彩模式下，一个像素点占一个字节，图片所占内存大小：宽*高*1
        //Bitmap.Config.ARGB_4444   这种色彩模式下，透明度、红、绿、蓝各占4位，一个像素点占2个字节，图片所占内存大小：宽*高*2
        //Bitmap.Config.ARGB_8888（默认）   这种色彩模式下，透明度、红、绿、蓝各占8位，一个像素点占4个字节，图片所占内存大小：宽*高*4
        //Bitmap.Config.RGB_565 这种色彩模式下，红、绿、蓝各占5、6、5位，一个像素点占2个字节，图片所占内存大小：宽*高*2
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        return bitmap;
    }
}
