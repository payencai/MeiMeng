package com.example.meimeng.mywebsocket;

/**
 * 作者：凌涛 on 2018/6/29 10:12
 * 邮箱：771548229@qq..com
 */
public interface ICallback<T> {

    void onSuccess(T t);

    void onFail(String msg);

}