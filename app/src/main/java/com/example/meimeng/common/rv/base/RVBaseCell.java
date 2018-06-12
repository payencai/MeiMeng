package com.example.meimeng.common.rv.base;

import java.io.Serializable;

/**
 * Created by HIAPAD on 2017/12/2.
 */

public abstract class RVBaseCell<T> implements Cell ,Serializable{

    /*T 相当于一个javaBean*/
    public T mData;
    public RVBaseCell(T t) {
        mData = t;
    }

    @Override
    public void releaseResource() {
        // do nothing
        // 如果有需要回收的资源，子类自己实现
    }



}
