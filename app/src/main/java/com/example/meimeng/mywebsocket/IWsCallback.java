package com.example.meimeng.mywebsocket;

/**
 * 发送请求ok了接下来需要处理回调的问题了.
 * 虽然在方法sendReq(Action action, T req, ICallback callback,
 * long timeout, int reqCount)中我们已经传入了ui层的回调ICallback,
 * 但这里还需要在封装一层回调处理一些通用逻辑,然后再调用ICallback对应方法.
 *
 * onSuccess与普通的成功回调一样,onError和onTimeout回调中有Request与Action是为了方便后续再次请求操作.
 */
public interface IWsCallback<T> {
    void onSuccess(T t);
    void onError(String msg, Request request, Action action);
    void onTimeout(Request request, Action action);
}