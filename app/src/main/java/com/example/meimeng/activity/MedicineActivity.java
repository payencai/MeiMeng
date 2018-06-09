package com.example.meimeng.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.ClientRecordBean;
import com.example.meimeng.bean.MedicineBean;
import com.example.meimeng.bean.MedicineResponse;
import com.example.meimeng.bean.RecordResponse;
import com.example.meimeng.common.rv.absRv.AbsBaseActivity;
import com.example.meimeng.common.rv.base.Cell;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicineActivity extends AbsBaseActivity<MedicineBean> {
    TextView title;
    TextView save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onRecyclerViewInitialized() {
        addDividerItem(0);
        loadData();
    }

    private void loadData() {
        Map<String,Object> params=new HashMap<>();
        params.put("type",2);
        HttpProxy.obtain().get(PlatformContans.Medicine.sGetMedicineByUserId, params , APP.getInstance().getUserInfo().getToken(),new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.e("TAG",result);
                List<MedicineBean> list = new ArrayList<>();
                Gson gson=new Gson();
                MedicineResponse medicineResponse=(MedicineResponse) gson.fromJson(result, MedicineResponse.class);
                List<MedicineResponse.Data> dataList=new ArrayList<>();
                dataList=medicineResponse.getData();
                int size=dataList.size();
                if(size==0){
                    mBaseAdapter.addAll(list);
                }else{
                    for(MedicineResponse.Data data:dataList){
                        MedicineBean medicineBean=new MedicineBean();
                        medicineBean.setId(data.getId());
                        medicineBean.setName(data.getName());
                        medicineBean.setNum(data.getNum());
                        list.add(medicineBean);
                    }
                    mBaseAdapter.addAll(list);
                }

            }

            @Override
            public void onFailure(String error) {
                Log.e("TAG",error);
            }
        });
    }

    @Override
    public void onPullRefresh() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(false);
            }
        }, 2000);

    }

    @Override
    public void onLoadMore() {

    }


    @Override
    public View addToolbar() {
        View view = LayoutInflater.from(this).inflate(R.layout.show_yaopin_content, null);
        title=view.findViewById(R.id.title);
        save=view.findViewById(R.id.saveText);
        save.setVisibility(View.VISIBLE);
        title.setText("个人药品库");
        save.setText("确定");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    @Override
    protected List<Cell> getCells(List<MedicineBean> list) {
        return null;
    }


    @Override
    public void onClick(View view) {

    }
}
