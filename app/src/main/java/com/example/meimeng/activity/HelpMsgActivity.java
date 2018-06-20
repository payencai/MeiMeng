package com.example.meimeng.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meimeng.APP;
import com.example.meimeng.R;
import com.example.meimeng.bean.HelpMsg;
import com.example.meimeng.bean.SystemMsgBean;
import com.example.meimeng.common.rv.absRv.AbsBaseActivity;
import com.example.meimeng.common.rv.base.Cell;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpMsgActivity extends AbsBaseActivity<HelpMsg> {
    private int page=1;
    private boolean isRefresh=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onRecyclerViewInitialized() {
        addDividerItem(0);
        loadData();
    }

    @Override
    public void onPullRefresh() {
        page=1;
        isRefresh=true;
        loadData();
    }

    @Override
    public void onLoadMore() {
        page++;
        loadData();
    }
    @Override
    public View addToolbar() {
        TextView title;
        ImageView back;
        View view = LayoutInflater.from(this).inflate(R.layout.toobar_head_layout, null);
        back=view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title=view.findViewById(R.id.title);
        title.setText("急救消息");
        return view;
    }
    @Override
    protected List<Cell> getCells(List<HelpMsg> list) {
        return null;
    }

    @Override
    public void onClick(View view) {

    }
    private void loadData(){
        Map<String,Object> params=new HashMap<>();
        String token=APP.getInstance().getServerUserInfo().getToken();
        params.put("page",page);
        HttpProxy.obtain().get(PlatformContans.PutMessage.sGetPutMessageByManage, params,token, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                mBaseAdapter.hideLoadMore();
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    int code=jsonObject.getInt("resultCode");
                    Log.e("msg--",result);
                    if(code==0){
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray beanList = data.getJSONArray("beanList");
                        List<HelpMsg> list = new ArrayList<>();
                        int length = beanList.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject item = beanList.getJSONObject(i);
                            //TrainBean bean = gson.fromJson(item.toString(), TrainBean.class);
                            HelpMsg bean=new HelpMsg();
                            bean.setAddress(item.getString("address"));
                            bean.setCreateTime(item.getString("createTime"));
                            bean.setTelephone(item.getString("telephone"));
                            list.add(bean);
                        }
                        if (isRefresh) {
                            isRefresh = false;
                            mBaseAdapter.clear();
                            mBaseAdapter.addAll(list);

                        } else {
                            mBaseAdapter.addAll(list);
                        }
                        mSwipeRefreshLayout.setRefreshing(isRefresh);
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
}
