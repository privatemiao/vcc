package com.easyway.vcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.easyway.vcc.serial.SerialPortActivity;

import java.util.Date;

public class SettingActivity extends SerialPortActivity {

    private static final int INTERVAL_OF_TWO_CLICK_TO_QUIT = 1000; // 1 seconde
    private long mLastPressBackTime = 0;
    private long sleepTime = 200;
    private long count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        findViewById(R.id.btn_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this
                        , StreamActivity.class));
            }
        });

        findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this
                        , PlayActivity.class));
            }
        });

        findViewById(R.id.btn_client).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, VideoChatActivity.class));
            }
        });

        findViewById(R.id.btn_serial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, SerialPortTestActivity.class));
            }
        });
        count = 0;
        heartbeat();
        watchButtonUp();
    }

    private void heartbeat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        mOutputStream.write(("1").getBytes());
                        mOutputStream.write('\n');
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        count++;
        Log.d("VCC", count + "");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    private void watchButtonUp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (checkEqual()){
                        Log.d("VCC", "按钮抬起^^^^^^^^^^^");
                        break;
                    }
                }
            }
        }).start();
    }

    private long olderVal = 0;

    private boolean checkEqual() {
        if (olderVal == 0){
            olderVal = count;
            return false;
        }
        if (olderVal == count){
            Log.d("VCC", String.format("oldVal %d, newVal: %d", olderVal, count));
            return true;
        }else{
            olderVal = count;
        }
        return false;
    }

}
