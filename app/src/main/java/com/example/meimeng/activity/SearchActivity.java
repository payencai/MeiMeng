package com.example.meimeng.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.DrugInfo;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.util.ToaskUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private int intoType = 0;//进入搜索界面的类型，0为地区搜索，1为药品搜索
    private EditText searchEdit;
    private RecyclerView drugRv;


    public static void startSearchActivity(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.searchBtn).setOnClickListener(this);
        searchEdit = (EditText) findViewById(R.id.searchEdit);

        drugRv = (RecyclerView) findViewById(R.id.drugRv);

        Intent intent = getIntent();
        intoType = intent.getIntExtra("type", 0);
        if (intoType == 0) {
            searchEdit.setHint("请输入城市名");
        } else {
            searchEdit.setHint("请输入药品");
        }

        RVBaseAdapter<DrugInfo> adapter = new RVBaseAdapter<DrugInfo>() {
            @Override
            protected void onViewHolderBound(RVBaseViewHolder holder, int position) {

            }
        };
        List<DrugInfo> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new DrugInfo());
        }
        adapter.setData(list);
        drugRv.setLayoutManager(new LinearLayoutManager(this));
        drugRv.setAdapter(adapter);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_search;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.searchBtn:
                ToaskUtil.showToast(this, "搜索");
                break;
        }
    }
}
