package com.example.meimeng.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.meimeng.APP;
import com.example.meimeng.bean.LoginAccount.ServerUserInfo;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.lang.reflect.Field;

/**
 * 作者：凌涛 on 2018/6/6 17:55
 * 邮箱：771548229@qq..com
 */
public class ServerUserInfoSharedPre {

    private static ServerUserInfoSharedPre sIntance;
    private static Context mContext;
    private SharedPreferences mPreferences;
    public static final String TAG = "ServerUserSharedPre";


    private ServerUserInfoSharedPre() {
        mPreferences = mContext.getSharedPreferences("serviceuserInfo", Context.MODE_PRIVATE);
    }

    public static ServerUserInfoSharedPre getIntance(Context context) {
        if (sIntance == null) {
            synchronized (ServerUserInfoSharedPre.class) {
                if (sIntance == null) {
                    mContext = context.getApplicationContext();
                    sIntance = new ServerUserInfoSharedPre();
                }
            }
        }
        return sIntance;
    }


    public void saveServerUserInfo(ServerUserInfo userInfo, boolean isSavePassword) {
        APP.getInstance().setServerUserInfo(userInfo);
        EaseUserUtils.saveMeUserHeadUrl(userInfo.getImage());
        APP.sUserType = 1;
        saveServerUserFields(userInfo);
        LoginSharedUilt.getIntance(mContext).saveLastLoginType(1);
    }

    public void clearUserInfo() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void saveServerUserFields(ServerUserInfo userInfo) {
        Class<ServerUserInfo> clazz = ServerUserInfo.class;
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

    public Object getServerUserFiledValue(String filedName) {
        Class<ServerUserInfo> clazz = ServerUserInfo.class;
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
