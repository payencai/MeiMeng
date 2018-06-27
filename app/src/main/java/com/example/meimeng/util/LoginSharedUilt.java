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

    //保存经度
    public void saveLon(double lon) {
        String lonStr = lon + "";
        mPreferences.edit().putString("lon", lonStr).commit();
    }

    public double getLon() {
        String lon = mPreferences.getString("lon", "0");
        double v = Double.parseDouble(lon);
        return v;
    }
    //保存维度
    public void saveLat(double lat) {
        String latStr = lat + "";
        mPreferences.edit().putString("lat", latStr).commit();
    }

    public double getLat() {
        String lat = mPreferences.getString("lat", "0");
        double v = Double.parseDouble(lat);
        return v;
    }

    public void saveCity(String cityName) {
        mPreferences.edit().putString("cityName", cityName).commit();
    }

    public String getCity() {
        return mPreferences.getString("cityName", "");
    }

    public void saveAddr(String addr) {
        mPreferences.edit().putString("addr", addr).commit();
    }

    public String getAddr() {
        return mPreferences.getString("addr", "");
    }

    //groupid
    public void saveGroupId(String groupid) {
        mPreferences.edit().putString("groupid", groupid).commit();
    }

    public String getGroupId() {
        return mPreferences.getString("groupid", "");
    }

    //保存开始救援时间
    public void saveStartHelpTime(long startTime) {
        mPreferences.edit().putLong("startTime", startTime).commit();
    }

    public long getStartHelpTime() {
        return mPreferences.getLong("startTime", 0);
    }

}
