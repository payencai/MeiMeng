package com.example.meimeng.bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meimeng.R;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

public class ChatEntity extends RVBaseCell {

    private long createTime;
    private String content;
    private int type;//类型，客户还是服务

    public ChatEntity() {
        super(null);
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChatEntity{" +
                "createTime=" + createTime +
                ", content='" + content + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public int getItemType() {
        return type;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (type == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_info_left_rv, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_info_right_rv, parent, false);
        }
        return new RVBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position) {

        holder.setText(R.id.chatContent, content);

    }
}
