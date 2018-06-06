package com.example.meimeng.activity;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.ChatEntity;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.util.ToaskUtil;

import java.util.ArrayList;
import java.util.List;

public class WaitSalvationActivity extends BaseActivity implements View.OnClickListener {

    private ImageView msgTypeBtn;
    private ImageView photoBtn;
    private EditText rescueEdit;
    private TextView voiceBtn;

    private int msgState = 0;//信息状态，0为文字编辑，1为语言状态
    private RecyclerView chatRv;
    private RVBaseAdapter<ChatEntity> mAdapter;
    private List<ChatEntity> mList;
    private TextView cancel;
    private PopupWindow mFinishPw;


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wait_salvation);
////        if (Build.VERSION.SDK_INT >= 21) {
////            View decorView = getWindow().getDecorView();
////            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
////            decorView.setSystemUiVisibility(option);
////            getWindow().setStatusBarColor(Color.TRANSPARENT);
////            ActionBar actionBar = getSupportActionBar();
////            actionBar.hide();
////        }
////        String res = "{\"content\":\"你好啊\",\"type\":0,\"createTime\":13213231}";
////        List<ChatEntity> list = new ChatEntityParse<ChatEntity>().beanParse(res);
//
//
//
//    }

    @Override
    protected int getContentId() {
        return R.layout.activity_wait_salvation;
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

    private void showData() {

    }

    @Override
    protected void initView() {

        msgTypeBtn = (ImageView) findViewById(R.id.msgTypeBtn);
        photoBtn = (ImageView) findViewById(R.id.photoBtn);
        rescueEdit = (EditText) findViewById(R.id.rescueEdit);
        voiceBtn = (TextView) findViewById(R.id.voiceBtn);
        chatRv = (RecyclerView) findViewById(R.id.chatRv);
        cancel = (TextView) findViewById(R.id.cancel);

        msgTypeBtn.setOnClickListener(this);
        voiceBtn.setOnClickListener(this);
        photoBtn.setOnClickListener(this);
        cancel.setOnClickListener(this);
        setState();

        initData();
        mAdapter = new RVBaseAdapter<ChatEntity>() {
            @Override
            protected void onViewHolderBound(RVBaseViewHolder holder, int position) {

            }
        };
        mAdapter.setData(mList);
        chatRv.setLayoutManager(new LinearLayoutManager(this));
        chatRv.setAdapter(mAdapter);
        showData();


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
                ToaskUtil.showToast(this, "弹出相册");
                break;
            case R.id.voiceBtn:
                ToaskUtil.showToast(this, "按住说话");
                break;
            case R.id.cancel:
                setBackgroundDrakValue(0.75f);
                showPopupW(v);
                break;
            case R.id.popCancel:
                mFinishPw.dismiss();
                break;
            case R.id.popConfirm:
                mFinishPw.dismiss();
                finish();
                break;
        }
    }

    private void showPopupW(View view) {

        View shareview = LayoutInflater.from(this).inflate(R.layout.popup_salvation_finish, null);
        handlerView(shareview);
        mFinishPw = new PopupWindow(shareview,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()));
        mFinishPw.setFocusable(true);
        mFinishPw.setBackgroundDrawable(new BitmapDrawable());
        mFinishPw.setOutsideTouchable(true);
        mFinishPw.setAnimationStyle(R.style.CustomPopWindowStyle);
        mFinishPw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundDrakValue(1.0f);
            }
        });
        //第一个参数可以取当前页面的任意一个View
        //第二个参数表示pw从哪一个方向显示出来
        //3、4表示pw的偏移量
        mFinishPw.showAtLocation(view, Gravity.CENTER, 0, 0);

    }

    private void handlerView(View view) {

        view.findViewById(R.id.popCancel).setOnClickListener(this);
        view.findViewById(R.id.popConfirm).setOnClickListener(this);

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
