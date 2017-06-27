package com.easyway.vcc;

import android.os.Bundle;
import android.widget.EditText;

import com.easyway.vcc.serial.SerialPortActivity;

import java.io.IOException;
import java.util.Date;

public class SerialPortTestActivity extends SerialPortActivity {
    private boolean beat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_test);

        beat = true;
        heartbeat();
    }

    private void heartbeat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!beat){
                        break;
                    }
                    try {
                        mOutputStream.write("1".getBytes());
                        mOutputStream.write('\n');
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        beat = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

    }


}
