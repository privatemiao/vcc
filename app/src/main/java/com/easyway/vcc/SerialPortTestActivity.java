package com.easyway.vcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.easyway.vcc.serial.SerialPortActivity;

import java.io.IOException;
import java.util.Date;

public class SerialPortTestActivity extends SerialPortActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("VCC", "SerialPortTestActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_test);

    }

    @Override
    protected void onButtonUp() {
        finish();
    }


    @Override
    protected void onDestroy() {
        Log.d("VCC", "SerialPortTestActivity onDestroy");
        super.onDestroy();
    }
}
