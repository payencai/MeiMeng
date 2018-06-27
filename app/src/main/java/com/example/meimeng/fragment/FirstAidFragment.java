package com.example.meimeng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.activity.CPROptionActivity;
import com.example.meimeng.activity.LoginActivity;
import com.example.meimeng.base.BaseFragment;
import com.example.meimeng.bean.FirstAidSkillOption;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.manager.ActivityManager;
import com.example.meimeng.util.MLog;
import com.example.meimeng.util.ToaskUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstAidFragment extends BaseFragment implements View.OnClickListener {


    private ImageView saveImg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_aid, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
//        firstAidOptionRv = (RecyclerView) view.findViewById(R.id.firstAidOptionRv);
//        view.findViewById(R.id.CPROption).setOnClickListener(this);
//        mAdapter = new RVBaseAdapter<FirstAidSkillOption>() {
//            @Override
//            protected void onViewHolderBound(RVBaseViewHolder holder, int position) {
//
//            }
//
//            @Override
//            protected void onClick(RVBaseViewHolder holder, int position) {
//                FirstAidSkillOption skillOption = mData.get(position);
//
//            }
//        };
//        firstAidOptionRv.setLayoutManager(new LinearLayoutManager(getContext()));
//        firstAidOptionRv.setAdapter(mAdapter);
//        loadData();
        view.findViewById(R.id.back).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.title)).setText("急救知识");
        saveImg = ((ImageView) view.findViewById(R.id.saveImg));
        saveImg.setVisibility(View.GONE);
        saveImg.setImageResource(R.mipmap.ic_common_nav_normal_messag);
        saveImg.setOnClickListener(this);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.skillRvFrameLayout, new FirstAidSkillragment()).commit();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveImg:
//                startActivity(new Intent(getContext(), CPROptionActivity.class));
                ToaskUtil.showToast(getContext(), "铃铛");
                break;
        }
    }



}
