package com.example.meimeng.common.rv.cell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.meimeng.R;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.common.rv.base.RVSimpleAdapter;


/**
 * Created by HIAPAD on 2017/12/2.
 */

public class EmptyCell extends RVAbsStateCell {
    public EmptyCell(Object o) {
        super(o);
    }

    @Override
    public int getItemType() {
        return RVSimpleAdapter.EMPTY_TYPE;
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position) {

    }

    @Override
    protected View getDefaultView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.rv_empty_layout, null);
    }

    @Override
    public void releaseResource() {
        super.releaseResource();
    }
}
