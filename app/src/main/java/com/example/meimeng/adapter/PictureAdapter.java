package com.example.meimeng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.meimeng.R;

import java.util.List;

/**
 * 作者：凌涛 on 2018/6/7 15:56
 * 邮箱：771548229@qq..com
 */
public class PictureAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mList;
    private LayoutInflater mInflater;

    public PictureAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    public void updata(List<String> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }
    OnItemDelListener mOnItemDelListener;
    public interface OnItemDelListener{
        void onClick(int position,View view);
    }
    public void setOnItemDelListener(OnItemDelListener onItemDelListener){
        this.mOnItemDelListener=onItemDelListener;
    }
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_picture_item_layout, parent, false);
            holder = new ViewHolder();
            holder.mImageView = convertView.findViewById(R.id.pictureLoad);
            holder.mImageDel=convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mImageDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemDelListener.onClick(position,holder.mImageView);
            }
        });
        String url = mList.get(position);
        Glide.with(mContext).load(url).into(holder.mImageView);
        return convertView;
    }

    class ViewHolder {
        ImageView mImageView;
        ImageView mImageDel;
    }

}
