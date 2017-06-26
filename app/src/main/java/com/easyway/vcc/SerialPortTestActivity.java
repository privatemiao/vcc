package com.easyway.vcc;

import android.os.Bundle;

import com.easyway.vcc.serial.SerialPortActivity;

public class SerialPortTestActivity extends SerialPortActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_test);

    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {

    }


}
