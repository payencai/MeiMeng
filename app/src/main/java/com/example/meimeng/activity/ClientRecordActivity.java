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
import com.example.meimeng.bean.ClientRecordBean;
import com.example.meimeng.bean.RecordResponse;
import com.example.meimeng.bean.SystemMsgBean;
import com.example.meimeng.common.rv.absRv.AbsBaseActivity;
import com.example.meimeng.common.rv.base.Cell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;
import com.example.meimeng.common.rv.base.RVSimpleAdapter;
import com.example.meimeng.constant.PlatformContans;
import com.example.meimeng.http.HttpCallback;
import com.example.meimeng.http.HttpProxy;
import com.example.meimeng.http.ICallBack;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class ClientRecordActivity extends AbsBaseActivity<ClientRecordBean> {

    TextView title;
    private int page=1;
    private boolean isRefresh = false;
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
        page = 1;
        isRefresh = true;
//        mRecyclerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setRefreshing(false);
//            }
//        }, 2000);
        loadData();
//        mRecyclerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setRefreshing(false);
//            }
//        }, 2000);

    }

    public void loadData() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        HttpProxy.obtain().get(PlatformContans.ForHelp.sGetCompleteHelpByUseUser, params, APP.getInstance().getUserInfo().getToken(), new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                mBaseAdapter.hideLoadMore();
                //Log.e("TAG", result);
                List<ClientRecordBean> list = new ArrayList<>();
                Gson gson = new Gson();
                RecordResponse recordResponse = (RecordResponse) gson.fromJson(result, RecordResponse.class);
                List<RecordResponse.beanList> beanLists = new ArrayList<>();
                beanLists = recordResponse.getData().getBeanLists();
                int size = beanLists.size();
                if (size == 0) {
                    mBaseAdapter.addAll(list);
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    for (RecordResponse.beanList beanList : beanLists) {
                        ClientRecordBean bean = new ClientRecordBean();
                        List<RecordResponse.serveruser> serverusers = beanList.getServerusers();
                        List<String> imgList = new ArrayList<>();
                        for (RecordResponse.serveruser serveruser : serverusers) {
                            imgList.add(serveruser.getServerImage());
                        }
                        bean.setCompleteTime(beanList.getCompleteTime());
                        bean.setAddress(beanList.getUserAddress());
                        bean.setImgList(imgList);
                        list.add(bean);
                    }
                    //mBaseAdapter.addAll(list);
                    if (isRefresh) {
                        isRefresh = false;
                        mBaseAdapter.clear();
                       // loadHead();
                        mBaseAdapter.addAll(list);

                    } else {
                        mBaseAdapter.addAll(list);
                    }
                    mSwipeRefreshLayout.setRefreshing(isRefresh);
                }

            }

            @Override
            public void onFailure(String error) {
                //Log.e("TAG", error);
            }
        });


    }

    @Override
    public void onLoadMore() {
           page++;
           loadData();
    }

    @Override
    public View addToolbar() {
        View view = LayoutInflater.from(this).inflate(R.layout.toobar_head_layout, null);
        title = view.findViewById(R.id.title);
        title.setText("求救记录");
        ImageView back;
        back = view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        return view;
    }

    @Override
    protected List<Cell> getCells(List<ClientRecordBean> list) {
        return null;
    }

    @Override
    public void onClick(View view) {

    }

}
