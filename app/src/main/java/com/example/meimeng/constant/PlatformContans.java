package com.example.meimeng.constant;

import com.example.meimeng.APP;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/13 0013.
 */

public class PlatformContans {

    public static String root = "http://47.106.164.34:8080/";//正式地址
    public static String rootUrl = root + "memen/";//
//    public static String root = "http://192.168.1.18:8080/";//诗安测试地址
//    public static String rootUrl = root + "memen/";//

    public static class UseUser {
        public static final String sHead = rootUrl + "useuser/";
        public static final String sGetVerificationCode = sHead + "getVerificationCode";//获取验证码
        public static final String sUseUserRegister = sHead + "useUserRegister";//注册
        public static final String sUpdateUserpwd = sHead + "updateUserpwd";//修改密码
        public static final String sLogin = sHead + "login";//用户登录
        public static final String sUpdateRealNameAuthentication=sHead+"updateRealNameAuthentication";//用户实名认证
        public static final String sUpdateUseUser=sHead+"updateUseUser";//更新用户

    }

    public static class Serveruser {
        public static final String sHead = rootUrl + "serveruser/";
        public static final String ServerUserLogin = sHead + "ServerUserLogin";//志愿者登录
    }
    public static class UserAdvice{
        public static final String sHead=rootUrl+"useradvice/";
        public static final String sAddAdvice=sHead+"addUserAdvice";//反馈意见
    }
    public static class ForHelp{
        public static final String sHead=rootUrl+"forhelp/";
        public static final String sGetCompleteHelpByServerUser=sHead+"getCompleteHelpByServerUser";
        public static final String sGetCompleteHelpByUseUser=sHead+"getCompleteHelpByUseUser";
    }
    public static class Medicine{
        public static final String sHead=rootUrl+"medicine/";
        public static final String sGetMedicineByUserId=sHead+"getMedicineByUserId";
    }
}
