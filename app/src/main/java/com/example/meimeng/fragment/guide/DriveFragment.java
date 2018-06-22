package com.example.meimeng.fragment.guide;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.model.LatLng;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseFragment;

/**
 * 作者：凌涛 on 2018/6/20 14:44
 * 邮箱：771548229@qq..com
 */
public class DriveFragment extends BaseFragment {


    private LatLng endPt;

    public void setEndPt(LatLng endPt) {
        this.endPt = endPt;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drive, container, false);
//        initView(view);
        return view;
    }
}
