package com.easyway.vcc;

import android.os.Bundle;
import android.widget.EditText;

import com.easyway.vcc.serial.SerialPortActivity;

import java.io.IOException;
import java.util.Date;

public class SerialPortTestActivity extends SerialPortActivity {
    private EditText txtInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_test);

        txtInfo = (EditText) findViewById(R.id.txtInfo);

        heartbeat();
    }

    private void heartbeat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        mOutputStream.write(("" + new Date().getTime()).getBytes());
                        mOutputStream.write('\n');
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (txtInfo != null){
                    txtInfo.append(new String(buffer, 0, size));
                }
            }
        });
    }


}
