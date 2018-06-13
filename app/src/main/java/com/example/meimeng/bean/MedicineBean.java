package com.example.meimeng.bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.example.meimeng.R;
import com.example.meimeng.common.rv.base.RVBaseCell;
import com.example.meimeng.common.rv.base.RVBaseViewHolder;

import org.greenrobot.eventbus.EventBus;

public class MedicineBean extends RVBaseCell {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String name;
    private int num;
    private int isCancel;
    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(int isCancel) {
        this.isCancel = isCancel;
    }

    public String getName() {
        return name;
    }

    public MedicineBean(String name, int num) {
        super(null);
        this.name = name;
        this.num = num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public MedicineBean() {
        super(null);
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);

        return new RVBaseViewHolder(view);
    }

    public interface OnItemCheckboxClickListener {
        void checkboxClick(boolean flag);
    }

    public OnItemCheckboxClickListener mOnItemCheckboxClickListener;

    public void setOnItemCheckboxClickListener(OnItemCheckboxClickListener mOnItemCheckboxClickListener) {
        this.mOnItemCheckboxClickListener = mOnItemCheckboxClickListener;
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final CheckBox checkBox = (CheckBox) holder.getView(R.id.cb_medicine);
        if (isCheck) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheck=b;
                checkBox.setChecked(b);
                //EventBus.getDefault().post(new MessageEvent(b,1));
            }
        });

        holder.setText(R.id.tv_medicine_name, name);
        LinearLayout layout = (LinearLayout) holder.getView(R.id.item);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheck) {
                    isCheck = false;
                    checkBox.setChecked(false);
                    //EventBus.getDefault().post(new MessageEvent(false,1));
                } else {
                    isCheck = true;
                    checkBox.setChecked(true);
                   // EventBus.getDefault().post(new MessageEvent(true,1));
                }
            }
        });

    }
}
