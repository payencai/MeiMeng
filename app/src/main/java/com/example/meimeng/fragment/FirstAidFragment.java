package com.example.meimeng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.meimeng.R;
import com.example.meimeng.activity.CPROptionActivity;
import com.example.meimeng.base.BaseFragment;

import butterknife.BindView;

public class FirstAidFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView firstAidOptionRv;
    RelativeLayout CPROption;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_aid, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        firstAidOptionRv = (RecyclerView) view.findViewById(R.id.firstAidOptionRv);
        view.findViewById(R.id.CPROption).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CPROption:
                startActivity(new Intent(getContext(), CPROptionActivity.class));
                break;
        }
    }
}
