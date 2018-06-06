package com.example.meimeng.util;

import android.util.Log;

/**
 * Created by HIAPAD on 2018/3/30.
 */

public class LogUtil {

    private final int VERBOSE = 0;
    private final int DEBUG = 1;
    private final int INFO = 2;
    private final int WARN = 3;
    private final int ERROR = 4;
    private final int ASSERT = 5;

    private int level = DEBUG;


    private static LogUtil mInstance;

    private String TAG;

    private LogUtil() {

    }

    public static LogUtil getInstance() {

        if (mInstance == null) {
            synchronized (LogUtil.class) {
                if (mInstance == null) {
                    mInstance = new LogUtil();
                }
            }
        }
        return mInstance;
    }


    public void log(String tag, String content) {
        if (level <= DEBUG) {
            TAG = tag;
            Log.d(TAG, content);
        }

    }


}
