package com.dev_marinov.convertercft;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyAlarm extends BroadcastReceiver {

    //метод будет запущен, когда сработает тревога
    @Override
    public void onReceive(Context context, Intent intent) {
        // выполняем задачу на каждый день тут
        Log.e("MyAlarmBelal", "Alarm just fired");
        Log.e("MyAlarmBelal", "-intent-" + intent);
    }
}