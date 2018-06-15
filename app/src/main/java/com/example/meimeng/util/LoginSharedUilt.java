package com.example.meimeng.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 作者：凌涛 on 2018/6/15 11:18
 * 邮箱：771548229@qq..com
 */
public class LoginSharedUilt {


    private static LoginSharedUilt sIntance;
    private static Context mContext;
    private SharedPreferences mPreferences;
    public static final String TAG = "LoginSharedUilt";

    private LoginSharedUilt() {
        mPreferences = mContext.getApplicationContext().getSharedPreferences("loginshared", Context.MODE_PRIVATE);
    }

    public static LoginSharedUilt getIntance(Context context) {
        if (sIntance == null) {
            synchronized (LoginSharedUilt.class) {
                if (sIntance == null) {
                    mContext = context.getApplicationContext();
                    sIntance = new LoginSharedUilt();
                }
            }
        }
        return sIntance;
    }


    /**
     * 保存上次的登录状态
     */
    public void saveLastLoginType( int value) {
        mPreferences.edit().putInt("lastLoginType", value).commit();
    }

    public int getLastLoginType() {
        return mPreferences.getInt("lastLoginType", 0);
    }

}
