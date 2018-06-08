package com.example.meimeng.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseFragment;


/**
 * 这是设备信息
 */
public class UsFragment extends BaseFragment implements View.OnClickListener {

    private TextView firstAidText;
    private TextView deviceText;
    private View view1;
    private View view2;

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
        setSelect(0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deviceLayout:
                setSelect(0);
                break;
            case R.id.firstAidLayout:
                setSelect(1);
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
