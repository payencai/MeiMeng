package com.example.meimeng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.meimeng.R;

import java.util.List;

/**
 * 作者：凌涛 on 2018/6/1 16:45
 * 邮箱：771548229@qq..com
 */
public class OptionAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mList;
    private LayoutInflater mInflater;

    public OptionAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler hodler;
        if (convertView == null) {
            hodler = new ViewHodler();
            convertView = mInflater.inflate(R.layout.option_item_text_layout, parent, false);
            hodler.mTextView = (TextView) convertView.findViewById(R.id.optionText);
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHodler) convertView.getTag();
        }
        hodler.mTextView.setText(mList.get(position));
        return convertView;
    }

    class ViewHodler {
        TextView mTextView;
    }

}
