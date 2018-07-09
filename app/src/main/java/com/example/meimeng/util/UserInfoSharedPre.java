package com.example.meimeng.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.meimeng.APP;
import com.example.meimeng.bean.LoginAccount.UserInfo;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.lang.reflect.Field;

import static com.example.meimeng.common.rv.absRv.AbsBaseActivity.TAG;

/**
 * 作者：凌涛 on 2018/6/6 17:55
 * 邮箱：771548229@qq..com
 */
public class UserInfoSharedPre {

    private static UserInfoSharedPre sIntance;
    private static Context mContext;
    private SharedPreferences mPreferences;
    public static final String TAG = "UserInfoSharedPre";


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
        EaseUserUtils.saveMeUserHeadUrl(userInfo.getImage());
        APP.sUserType = 0;
        saveUserFields(userInfo);
        LoginSharedUilt.getIntance(mContext).saveLastLoginType(0);
    }

    public void clearUserInfo() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear();
        editor.commit();
    }


    private void saveUserFields(UserInfo userInfo) {
        Class<UserInfo> clazz = UserInfo.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            String name = type.getName();
            String key = field.getName();
            Log.d(TAG, "枚举到的field:" + name + "  " + key);
            try {
                if (name.equals("int")) {
                    int value = (int) field.get(userInfo);
                    mPreferences.edit().putInt(key, value).commit();
                } else if (name.equals("java.lang.String")) {
                    String value = (String) field.get(userInfo);
                    mPreferences.edit().putString(key, value).commit();
                }
            } catch (Exception e) {

            }

        }
    }

    public Object getUserInfoFiledValue(String filedName) {
        Class<UserInfo> clazz = UserInfo.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            String name = type.getName();//对应UserInfo的字段的类型
            String key = field.getName();
            try {
                if (filedName.equals(key)) {//获取对应的字段的名称
                    if (name.equals(int.class.getName())) {
                        return mPreferences.getInt(key, -1);
                    } else if (name.equals(String.class.getName())) {
                        return mPreferences.getString(key, "");
                    }
                }
            } catch (Exception e) {

            }

        }
        return null;

    }


}
