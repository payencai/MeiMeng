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


    private FragmentManager fm;
    private TrainFragment mTrainFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_us, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.back).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.title)).setText("培训信息");
        initFragment();

    }

    private void initFragment() {
        fm = getActivity().getSupportFragmentManager();
        mTrainFragment = new TrainFragment();
        fm.beginTransaction().add(R.id.devicContainer, mTrainFragment).commit();

    }


    @Override
    public void onClick(View v) {

    }
}
