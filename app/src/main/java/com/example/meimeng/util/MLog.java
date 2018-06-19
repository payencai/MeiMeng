package com.example.meimeng.util;

import android.util.Log;

import com.example.meimeng.APP;

/**
 * 作者：凌涛 on 2018/5/21 16:34
 * 邮箱：771548229@qq..com
 */
public class MLog {

    private static String TAG = "meimeng";
    private static final boolean isDubg = true;

    public static void log(String tag, String content) {
        TAG = tag;
        if (isDubg) {
            d(TAG, content);
        }
    }

    public static void log(String content) {
        if (isDubg) {
            d(TAG, content);
        }
    }

    /**
     * 截断输出日志 * @param msg
     */
    private static void d(String tag, String msg) {
        if (tag == null || tag.length() == 0 || msg == null || msg.length() == 0) return;
        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {// 长度小于等于限制直接打印
            Log.d(tag, msg);
        } else {
            while (msg.length() > segmentSize) {// 循环分段打印日志
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                Log.d(tag, logContent);
            }
            Log.d(tag, msg);// 打印剩余日志
        }
    }


}
