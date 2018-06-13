package com.example.meimeng.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.meimeng.bean.NedicineBean2;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicineActivity extends AbsBaseActivity<MedicineBean> {
    TextView title;
    TextView save;
    boolean isEnter = true;
    boolean isEmpty = false;
    boolean isClick = true;
    private int page = 1;
    private boolean isRefresh = false;
    //  CheckBox itemcheckbox;
    private CheckBox mCheckBox;
    RVSimpleAdapter myAdapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EventBus.getDefault().register(this);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageReceived(MessageEvent event) {
//        switch (event.getType()) {
//            case 1:
//                if (event.getMsg()) {
//                    if (mCheckBox.isChecked()) {
//                        //isSelect(true);
//                        mCheckBox.setChecked(false);
//                        Toast.makeText(MedicineActivity.this,"click",Toast.LENGTH_LONG).show();
//
//                    }
//                }
//                break;
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // EventBus.getDefault().unregister(this); //取消注册
    }

    @Override
    public void onRecyclerViewInitialized() {
        addDividerItem(0);
        loadData();
        mBaseAdapter.setListen(new RVSimpleAdapter.onSimpleItemClickListen() {
            @Override
            public void onItemListen(RVBaseViewHolder holder, int position) {
                CheckBox checkBox = (CheckBox) holder.getView(R.id.cb_medicine);
//                if (checkBox.isChecked()) {
//                    Toast.makeText(MedicineActivity.this, "click", Toast.LENGTH_LONG);
//                    checkBox.setChecked(true);
//                }
            }
        });

    }

    private List<String> listid = new ArrayList<>();
    private List<MedicineBean> list = new ArrayList<>();

    private void getMedicineByManage(final List<String> listid) {
        final Map<String, Object> params = new HashMap<>();
        params.put("page", 2);
        HttpProxy.obtain().get(PlatformContans.Medicine.sGetMedicineByManage, params, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.e("Manage", result);
                Gson gson = new Gson();
                ++page;
                MedResponse medResponse = (MedResponse) gson.fromJson(result, MedResponse.class);
                MedResponse.Data data = medResponse.getData();
                List<MedResponse.BeanList> beanLists = data.getBeanLists();
                // List<MedicineBean> list = new ArrayList<>();
                int size = beanLists.size();
                if (size == 0) {
                    mBaseAdapter.addAll(list);
                     myAdapter=mBaseAdapter;
                } else {
                    for (MedResponse.BeanList beanList : beanLists) {
                        MedicineBean medicineBean = new MedicineBean();
                        medicineBean.setId(beanList.getId());
                        medicineBean.setName(beanList.getName());
                        medicineBean.setNum(beanList.getNum());
                        medicineBean.setIsCancel(beanList.getIsCancel());
                        //Log.e("name",medicineBean.getName());
                        if (listid.size() == 0) {
                            medicineBean.setCheck(false);
                        }
                        if (listid.contains(beanList.getId())) {
                            medicineBean.setCheck(true);
                        } else {
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

    private void getMedicineByServer(final List<String> listid) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        HttpProxy.obtain().get(PlatformContans.Medicine.sGetMedicineByServer, params, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                mBaseAdapter.hideLoadMore();
                Log.e("123456", result);
                Gson gson = new Gson();
                MedResponse medResponse = (MedResponse) gson.fromJson(result, MedResponse.class);
                MedResponse.Data data = medResponse.getData();
                List<MedResponse.BeanList> beanLists = data.getBeanLists();
                int size = beanLists.size();
                if (size == 0) {
                    isEmpty = true;
                    mBaseAdapter.clear();
                    mBaseAdapter.addAll(list);
                } else {
                    int i = 0;
                    for (MedResponse.BeanList beanList : beanLists) {
                        i++;
                        MedicineBean medicineBean = new MedicineBean();
                        medicineBean.setId(beanList.getId());
                        medicineBean.setName(beanList.getName());
                        medicineBean.setNum(beanList.getNum());
                        medicineBean.setIsCancel(beanList.getIsCancel());
                        //Log.e("name",medicineBean.getName());
                        if (listid.size() == 0) {
                            medicineBean.setCheck(false);
                        }
                        if (listid.contains(beanList.getId())) {
                            medicineBean.setCheck(true);
                        } else {
                            medicineBean.setCheck(false);
                        }
                        list.add(medicineBean);
                    }
                    if (isEnter) {
                        getMedicineByManage(listid);
                        isEnter = false;
                    } else {
                        //mBaseAdapter.clear();
                        mBaseAdapter.addAll(list);
                    }

                }
                //getMedicineByManage(2, listid);
                //mBaseAdapter.addAll(list);
            }


            @Override
            public void onFailure(String error) {
                Log.e("TAG", error);
            }
        });
    }

    private void queryMedicine() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", 2);
        HttpProxy.obtain().get(PlatformContans.Medicine.sGetMedicineByUserId, params, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                //Log.e("getbyuserid", result);
                listid = new ArrayList<>();
                Gson gson = new Gson();
                MedicineResponse medicineResponse = (MedicineResponse) gson.fromJson(result, MedicineResponse.class);
                List<MedicineResponse.Data> dataList = new ArrayList<>();
                dataList = medicineResponse.getData();
                int size = dataList.size();
                if (size == 0) {
                    getMedicineByServer(listid);
                } else {
                    for (MedicineResponse.Data data : dataList) {
                        listid.add(data.getId());
                    }
                    getMedicineByServer(listid);

                }

            }

            @Override
            public void onFailure(String error) {
                Log.e("TAG", error);
            }
        });
    }

    private void addMedicicneLib(String ids) {
        String id = "";
        Map<String, Object> params = new HashMap<>();
        if (!TextUtils.isEmpty(ids)) {
            id = ids.substring(0, ids.length() - 1);
        }
        //Log.e("shuzu",id);
        params.put("mids", id);
        params.put("type", 2);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        /// String data = gson.toJson(params);
        HttpProxy.obtain().get(PlatformContans.Medicine.sAddMedicineRelation, params, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    //Log.e("tag",result);
                    if (resultCode == 0) {
                        Toast.makeText(MedicineActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(MedicineActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });

    }

    private void loadData() {
        queryMedicine();

    }

    @Override
    public void onPullRefresh() {
        //page = 1;
        //isRefresh = true;
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(false);
                //getMedicineByServer(listid);
            }
        }, 2000);

    }

    @Override
    public void onLoadMore() {

        if (!isEmpty) {
            page++;
            Log.e("page", String.valueOf(page));
            mBaseAdapter.showLoadMore();
            getMedicineByServer(listid);
        }
        //queryMedicicneBack(++currentPage);

        //mBaseAdapter.notifyDataSetChanged();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.noSelect:
                if (mCheckBox.isChecked()) {
                    mCheckBox.setChecked(false);
                } else {
                    mCheckBox.setChecked(true);

                }

                break;
            case R.id.saveText:
                List data = mBaseAdapter.getData();
                String ids = "";
                for (Object datum : data) {
                    if (datum instanceof MedicineBean) {
                        MedicineBean medicineBean = (MedicineBean) datum;
                        if (medicineBean.isCheck()) {
                            ids = ids + medicineBean.getId() + ",";
                        }
                    }
                }
                addMedicicneLib(ids);
                break;

        }
    }

    private void isSelect(boolean b) {
        List data = mBaseAdapter.getData();
        int len=data.size();
        if (len != 0) {
            for (int i=0;i<len; i++) {
                Object object=data.get(i);
                if(object instanceof MedicineBean){
                    MedicineBean medicineBean = (MedicineBean) object;
                    if (b == true) {
                        medicineBean.setCheck(false);//将所有的itemtcheck改成未选
                    } else {
                        medicineBean.setCheck(false);//先全部设置成为未选
                        if (listid.contains(medicineBean.getId()) == true) {
                            // Log.e("size",listid.size()+"");
                            //Toast.makeText(MedicineActivity.this, "aaa", Toast.LENGTH_LONG).show();
                            medicineBean.setCheck(true);
                        }
                    }
                }

            }
            mBaseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected List<Cell> getCells(List<MedicineBean> list) {
        return null;
    }


    private void addMedicicne(List<String> ids) {
        Map<String, Object> params = new HashMap<>();
        params.put("mids", ids);
        params.put("type", 2);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String data = gson.toJson(params);
        HttpProxy.obtain().get(PlatformContans.Medicine.sAddMedicineRelation, params, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    Log.e("tag", result);
                    if (resultCode == 0) {
                        Toast.makeText(MedicineActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(MedicineActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });

    }


    private void deleteMedicine(String id, int isCancel) {
        Map<String, Object> params = new HashMap<>();
        Log.e("id", id);
        params.put("id", id);
        params.put("isCancel", isCancel);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String data = gson.toJson(params);
        HttpProxy.obtain().post(PlatformContans.Medicine.sUpdateMedicineByManage, APP.getInstance().getUserInfo().getToken(), data, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int resultCode = object.getInt("resultCode");
                    Log.e("111", result);
                    if (resultCode == 0) {
                        Toast.makeText(MedicineActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(MedicineActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
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
