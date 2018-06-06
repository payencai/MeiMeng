package com.example.meimeng.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.meimeng.APP;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.example.meimeng.bean.LoginAccount.UserInfo;

/**
 * 作者：凌涛 on 2018/6/6 17:55
 * 邮箱：771548229@qq..com
 */
public class UserInfoSharedPre {

    private static UserInfoSharedPre sIntance;
    private static Context mContext;
    private SharedPreferences mPreferences;


    private UserInfoSharedPre() {
        mPreferences = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    public static UserInfoSharedPre getIntance(Context context) {
        if (sIntance == null) {
            synchronized (UserInfoSharedPre.class) {
                if (sIntance == null) {
                    mContext = context.getApplicationContext();
                    sIntance = new UserInfoSharedPre();
                }
            }
        }
        return sIntance;
    }

    public void saveUserInfo(UserInfo userInfo, boolean isSavePassword) {
        APP.getInstance().setUserInfo(userInfo);
        APP.sUserType = 0;
        if (isSavePassword) {
            savePassword(userInfo.getPassword());
        }
    }

    public void saveServerUserInfo(ServerUserInfo userInfo, boolean isSavePassword) {
        APP.getInstance().setServerUserInfo(userInfo);
        APP.sUserType = 1;
        if (isSavePassword) {
            savePassword(userInfo.getPassword());
        }
    }

    public void clearUserInfo() {
        savePassword("");
    }

    public String getPassword() {
        return mPreferences.getString("password", "");

    }

    public void savePassword(String password) {
        mPreferences.edit().putString("password", password).commit();
    }


}
