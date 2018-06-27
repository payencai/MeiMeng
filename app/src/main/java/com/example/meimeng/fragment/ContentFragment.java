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
public class ContentFragment extends BaseFragment implements View.OnClickListener {

    private ImageView msgTypeBtn;
    private ImageView photoBtn;
    private EditText rescueEdit;
    private TextView voiceBtn;

    private RecyclerView chatRv;

    private RVBaseAdapter<ChatEntity> mAdapter;
    private List<ChatEntity> mList;

    private int msgState = 0;//信息状态，0为文字编辑，1为语言状态

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_layout, container, false);
        initView(view);
        initRvData();
        return view;
    }

    private void initRvData() {
        initData();
        mAdapter = new RVBaseAdapter<ChatEntity>() {
            @Override
            protected void onViewHolderBound(RVBaseViewHolder holder, int position) {

            }
        };
        mAdapter.setData(mList);
        chatRv.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRv.setAdapter(mAdapter);
    }

    private void initView(View view) {
        msgTypeBtn = (ImageView) view.findViewById(R.id.msgTypeBtn);
        photoBtn = (ImageView) view.findViewById(R.id.photoBtn);
        rescueEdit = (EditText) view.findViewById(R.id.rescueEdit);
        voiceBtn = (TextView) view.findViewById(R.id.voiceBtn);

        chatRv = (RecyclerView) view.findViewById(R.id.chatRv);

        msgTypeBtn.setOnClickListener(this);
        voiceBtn.setOnClickListener(this);
        photoBtn.setOnClickListener(this);

        setState();
    }

    private void initData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {

            ChatEntity chatEntity = new ChatEntity();
            if (i % 2 == 0) {
                chatEntity.setType(0);
            } else {
                chatEntity.setType(1);
            }

            chatEntity.setContent("我是内容我是内容我是内容我是内容我是内容我是内容我是内容我是内容我是内容我是内容" + i);
            mList.add(chatEntity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msgTypeBtn:
                if (msgState == 0) {
                    msgState = 1;
                } else {
                    msgState = 0;
                }
                setState();
                break;
            case R.id.photoBtn:
                ToaskUtil.showToast(getContext(), "弹出相册");
                break;
            case R.id.voiceBtn:
                ToaskUtil.showToast(getContext(), "按住说话");
                break;
        }
    }

    private void setState() {
        if (msgState == 0) {
            msgTypeBtn.setImageResource(R.mipmap.ic_speak_btn);
            rescueEdit.setVisibility(View.VISIBLE);
            voiceBtn.setVisibility(View.GONE);
        } else if (msgState == 1) {
            rescueEdit.setVisibility(View.GONE);
            voiceBtn.setVisibility(View.VISIBLE);
            msgTypeBtn.setImageResource(R.mipmap.ic_message_btn);
        }
    }
}
