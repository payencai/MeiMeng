package com.example.meimeng.util;

import android.os.CountDownTimer;
import android.widget.TextView;

public class TimerCount extends CountDownTimer {
    private TextView bnt;

    public TimerCount(long millisInFuture, long countDownInterval, TextView bnt) {
        super(millisInFuture, countDownInterval);
        this.bnt = bnt;
    }

    public TimerCount(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onFinish() {
        // TODO Auto-generated method stub
        bnt.setClickable(true);
        bnt.setText("获取验证码");
    }

    @Override
    public void onTick(long arg0) {
        // TODO Auto-generated method stub
        bnt.setClickable(false);
        bnt.setText("等待"+arg0 / 1000+"秒");
    }

}
