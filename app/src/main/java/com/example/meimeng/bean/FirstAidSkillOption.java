package com.example.meimeng.bean;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meimeng.R;
import com.example.meimeng.activity.CPROptionActivity;
import com.example.meimeng.activity.FirstAidDetailsWebActivity;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

import java.io.Serializable;

/**
 * 作者：凌涛 on 2018/6/8 18:32
 * 邮箱：771548229@qq..com
 */
public class FirstAidSkillOption extends RVBaseCell implements Serializable {


    /**
     * id : 33
     * title : 塌方砸伤或挤压伤
     * article : http://memen.oss-cn-shenzhen.aliyuncs.com/%E4%B8%8A%E4%BC%A0/94208c0b-0524-4dcb-9da4-128f58f28430?Expires=1528767401&OSSAccessKeyId=LTAIu2UT56nIQWZI&Signature=6A0FWlRsr1m0d8X%2BKaz3t2hm21Q%3D
     * type : 1
     * articleKey : 上传/94208c0b-0524-4dcb-9da4-128f58f28430
     */

    public int layoutType = 0;
    private int id;
    private int type;
    private String title;
    private String article;
    private String articleKey;


    public FirstAidSkillOption() {
        super(null);
    }

    @Override
    public int getItemType() {
        return layoutType;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (layoutType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_head_firstaidskill, parent, false);
            return new RVBaseViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_firstaidskilloption_layout, parent, false);
            return new RVBaseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position) {
        final Context context = holder.getItemView().getContext();
        if (layoutType == 0) {
            holder.getView(R.id.CPROption).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, CPROptionActivity.class));
                }
            });
        } else {
            holder.setText(R.id.skillName, title);
            holder.getView(R.id.item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FirstAidDetailsWebActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("type", type);
                    intent.putExtra("title", title);
                    intent.putExtra("article", article);
                    intent.putExtra("articleKey", articleKey);
                    context.startActivity(intent);
                }
            });
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getArticleKey() {
        return articleKey;
    }

    public void setArticleKey(String articleKey) {
        this.articleKey = articleKey;
    }
}
