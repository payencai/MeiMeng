package com.example.meimeng.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseFragment;
import com.example.meimeng.bean.ChatEntity;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.util.ToaskUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：凌涛 on 2018/6/27 09:45
 * 邮箱：771548229@qq..com
 */
public class EaseUIFragment extends BaseFragment {




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_easeui_layout, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        

    }
}
