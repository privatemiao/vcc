package com.easyway.vcc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        System.out.println("Welcome Splash");
        Utils.processDelay(new IProcess() {
            @Override
            public Message doProcess() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.obj = "Welcome";
                return message;
            }
        }, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(SplashActivity.this
                        , String.format("Send message is %s.", msg.obj), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this
                        , SettingActivity.class));
            }
        });
    }


}
