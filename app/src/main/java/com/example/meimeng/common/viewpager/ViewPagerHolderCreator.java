package com.example.meimeng.common.viewpager;


/**
 * Created by HIAPAD on 2017/12/1.
 * @param <VH> viewholder生成器
 */
public interface ViewPagerHolderCreator<VH extends ViewPagerHolder> {

    /**
     * 创建viewholder
     * @return
     */
    VH createViewHolder();

}
