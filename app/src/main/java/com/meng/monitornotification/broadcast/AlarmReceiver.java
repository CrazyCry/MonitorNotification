package com.meng.monitornotification.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.meng.monitornotification.activity.MainActivity;
import com.meng.monitornotification.service.CheckMonitorService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i =new Intent(context, CheckMonitorService.class);
        i.putExtra("looptime",MainActivity.loopTime);
        i.putExtra("url",MainActivity.uri);
        if (MainActivity.flag){
            i.putExtra("flag",true);
        }else {
            i.putExtra("flag", false);
        }
        if (MainActivity.ringEnable){
            i.putExtra("ringEnable",true);
        }else {
            i.putExtra("ringEnable", false);
        }
        if (MainActivity.vibrationEnable){
            i.putExtra("vibrationEnable",true);
        }else {
            i.putExtra("vibrationEnable", false);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);
    }
}
