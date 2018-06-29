package com.example.meimeng.mywebsocket.mynotification;

/**
 * 作者：凌涛 on 2018/6/29 10:54
 * 邮箱：771548229@qq..com
 */
public interface INotifyListener<T> {
    void fire(T t);
}
