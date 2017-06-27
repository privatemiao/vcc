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

    }

    @Override
    protected void onButtonUp() {
        Log.d("VCC", "YES");
//        watchButtonUp();
        startActivity(new Intent(SettingActivity.this, SerialPortTestActivity.class));
        finish();
    }

}
