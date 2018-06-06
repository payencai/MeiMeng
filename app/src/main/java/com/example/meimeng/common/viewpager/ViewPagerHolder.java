package com.example.meimeng.common.viewpager;

import android.content.Context;
import android.view.View;

/**
 * Created by HIAPAD on 2017/12/1.
 * @param <T>
 */

public interface ViewPagerHolder<T> {


    /**
     * 创建view
     *
     * @param context
     * @return
     */
    View createView(Context context);


    /**
     * 绑定数据
     *
     * @param context
     * @param position
     * @param data
     */
    void onBind(Context context, int position, T data);

}
