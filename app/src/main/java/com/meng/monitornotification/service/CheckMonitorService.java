package com.meng.monitornotification.service;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.meng.monitornotification.activity.MainActivity;
import com.meng.monitornotification.broadcast.AlarmReceiver;
import com.meng.monitornotification.nettools.NetTools;

public class CheckMonitorService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Message msg = new Message();
        msg.what = 1;
        if (intent.getExtras().getBoolean("flag")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (NetTools.getStatus(intent.getExtras().getString("url"))) {
//                        Log.d("CheckMonitorService", "Service起来了");
                        if (intent.getExtras().getBoolean("vibrationEnable")) {
                            Message msg = new Message();
                            msg.what = 2;
                            MainActivity.handler.sendMessage(msg);

                        }
                        if (intent.getExtras().getBoolean("ringEnable")){
                            Message msg = new Message();
                            msg.what = 3;
                            MainActivity.handler.sendMessage(msg);
                        }
                    }
                }
            }).start();
            //28之后禁止后台启动service，通过发通知的方式，来前台启动service
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel("1", "Check Moniter Service", NotificationManager.IMPORTANCE_DEFAULT));
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
            startForeground(2, builder.build());

            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            int checkInterval = intent.getExtras().getInt("looptime")*60 * 1000;
//        int checkInterval= 5*60*1000;
            long triggerAtTime = SystemClock.elapsedRealtime() + checkInterval;
            Intent i = new Intent(this, AlarmReceiver.class);
            i.putExtra("time", System.currentTimeMillis());
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
            //给Activity发消息，更新进度
            msg.obj = "服务正在运行中...";

        } else {

//            msg.obj = String.valueOf(System.currentTimeMillis());
            msg.obj = "服务已停止！";
        }
        MainActivity.handler.sendMessage(msg);
        return super.onStartCommand(intent, flags, startId);
    }
}
