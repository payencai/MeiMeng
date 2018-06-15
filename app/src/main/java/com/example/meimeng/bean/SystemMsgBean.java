package com.example.meimeng.bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meimeng.R;
import com.example.meimeng.activity.SystemMsgDetailedActivity;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

import java.text.SimpleDateFormat;

/**
 * 作者：凌涛 on 2018/6/7 10:12
 * 邮箱：771548229@qq..com
 */
public class SystemMsgBean extends RVBaseCell {

    private String article;
    private long createTime;
    private String title;
    private int id;
    private int type;
    public SystemMsgBean() {
        super(null);
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_msg_list_layout, parent, false);
        return new RVBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RVBaseViewHolder holder, int position) {
        holder.getView(R.id.item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemMsgDetailedActivity.startSystemMsgDetailedActivity(holder.getItemView().getContext(), title, article);
            }
        });
        holder.setText(R.id.msgTitle,title);
        SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String time=sdf.format(createTime);
        holder.setText(R.id.msgTime,time);
    }
}
