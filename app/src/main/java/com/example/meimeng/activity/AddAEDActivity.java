package com.example.meimeng.activity;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.adapter.OptionAdapter;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.util.CustomPopWindow;

import java.util.ArrayList;
import java.util.List;

public class AddAEDActivity extends BaseActivity implements View.OnClickListener {

    private TextView title;

    @Override
    protected void initView() {

        title = ((TextView) findViewById(R.id.title));
        title.setText("添加AED");
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.option1).setOnClickListener(this);

    }


    @Override
    protected int getContentId() {
        return R.layout.activity_add_aed;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.option1:
                showPwBrandSelector(v);
                break;
        }
    }

    private void showPwBrandSelector( View view) {
        View brandView = LayoutInflater.from(this).inflate(R.layout.pw_list_selector, null);
        CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(brandView)
                .sizeByPercentage(this, 0.7f, 0.6f)
                .setOutsideTouchable(true)
                .enableBackgroundDark(true)
                .setAnimationStyle(R.style.CustomPopWindowStyle)
                .setBgDarkAlpha(0.5f)
                .create();
        handlerView(this, brandView, customPopWindow);
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
                if (customPopWindow != null) {
                    customPopWindow.dissmiss();
                }
            }
        });
        pwList.setAdapter(adapter);
    }

}
