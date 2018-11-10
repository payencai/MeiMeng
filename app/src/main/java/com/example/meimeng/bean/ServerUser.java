package com.example.meimeng.bean;

import java.util.List;

/**
 * 作者：凌涛 on 2018/6/15 15:29
 * 邮箱：771548229@qq..com
 * 志愿者javabean
 */
public class ServerUser {


    /**
     * account : string
     * age : 0
     * bloodType : string
     * cImages : ["string"]
     * certificateImages : string
     * createTime : 2018-06-15T07:01:23.479Z
     * distance : 0
     * equipmentIp : string
     * examineReason : string
     * fixedLineTelephone : string
     * helpDistance : 0
     * helpNum : 0
     * homeAddress : string
     * homeGeohash : string
     * homeLatitude : string
     * homeLongitude : string
     * hxPwd : string
     * id : string
     * idNumber : string
     * image : string
     * imageKey : string
     * isCancel : 0
     * isCertificate : 0
     * isExamine : 0
     * isUpgrade : 0
     * isUse : 0
     * level : 0
     * levelHelp : 0
     * levelMessage : 0
     * loginTime : 2018-06-15T07:01:23.479Z
     * name : string
     * nickname : string
     * num : 0
     * onlineTime : 0
     * openId : string
     * password : string
     * pushAlias : string
     * sex : string
     * telephone : string
     * token : string
     * workAddress : string
     * workGeohash : string
     * workLatitude : string
     * workLongitude : string
     * workTime : string
     */

    private String account;
    private int age;
    private String bloodType;
    private String certificateImages;
    private String createTime;
    private int distance;
    private String equipmentIp;
    private String examineReason;
    private String fixedLineTelephone;
    private int helpDistance;
    private int helpNum;
    private String homeAddress;
    private String homeGeohash;
    private String homeLatitude;
    private String homeLongitude;
    private String hxPwd;
    private String id;
    private String idNumber;
    private String image;
    private String imageKey;
    private int isCancel;
    private int isCertificate;
    private int isExamine;
    private int isUpgrade;
    private int isUse;
    private int level;
    private int levelHelp;
    private int levelMessage;
    private String loginTime;
    private String name;
    private String nickname;
    private int num;
    private int onlineTime;
    private String openId;
    private String password;
    private String pushAlias;
    private String sex;
    private String telephone;
    private String token;
    private String workAddress;
    private String workGeohash;
    private String workLatitude;
    private String workLongitude;
    private String workTime;
    private List<String> cImages;
    private String loginLatitude;

    public String getLoginLatitude() {
        return loginLatitude;
    }

    public void setLoginLatitude(String loginLatitude) {
        this.loginLatitude = loginLatitude;
    }

    public String getLoginLongitude() {
        return loginLongitude;
    }

    public void setLoginLongitude(String loginLongitude) {
        this.loginLongitude = loginLongitude;
    }

    private String loginLongitude;
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getCertificateImages() {
        return certificateImages;
    }

    public void setCertificateImages(String certificateImages) {
        this.certificateImages = certificateImages;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getEquipmentIp() {
        return equipmentIp;
    }

    public void setEquipmentIp(String equipmentIp) {
        this.equipmentIp = equipmentIp;
    }

    public String getExamineReason() {
        return examineReason;
    }

    public void setExamineReason(String examineReason) {
        this.examineReason = examineReason;
    }

    public String getFixedLineTelephone() {
        return fixedLineTelephone;
    }

    public void setFixedLineTelephone(String fixedLineTelephone) {
        this.fixedLineTelephone = fixedLineTelephone;
    }

    public int getHelpDistance() {
        return helpDistance;
    }

    public void setHelpDistance(int helpDistance) {
        this.helpDistance = helpDistance;
    }

    public int getHelpNum() {
        return helpNum;
    }

    public void setHelpNum(int helpNum) {
        this.helpNum = helpNum;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getHomeGeohash() {
        return homeGeohash;
    }

    public void setHomeGeohash(String homeGeohash) {
        this.homeGeohash = homeGeohash;
    }

    public String getHomeLatitude() {
        return homeLatitude;
    }

    public void setHomeLatitude(String homeLatitude) {
        this.homeLatitude = homeLatitude;
    }

    public String getHomeLongitude() {
        return homeLongitude;
    }

    public void setHomeLongitude(String homeLongitude) {
        this.homeLongitude = homeLongitude;
    }

    public String getHxPwd() {
        return hxPwd;
    }

    public void setHxPwd(String hxPwd) {
        this.hxPwd = hxPwd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public int getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(int isCancel) {
        this.isCancel = isCancel;
    }

    public int getIsCertificate() {
        return isCertificate;
    }

    public void setIsCertificate(int isCertificate) {
        this.isCertificate = isCertificate;
    }

    public int getIsExamine() {
        return isExamine;
    }

    public void setIsExamine(int isExamine) {
        this.isExamine = isExamine;
    }

    public int getIsUpgrade() {
        return isUpgrade;
    }

    public void setIsUpgrade(int isUpgrade) {
        this.isUpgrade = isUpgrade;
    }

    public int getIsUse() {
        return isUse;
    }

    public void setIsUse(int isUse) {
        this.isUse = isUse;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevelHelp() {
        return levelHelp;
    }

    public void setLevelHelp(int levelHelp) {
        this.levelHelp = levelHelp;
    }

    public int getLevelMessage() {
        return levelMessage;
    }

    public void setLevelMessage(int levelMessage) {
        this.levelMessage = levelMessage;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(int onlineTime) {
        this.onlineTime = onlineTime;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPushAlias() {
        return pushAlias;
    }

    public void setPushAlias(String pushAlias) {
        this.pushAlias = pushAlias;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getWorkGeohash() {
        return workGeohash;
    }

    public void setWorkGeohash(String workGeohash) {
        this.workGeohash = workGeohash;
    }

    public String getWorkLatitude() {
        return workLatitude;
    }

    public void setWorkLatitude(String workLatitude) {
        this.workLatitude = workLatitude;
    }

    public String getWorkLongitude() {
        return workLongitude;
    }

    public void setWorkLongitude(String workLongitude) {
        this.workLongitude = workLongitude;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public List<String> getCImages() {
        return cImages;
    }

    public void setCImages(List<String> cImages) {
        this.cImages = cImages;
    }
}
