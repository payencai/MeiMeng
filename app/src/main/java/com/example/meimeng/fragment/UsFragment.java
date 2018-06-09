package com.example.meimeng.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseFragment;
import com.example.meimeng.fragment.device.FirstAidRankFragment;
import com.example.meimeng.fragment.device.TrainFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是设备信息
 */
public class UsFragment extends BaseFragment implements View.OnClickListener {

    private TextView firstAidText;
    private TextView deviceText;
    private View view1;
    private View view2;
    private List<Fragment> fragments;
    private FragmentManager fm;
    private TrainFragment mTrainFragment;
    private FirstAidRankFragment mFirstAidRankFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_us, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.back).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.title)).setText("急救信息");
        view.findViewById(R.id.deviceLayout).setOnClickListener(this);
        view.findViewById(R.id.firstAidLayout).setOnClickListener(this);
        view1 = view.findViewById(R.id.view1);
        view2 = view.findViewById(R.id.view2);
        deviceText = ((TextView) view.findViewById(R.id.deviceText));
        firstAidText = ((TextView) view.findViewById(R.id.firstAidText));
        initFragment();

    }

    private void initFragment() {
        fm = getActivity().getSupportFragmentManager();

        mTrainFragment = new TrainFragment();
        mFirstAidRankFragment = new FirstAidRankFragment();

        fragments = new ArrayList<>();
        fragments.add(mTrainFragment);
        fragments.add(mFirstAidRankFragment);


        for (Fragment fragment : fragments) {
            fm.beginTransaction().add(R.id.devicContainer, fragment).commit();
        }
        //显示主页
        setSelect(0);
        hideAllFragment();
        showFragment(0);
    }

    private void hideAllFragment() {
        for (Fragment fragment : fragments) {
            fm.beginTransaction().hide(fragment).commit();
        }
    }

    private void showFragment(int position) {
        fm.beginTransaction().show(fragments.get(position)).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deviceLayout:
                setSelect(0);
                hideAllFragment();
                showFragment(0);
                break;
            case R.id.firstAidLayout:
                setSelect(1);
                hideAllFragment();
                showFragment(1);
                break;
        }
    }

    //重置所有文本的选中状态
    public void resetState() {
        deviceText.setSelected(false);
        firstAidText.setSelected(false);
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
    }

    public void setSelect(int position) {
        if (position == 0) {
            deviceText.setSelected(true);
            firstAidText.setSelected(false);
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.GONE);
        } else {
            deviceText.setSelected(false);
            firstAidText.setSelected(true);
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.VISIBLE);
        }
    }


}
