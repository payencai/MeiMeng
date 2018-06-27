package com.example.meimeng.chat;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meimeng.IBackService;
import com.example.meimeng.R;
import com.example.meimeng.activity.MainActivity;

public class LongConnectActivity extends AppCompatActivity {
    private static final String TAG = "LongConnectActivity";

    private Intent mServiceIntent;
    private IBackService iBackService;
    private TextView tv;
    private EditText et;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_connect);
        initViews();
        initData();
    }

    private void initViews() {
        tv = (TextView) findViewById(R.id.tv);
        et = (EditText) findViewById(R.id.editText1);
        btn = (Button) findViewById(R.id.button1);
    }

    private void initData() {
        mServiceIntent = new Intent(this, BackService.class);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = et.getText().toString().trim();
                Log.i(TAG, string);
                try {
                    Log.i(TAG, "是否为空：" + iBackService);
                    if (iBackService == null) {
                        Toast.makeText(getApplicationContext(), "没有连接，可能是服务器已断开", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isSend = iBackService.sendMessage(string);
                        Toast.makeText(LongConnectActivity.this,
                                isSend ? "success" : "fail", Toast.LENGTH_SHORT)
                                .show();
                        et.setText("");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
        // 开始服务
        registerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册广播 最好在onResume中注册
        // registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销广播 最好在onPause上注销
        unregisterReceiver(mReceiver);
        // 注销服务
        unbindService(conn);
    }

    // 注册广播
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BackService.HEART_BEAT_ACTION);
        intentFilter.addAction(BackService.MESSAGE_ACTION);
        registerReceiver(mReceiver, intentFilter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 消息广播
            if (action.equals(BackService.MESSAGE_ACTION)) {
                String stringExtra = intent.getStringExtra("message");
                tv.setText(stringExtra);
            } else if (action.equals(BackService.HEART_BEAT_ACTION)) {// 心跳广播
                tv.setText("正常心跳");
            }
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 未连接为空
            iBackService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 已连接
            iBackService = IBackService.Stub.asInterface(service);
        }
    };
}
