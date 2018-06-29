package com.example.meimeng.mywebsocket;

/**
 * 作者：凌涛 on 2018/6/29 10:27
 * 邮箱：771548229@qq..com
 */
public class CallbackDataWrapper<T> {

    private ICallback<T> callback;
    private Object data;

    public CallbackDataWrapper(ICallback<T> callback, Object data) {
        this.callback = callback;
        this.data = data;
    }

    public ICallback<T> getCallback() {
        return callback;
    }


    public void setCallback(ICallback<T> callback) {
        this.callback = callback;
    }


    public Object getData() {
        return data;
    }


    public void setData(Object data) {
        this.data = data;
    }
}