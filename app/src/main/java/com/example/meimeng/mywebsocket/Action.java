package com.example.meimeng.mywebsocket;

/**
 * 作者：凌涛 on 2018/6/29 10:12
 * 邮箱：771548229@qq..com
 */
public enum Action {
    LOGIN("login", 1, null),
    HEARTBEAT("heartbeat", 1, null),
    SYNC("sync", 1, null);

    private String action;
    private int reqEvent;
    private Class respClazz;


    Action(String action, int reqEvent, Class respClazz) {
        this.action = action;
        this.reqEvent = reqEvent;
        this.respClazz = respClazz;
    }


    public String getAction() {
        return action;
    }


    public int getReqEvent() {
        return reqEvent;
    }


    public Class getRespClazz() {
        return respClazz;
    }


}
