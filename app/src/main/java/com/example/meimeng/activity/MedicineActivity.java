package com.example.meimeng.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.ClientRecordBean;
import com.example.meimeng.bean.MedResponse;
import com.example.meimeng.bean.MedicineBean;
import com.example.meimeng.bean.MedicineResponse;
import com.example.meimeng.bean.MessageEvent;
import com.example.meimeng.bean.RecordResponse;
import com.example.meimeng.common.rv.absRv.AbsBaseActivity;
import com.example.meimeng.common.rv.base.Cell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.common.rv.base.RVSimpleAdapter;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicineActivity extends AbsBaseActivity<MedicineBean> {
    TextView title;
    TextView save;
  //  CheckBox itemcheckbox;
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageReceived(MessageEvent event) {
        switch(event.getType()){
            case 1:
                if(event.getMsg()){
                    if(mCheckBox.isChecked()){
                        mCheckBox.setChecked(false);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this); //取消注册
    }

    @Override
    public void onRecyclerViewInitialized() {
        addDividerItem(0);
        loadDataAdv();
        mBaseAdapter.setListen(new RVSimpleAdapter.onSimpleItemClickListen() {
            @Override
            public void onItemListen(RVBaseViewHolder holder, int position) {
                 CheckBox checkBox= (CheckBox) holder.getView(R.id.cb_medicine);
                 if (checkBox.isChecked()){
                 }
            }
        });


    }
   private void loadDataAdv(){
       Map<String, Object> params = new HashMap<>();
       params.put("page", 1);
       HttpProxy.obtain().get(PlatformContans.Medicine.sGetMedicineByManage, params, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
           @Override
           public void OnSuccess(String result) {
               //Log.e("TAG", result);
               List<MedicineBean> list = new ArrayList<>();
               Gson gson = new Gson();
               MedResponse medResponse = (MedResponse) gson.fromJson(result, MedResponse.class);
               MedResponse.Data data = medResponse.getData();
               List<MedResponse.BeanList> beanLists=data.getBeanLists();
               int size = beanLists.size();
               if (size == 0) {
                   mBaseAdapter.addAll(list);
               } else {
                   for (MedResponse.BeanList beanList : beanLists) {
                       MedicineBean medicineBean = new MedicineBean();
                       medicineBean.setId(beanList.getId());
                       medicineBean.setName(beanList.getName());
                       medicineBean.setNum(beanList.getNum());
                       medicineBean.setIsCancel(beanList.getIsCancel());
                       Log.e("name",medicineBean.getName());
                       if (beanList.getIsCancel()==1)
                           medicineBean.setCheck(true);
                       else{
                           medicineBean.setCheck(false);
                       }
                       list.add(medicineBean);
                   }
                   mBaseAdapter.addAll(list);
               }

           }

           @Override
           public void onFailure(String error) {
               Log.e("TAG", error);
           }
       });
   }

    private void loadData() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", 2);
        HttpProxy.obtain().get(PlatformContans.Medicine.sGetMedicineByUserId, params, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                //Log.e("TAG", result);
                List<MedicineBean> list = new ArrayList<>();
                Gson gson = new Gson();
                MedicineResponse medicineResponse = (MedicineResponse) gson.fromJson(result, MedicineResponse.class);
                List<MedicineResponse.Data> dataList = new ArrayList<>();
                dataList = medicineResponse.getData();
                int size = dataList.size();
                if (size == 0) {
                    mBaseAdapter.addAll(list);
                } else {

                    for (MedicineResponse.Data data : dataList) {
                        MedicineBean medicineBean = new MedicineBean();
                        medicineBean.setId(data.getId());
                        medicineBean.setName(data.getName());
                        medicineBean.setNum(data.getNum());
                        medicineBean.setIsCancel(data.getIsCancel());
                        if (data.getIsCancel()==1)
                           medicineBean.setCheck(true);
                        else{
                            medicineBean.setCheck(false);
                        }
                        list.add(medicineBean);
                    }
                    mBaseAdapter.addAll(list);
                }

            }

            @Override
            public void onFailure(String error) {
                Log.e("TAG", error);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public View addToolbar() {

        View view = LayoutInflater.from(this).inflate(R.layout.show_yaopin_content, null);
        view.findViewById(R.id.noSelect).setOnClickListener(this);
        view.findViewById(R.id.back).setOnClickListener(this);
        mCheckBox = ((CheckBox) view.findViewById(R.id.noSelectCheck));
       // itemcheckbox=view.findViewById(R.id.cb_medicine);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                  isSelect(!b);
                else
                  isSelect(b);
            }
        });
        //itemcheckbox.setOnClickListener(this);
        title = view.findViewById(R.id.title);
        save = view.findViewById(R.id.saveText);
        save.setVisibility(View.VISIBLE);
        title.setText("个人药品库");
        save.setText("确定");
        save.setOnClickListener(this);
        return view;
    }

    @Override
    protected List<Cell> getCells(List<MedicineBean> list) {
        return null;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.noSelect:
                if (mCheckBox.isChecked()) {
                    mCheckBox.setChecked(false);
                    isSelect(true);

                } else {
                    mCheckBox.setChecked(true);
                    isSelect(false);
                }

                break;
            case R.id.saveText:
                List<String> dellist=new ArrayList<>();
                List<String> addlist=new ArrayList<>();
                List data = mBaseAdapter.getData();
                for (Object datum : data) {
                    if (datum instanceof MedicineBean) {
                        MedicineBean medicineBean = (MedicineBean) datum;
                        if(medicineBean.getIsCancel()==1){
                            if (!medicineBean.isCheck()) {
                                dellist.add(medicineBean.getId());
                            }
                        }
                     else{
                            if(medicineBean.isCheck()){
                                addlist.add(medicineBean.getId());
                            }
                        }

                    }
                }
                for (String id:dellist){
                    deleteMedicine(id,2);
                }
                String []ids=new String[addlist.size()];
                for (int i=0;i<addlist.size();i++){
                   ids[i]=addlist.get(i);
                }
                //addMedicicne(ids);
                break;

        }
    }

    private void addMedicicne(String[] ids) {
        Map<String,Object> params=new HashMap<>();
        params.put("mids",ids);
        params.put("type",2);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String data = gson.toJson(params);
        HttpProxy.obtain().get(PlatformContans.Medicine.sAddMedicineRelation, params, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    Log.e("tag",result);
                    if (resultCode == 0) {
                        Toast.makeText(MedicineActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(MedicineActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });

    }

    private void isSelect(boolean b) {
        List data = mBaseAdapter.getData();
        for (Object datum : data) {
            MedicineBean medicineBean = (MedicineBean) datum;
            medicineBean.setCheck(b);
        }
        mBaseAdapter.notifyDataSetChanged();
    }
    private void deleteMedicine(String id,int isCancel){
        Map<String,Object> params=new HashMap<>();
        Log.e("id",id);
        params.put("id",id);
        params.put("isCancel",isCancel);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String data = gson.toJson(params);
        HttpProxy.obtain().post(PlatformContans.Medicine.sUpdateMedicineByManage, APP.getInstance().getUserInfo().getToken(), data, new ICallBack() {
            @Override
            public void OnSuccess(String result)
            {
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    Log.e("tag",result);
                    if (resultCode == 0) {
                        Toast.makeText(MedicineActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(MedicineActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("tag", "onResponse: " + error);
            }
        });
    }

}
