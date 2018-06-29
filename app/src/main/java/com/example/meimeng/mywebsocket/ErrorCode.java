package com.example.meimeng.mywebsocket;

/**
 * 作者：凌涛 on 2018/6/29 10:43
 * 邮箱：771548229@qq..com
 */
public enum ErrorCode {

    BUSINESS_EXCEPTION("业务异常"),
    PARSE_EXCEPTION("数据格式异常"),
    DISCONNECT_EXCEPTION("连接断开");

    private String msg;

    ErrorCode(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
