package com.example.meimeng.mywebsocket.request;

import com.google.gson.annotations.SerializedName;

/**
 * 作者：凌涛 on 2018/6/29 10:40
 * 邮箱：771548229@qq..com
 */
public class Response {

    @SerializedName("resp_event")
    private int respEvent;

    @SerializedName("seq_id")
    private String seqId;

    private String action;
    private String resp;

    public int getRespEvent() {
        return respEvent;
    }

    public void setRespEvent(int respEvent) {
        this.respEvent = respEvent;
    }

    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }
}
