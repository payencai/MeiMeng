package com.example.meimeng.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.bean.DrugInfo;
import com.example.meimeng.bean.MedResponse;
import com.example.meimeng.bean.MedicineBean;
import com.example.meimeng.common.rv.base.RVBaseAdapter;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.example.meimeng.util.AutoNewLineLayout;
import com.example.meimeng.util.FlowLayout;
import com.example.meimeng.util.ToaskUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.view.Gravity.CENTER;

public class SearchActivity extends BaseActivity implements View.OnClickListener {
    //private FlowLayout mSearchText;
    private int intoType = 0;//进入搜索界面的类型，0为地区搜索，1为药品搜索
    private EditText searchEdit;
    private AutoNewLineLayout mAutoNewLineLayout;
    private ListView lv_his;
    public static void startSearchActivity(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }
    private LinearLayout his_layput;
    private ImageView del;
    List<String> hisList=null;
    ArrayAdapter<String> adapter=null;
    String flag="";
    @Override
    protected void initView() {
        lv_his=findViewById(R.id.his);
        del=findViewById(R.id.del_his);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.searchBtn).setOnClickListener(this);
        searchEdit = (EditText) findViewById(R.id.searchEdit);
        his_layput=findViewById(R.id.layout_his);
        his_layput.setVisibility(View.GONE);
       // mSearchText = findViewById(R.id.flow_search);
        mAutoNewLineLayout=findViewById(R.id.auto_line);
        flag=getIntent().getStringExtra("type");
        SharedPreferences preferences = getSharedPreferences("medhistory", MODE_PRIVATE);
        hisList=new ArrayList<>();
        int count=preferences.getInt("count",0);
        if(count!=0){
            String h1=preferences.getString("h1","");
            String h2=preferences.getString("h2","");
            String h3=preferences.getString("h3","");
            String h4=preferences.getString("h4","");
            String h5=preferences.getString("h5","");
            if (h1!="")
            hisList.add(h1);
            if (h2!="")
            hisList.add(h2);
            if (h3!="")
            hisList.add(h3);
            if (h4!="")
            hisList.add(h4);
            if (h5!="")
            hisList.add(h5);
            // 使用HashSet去掉重复
            Set<String> set = new HashSet<String>(hisList);
            // 得到去重后的新集合
            List<String> newList = new ArrayList<String>(set);
             adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,newList);
            lv_his.setAdapter(adapter);
            lv_his.setVisibility(View.GONE);
            lv_his.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView textView= (TextView) view;
                    Intent intent=new Intent(SearchActivity.this,MedSearchActivity.class);
                    intent.putExtra("name",textView.getText().toString());
                    startActivityForResult(intent,3);
                }
            });
        }else{
            his_layput.setVisibility(View.GONE);
            lv_his.setVisibility(View.GONE);
        }
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("medhistory", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                adapter.clear();
                his_layput.setVisibility(View.GONE);
                editor.commit();
            }
        });
        Intent intent = getIntent();
        intoType = intent.getIntExtra("type", 0);
        if (intoType == 0) {
            searchEdit.setHint("请输入城市名");
        } else {
            searchEdit.setHint("请输入药品");
        }
        if(APP.sUserType==1){
            searchEdit.setHint("请输入药品名");
            searchEdit.setTextColor(getResources().getColor(R.color.text_9));
        }
        getMedicine(1);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(adapter!=null)
        adapter.clear();
        SharedPreferences preferences = getSharedPreferences("medhistory", MODE_PRIVATE);
        if (hisList!=null)
        hisList.clear();
        hisList=new ArrayList<>();
        String h1=preferences.getString("h1","");
        String h2=preferences.getString("h2","");
        String h3=preferences.getString("h3","");
        String h4=preferences.getString("h4","");
        String h5=preferences.getString("h5","");
        if (h1!="")
            hisList.add(h1);
        if (h2!="")
            hisList.add(h2);
        if (h3!="")
            hisList.add(h3);
        if (h4!="")
            hisList.add(h4);
        if (h5!="")
            hisList.add(h5);
        Set<String> set = new HashSet<String>(hisList);
        // 得到去重后的新集合
        List<String> newList = new ArrayList<String>(set);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,newList);
        lv_his.setAdapter(adapter);
        his_layput.setVisibility(View.VISIBLE);
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
                //ToaskUtil.showToast(this, "搜索");
                String name = searchEdit.getEditableText().toString();

                if (!TextUtils.isEmpty(name))
                    search(name, intoType);
                else {

                }
                break;
        }
    }

    private void search(String name, int type) {
        if (type == 0&&!TextUtils.equals(flag,"server")) {
            searchCity(name);
        } else {
            Intent intent=new Intent(SearchActivity.this,MedSearchActivity.class);
            intent.putExtra("name",name);
            startActivityForResult(intent,2);
           // searchMedicine(name);
        }
    }
    String token="";
    List<String> MedicineName = new ArrayList<>();
    private void getMedicine( int page) {

        Map<String, Object> params = new HashMap<>();
        params.put("page", page);

        if(APP.sUserType==0)
        {
            token=APP.getInstance().getUserInfo().getToken();
        }else{
            token=APP.getInstance().getServerUserInfo().getToken();
        }
        HttpProxy.obtain().get(PlatformContans.Medicine.sGetMedicineByManage, params, token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Gson gson = new Gson();
                MedResponse medResponse = (MedResponse) gson.fromJson(result, MedResponse.class);
                MedResponse.Data data = medResponse.getData();
                List<MedResponse.BeanList> beanLists = data.getBeanLists();
                int size = beanLists.size();
                if (size == 0) {
                } else {
                    for (MedResponse.BeanList beanList : beanLists) {
                        MedicineName.add(beanList.getName());
                    }
                }
                Map<String, Object> params = new HashMap<>();
                params.put("page", 2);
                HttpProxy.obtain().get(PlatformContans.Medicine.sGetMedicineByManage, params, token, new ICallBack() {
                            @Override
                            public void OnSuccess(String result) {
                                Gson gson = new Gson();
                                MedResponse medResponse = (MedResponse) gson.fromJson(result, MedResponse.class);
                                MedResponse.Data data = medResponse.getData();
                                List<MedResponse.BeanList> beanLists = data.getBeanLists();
                                int size = beanLists.size();
                                if (size == 0) {
                                } else {
                                    for (MedResponse.BeanList beanList : beanLists) {
                                        MedicineName.add(beanList.getName());
                                    }

                                }
                                Collections.sort(MedicineName, new Comparator<String>() {
                                    @Override
                                    public int compare(String s, String t1) {
                                        return s.compareTo(t1);
                                    }
                                });
                                for (final String name : MedicineName) {
                                    final TextView textView = new TextView(getApplicationContext());
                                    textView.setText(name);
                                    textView.setTextSize(14);
                                    textView.setGravity(CENTER);
                                    textView.setPadding(10,10,10,10);
                                    textView.setTextColor(getResources().getColor(R.color.text_9));
                                    textView.setBackgroundResource(R.drawable.shape_bg_searchtext);
                                    mAutoNewLineLayout.addView(textView);
                                    textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent=new Intent(SearchActivity.this,MedSearchActivity.class);
                                            intent.putExtra("name",textView.getText().toString());
                                            startActivityForResult(intent,1);
                                        }
                                    });
                                }
                                lv_his.setVisibility(View.VISIBLE);
                                SharedPreferences preferences = getSharedPreferences("medhistory", MODE_PRIVATE);
                                int count=preferences.getInt("count",0);
                                if(count!=0){
                                    his_layput.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onFailure(String error) {

                            }
                        });


            }

            @Override
            public void onFailure(String error) {

            }
        });


    }

    private void searchCity(String cityname) {

        startActivity(new Intent(this, CityPickerActivity.class));
    }


}
