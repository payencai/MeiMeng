package com.example.meimeng.common.viewpager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by HIAPAD on 2017/12/1.
 * @param <T> 相当于BaseAdapter,pager直接设置这个类就行，最好不对用户开发
 *
 */

public class CommonViewPagerAdapter<T> extends PagerAdapter {

    private List<T> mDatas;
    private ViewPagerHolderCreator mCreator;

    public CommonViewPagerAdapter(List<T> mDatas, ViewPagerHolderCreator mCreator) {
        this.mDatas = mDatas;
        this.mCreator = mCreator;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getView(position, null, container);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private View getView(int position, View view, ViewGroup container) {
        ViewPagerHolder holder = null;
        if (view == null) {
//            创建holder
            holder = mCreator.createViewHolder();
            view = holder.createView(container.getContext());
            view.setTag(holder);
        } else {
            holder = (ViewPagerHolder) view.getTag();
        }
        if (holder != null && mDatas != null && mDatas.size() > 0) {
            holder.onBind(container.getContext(), position, mDatas);
        }
        return view;
    }
}
