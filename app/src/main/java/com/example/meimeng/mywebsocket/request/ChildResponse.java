package com.example.meimeng.mywebsocket.request;

/**
 * 作者：凌涛 on 2018/6/29 10:40
 * 邮箱：771548229@qq..com
 */
public class ChildResponse {
    private int code;
    private String msg;
    private String data;

    public boolean isOK(){
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
