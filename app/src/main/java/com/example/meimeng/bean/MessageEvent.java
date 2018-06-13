package com.example.meimeng.bean;

public class MessageEvent {
    private boolean msg;
    private int type;

    public MessageEvent(boolean msg) {
        this.msg = msg;
    }

    public MessageEvent(boolean msg, int type) {
        this.msg = msg;
        this.type = type;
    }

    public boolean getMsg() {
        return msg;
    }

    public void setMsg(boolean msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
