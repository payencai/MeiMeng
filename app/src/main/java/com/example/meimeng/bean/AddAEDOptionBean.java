package com.example.meimeng.bean;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.meimeng.R;
import com.example.meimeng.adapter.OptionAdapter;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.util.CustomPopWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：凌涛 on 2018/6/1 14:49
 * 邮箱：771548229@qq..com
 */
public class AddAEDOptionBean extends RVBaseCell {

    public String optionName;
    public boolean isMustHint;
    public String optionHint;
    public boolean isShowArrows;
    public int type = 0;

    public AddAEDOptionBean(String optionName, boolean isMustHint, String optionHint, int type) {
        super(null);
        this.optionName = optionName;
        this.isMustHint = isMustHint;
        this.optionHint = optionHint;
        this.type = type;
    }

    public AddAEDOptionBean() {
        super(null);
    }


    @Override
    public int getItemType() {
        return type;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (type == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.option_item_layout1, parent, false);
            return new RVBaseViewHolder(view);

        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.option_item_addaed_layout, parent, false);
            return new RVBaseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RVBaseViewHolder holder, final int position) {
        if (type == 0) {
            if (isMustHint) {
                holder.getTextView(R.id.isMustHint).setVisibility(View.VISIBLE);
            } else {
                holder.getTextView(R.id.isMustHint).setVisibility(View.GONE);
            }
            holder.setText(R.id.optionName, optionName);
            holder.setText(R.id.optionHint, optionHint);
            holder.getView(R.id.item0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            showPwBrandSelector(holder.getItemView().getContext(), v);
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                    }
                }
            });
        } else {


        }
    }

    private void showPwBrandSelector(Context context, View view) {
        View brandView = LayoutInflater.from(context).inflate(R.layout.pw_list_selector, null);
        CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(context)
                .setView(brandView)
                .sizeByPercentage(context, 0.7f, 0.6f)
                .setOutsideTouchable(true)
                .enableBackgroundDark(true)
                .setAnimationStyle(R.style.CustomPopWindowStyle)
                .setBgDarkAlpha(0.5f)
                .create();
        handlerView(context, brandView, customPopWindow);
        customPopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void handlerView(final Context context, View view, final CustomPopWindow customPopWindow) {
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
            }
        });
        ListView pwList = (ListView) view.findViewById(R.id.pwList);
        List<String> list = new ArrayList<>();
        list.add("飞利浦（PHILIPS）");
        list.add("卓尔（ZOLL）");
        list.add("迈瑞（mindray）");
        list.add("普美康（PRIMEDIC）");
        list.add("日本光电（NIHON KOHDEN）");
        list.add("瑞士席勒（SCHILLER）");
        list.add("捷斯特（CHEST）");
        list.add("其它");
        OptionAdapter adapter = new OptionAdapter(context, list);
        pwList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemString = (String) parent.getItemAtPosition(position);
                optionHint = itemString;
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
            }
        });
        pwList.setAdapter(adapter);
    }
}
