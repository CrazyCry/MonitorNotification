package com.meng.monitornotification.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.meng.monitornotification.R;
import com.meng.monitornotification.service.CheckMonitorService;

public class MainActivity extends AppCompatActivity {

    private Button startService, stopService;
    private TextView status;
    private CheckBox ring, vibration;
    private EditText url, looptime;
    private Intent intent;
    public static Handler handler;
    public static boolean flag = false;
    private Vibrator vibrator;
    public static boolean vibrationEnable = true;
    public static boolean ringEnable = true;
    private Ringtone r;
    public static int loopTime;
    public static String uri;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
        intent = new Intent(MainActivity.this, CheckMonitorService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    status.setText(msg.obj.toString());
                } else if (msg.what == 2) {
                    if (flag) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    if (flag) {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                        vibrator.vibrate(1000);
                                    }
                                }
                            }
                        }).start();
                    }
                } else if (msg.what == 3) {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    r = RingtoneManager.getRingtone(MainActivity.this, notification);
                    r.setLooping(true);
                    r.play();
                    if (flag = false) {
                        r.stop();
                    }
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences("TimeWithCounts",
                MainActivity.MODE_PRIVATE);
        String times = sharedPreferences.getString("url", "");
        String counts = sharedPreferences.getString("looptime", "");
        if (times != "" | counts != "") {
            url.setText(times);
            looptime.setText(counts);
        }
    }

    private void setViews() {
        startService = findViewById(R.id.startService);
        stopService = findViewById(R.id.stopService);
        status = findViewById(R.id.status);
        ring = findViewById(R.id.ring);
        vibration = findViewById(R.id.vibration);
        url = findViewById(R.id.url);
        looptime = findViewById(R.id.looptime);
        uri=url.getText().toString();
        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri=="" || uri.length()<8){
                    url.setText("请输入监控API端口");
                    return;
                }
                intent.putExtra("flag", true);
                intent.putExtra("vibrationEnable", vibrationEnable);
                intent.putExtra("ringEnable", ringEnable);
                loopTime = Integer.parseInt(looptime.getText().toString());
                loopTime = loopTime > 0 ? loopTime : 5;
                intent.putExtra("looptime", loopTime);
                intent.putExtra("url",url.getText().toString());
                flag = true;
                startService(intent);
                stopService.setClickable(true);
                SharedPreferences sharedPreferences = getSharedPreferences("TimeWithCounts",
                        MainActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("url",uri);
                editor.putString("looptime", looptime.getText().toString());
                editor.apply();
            }
        });

        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status.setText("服务已停止！");
                flag = false;
                if (r != null)
                    r.stop();
            }
        });
        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (vibration.isChecked()) {
                    vibrationEnable = true;
                } else {
                    vibrationEnable = false;
                }
            }
        });
        ring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ring.isChecked()) {
                    ringEnable = true;
                } else {
                    ringEnable = false;
                }
            }
        });
    }
}
