package com.easyway.vcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private static final int INTERVAL_OF_TWO_CLICK_TO_QUIT = 1000; // 1 seconde
    private long mLastPressBackTime = 0;

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


    }

}
