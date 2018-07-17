package com.example.meimeng.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meimeng.R;
import com.example.meimeng.base.BaseActivity;
import com.example.meimeng.util.ToaskUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeContactsActivity extends BaseActivity implements View.OnClickListener {


    ImageView back;
    TextView title;
    TextView saveText;
    EditText contactsName;
    EditText contactsTel;

    private int mIntoType = 1;//进入类型，1为联系人1，2为联系人2，3为联系人3，默认为1

    public static void startMakeContactsActivity(Activity context, int intoType) {
        Intent intent = new Intent(context, MakeContactsActivity.class);
        intent.putExtra("intoType", intoType);
        context.startActivityForResult(intent, intoType);
    }


    @Override
    protected void initView() {
        back = findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        saveText = (TextView) findViewById(R.id.saveText);

        back.setOnClickListener(this);
        saveText.setOnClickListener(this);
        contactsName = (EditText) findViewById(R.id.contactsName);
        contactsTel = (EditText) findViewById(R.id.contactsTel);

        title.setText("添加联系人");
        saveText.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        mIntoType = intent.getIntExtra("intoType", 1);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_make_contacts;
    }

    private boolean checkForm(String name, String tel) {
        if(TextUtils.isEmpty(name)&&TextUtils.isEmpty(tel)){
            return true;
        }
        if (TextUtils.isEmpty(name)&&!TextUtils.isEmpty(tel)) {
            ToaskUtil.showToast(this, "名称不能为空");
            return false;
        }

        if (tel.length() != 11) {
            ToaskUtil.showToast(this, "号码格式错误");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.saveText:
                String name = this.contactsName.getEditableText().toString().replace(" ", "");
                String tel = this.contactsTel.getEditableText().toString().replace(" ", "");
                if (checkForm(name, tel)) {
                    Intent intent = new Intent();
                    intent.putExtra("name", name);
                    intent.putExtra("tel", tel);
                    setResult(mIntoType, intent);
                    finish();
                }
                break;
        }
    }
}
