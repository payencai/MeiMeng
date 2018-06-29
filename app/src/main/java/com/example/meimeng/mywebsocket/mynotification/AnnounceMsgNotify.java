package com.example.meimeng.mywebsocket.mynotification;

import com.google.gson.annotations.SerializedName;

/**
 * 作者：凌涛 on 2018/6/29 10:58
 * 邮箱：771548229@qq..com
 */
public class AnnounceMsgNotify {
    @SerializedName("msg_version")
    private String msgVersion;

    public String getMsgVersion() {
        return msgVersion;
    }

    public void setMsgVersion(String msgVersion) {
        this.msgVersion = msgVersion;
    }
}
