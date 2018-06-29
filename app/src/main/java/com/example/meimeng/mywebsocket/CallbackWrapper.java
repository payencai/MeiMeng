package com.example.meimeng.mywebsocket;

import java.util.concurrent.ScheduledFuture;

/**
 * 作者：凌涛 on 2018/6/29 10:36
 * 邮箱：771548229@qq..com
 */
public class CallbackWrapper {
    private final IWsCallback tempCallback;
    private final ScheduledFuture timeoutTask;
    private final Action action;
    private final Request request;


    public CallbackWrapper(IWsCallback tempCallback, ScheduledFuture timeoutTask, Action action, Request request) {
        this.tempCallback = tempCallback;
        this.timeoutTask = timeoutTask;
        this.action = action;
        this.request = request;
    }


    public IWsCallback getTempCallback() {
        return tempCallback;
    }


    public ScheduledFuture getTimeoutTask() {
        return timeoutTask;
    }


    public Action getAction() {
        return action;
    }


    public Request getRequest() {
        return request;
    }

}
