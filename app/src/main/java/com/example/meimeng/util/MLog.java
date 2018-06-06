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
            Log.d(TAG, content);
        }
    }

    public static void log(String content) {
        if (isDubg) {
            Log.d(TAG, content);
        }
    }

}
