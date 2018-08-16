package com.example.meimeng.constant;

import com.example.meimeng.APP;

import java.util.HashMap;
import java.util.Map;

import retrofit2.http.GET;

/**
 * Created by Administrator on 2017/12/13 0013.
 */

public class PlatformContans {
    public static String newRoot="http://www.wewobang.com/login/";
    public static String root = "http://47.106.164.34/";//正式地址
    public static String rootUrl = root + "memen/";//
//    public static String root = "http://192.168.1.18:8080/";//诗安测试地址
//    public static String rootUrl = root + "memen/";//

    public static class UseUser {
        public static final String sHead = rootUrl + "useuser/";
        public static final String sGetVerificationCode = sHead + "getVerificationCode";//获取验证码
        public static final String sUseUserRegister = sHead + "useUserRegister";//注册
        public static final String sUpdateUserpwd = sHead + "updateUserpwd";//修改密码
        public static final String sLogin = sHead + "login";//用户登录
        public static final String sUpdateRealNameAuthentication = sHead + "updateRealNameAuthentication";//用户实名认证
        public static final String sUpdateUseUser = sHead + "updateUseUser";//更新用户
        public static final String sGetUseUser = sHead + "getUseUser";//登录用户获取。
        public static final String sGetServerUserByUser = sHead + "getServerUserByUser";//获取附近救援人员信息。
        public static final String sEquipment = sHead + "equipment";//获取设备信息。
        public static final String sIsRegister=sHead+"getUserByTelephone";

    }

    public static class Serveruser {
        public static final String sHead = rootUrl + "serveruser/";
        public static final String ServerUserLogin = sHead + "ServerUserLogin";//志愿者登录
        public static final String sUpdateServerUser = sHead + "updateServerUser";//修改服务用户个人信息
        public static final String sServerUserUpgrade = sHead + "serverUserUpgrade";//升级高级急救人员
        public static final String sAddServerUserByUseUser = sHead + "addServerUserByUseUser";
        public static final String sGetServerUser = sHead + "getServerUser";
    }

    public static class AidKnowController {
        public static final String sHead = rootUrl + "aidKnowController/";
        public static final String sGetAidKnowByManage = sHead + "getAidKnowByManage ";//志愿者登录
    }

    public static class AedController {
        public static final String sHead = rootUrl + "aedController/";
        public static final String sAddAed = sHead + "addAed";
        public static final String sGetContent = sHead + "getFacilityByApp";
        public static final String sGetFacility = sHead + "getFacility";
        public static final String sGetAed = sHead + "getAed";
        public static final String sGetDrone = sHead + "getDrone";//呼叫无人机AED

    }

    public static class UserAdvice {
        public static final String sHead = rootUrl + "useradvice/";
        public static final String sAddAdvice = sHead + "addUserAdvice";//反馈意见
    }

    public static class ForHelp {
        public static final String sHead = rootUrl + "forhelp/";
        public static final String sGetCompleteHelpByServerUser = sHead + "getCompleteHelpByServerUser";
        public static final String sGetCompleteHelpByUseUser = sHead + "getCompleteHelpByUseUser";
        public static final String sGetServerMedicine = sHead + "getServerMedicine";
        public static final String sGetCurrentHelp = sHead + "getCurrentHelp";
        public static final String sAddForHelpInfo = sHead + "addForHelpInfo";
        public static final String sUpdateForHelpInfoToCancel = sHead + "updateForHelpInfoToCancel";//取消求救
//        public static final String sUpdateForHelpInfoToEnd = sHead + "updateForHelpInfoToEnd";//完成救助
        public static final String sUpdateForHelpInfoToCancelByServerUser = sHead + "updateForHelpInfoToCancelByServerUser";//救援人员取消求救
        public static final String sUpdateForHelpInfoToEnd = sHead + "updateForHelpInfoToEnd";//救援人员完成救援
        public static final String sUpdateForHelpInfoByGet = sHead + "updateForHelpInfoByGet";//接取求救信息前往救援
    }

    public static class PutMessage {
        public static final String sHead = rootUrl + "putmessage/";
        public static final String sGetPutMessageByManage = sHead + "getPutMessageByManage";
    }

    public static class Medicine {
        public static final String sHead = rootUrl + "medicine/";
        public static final String sGetMedicineByUserId = sHead + "getMedicineByUserId";
        public static final String sUpdateMedicineByManage = sHead + "updateMedicineByManage";
        public static final String sAddMedicineByManage = sHead + "addMedicineByManage";
        public static final String sAddMedicineRelation = sHead + "addMedicineRelation";
        public static final String sGetMedicineByManage = sHead + "getMedicineByManage";
        public static final String sGetMedicineByServer = sHead + "getMedicineByServer";
        public static final String sGetVirtualNumber=sHead+"getVirtualNumber";
    }

    public static class Image {
        public static final String sHead = rootUrl + "image/";
        public static final String sUpdateImage = sHead + "uploadImage";
    }

    public static class MessageController {
        public static final String sHead = rootUrl + "messageController/";
        public static final String sGetSysInfoBytype = sHead + "getSysInfoBytype";
        public static final String sGetSysInfoById = sHead + "getSysInfoById";
        public static final String sAddSysInfo = sHead + "addSysInfo";
        public static final String sAddFeedBack=sHead+"addFeedBack";
    }

}
