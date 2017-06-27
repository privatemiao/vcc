/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.easyway.vcc.serial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

import com.easyway.vcc.net.Application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

public abstract class SerialPortActivity extends Activity {

    protected Application mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    private HeartBeatThread heartBeatThread;
    private WatchButtonUpThread watchButtonUpThread;

    private long sleepTime = 200;
    private long triggerTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (Application) getApplication();
        try {
            mSerialPort = mApplication.getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        triggerTime = 0;
        heartBeatThread = new HeartBeatThread();
        heartBeatThread.start();
        watchButtonUpThread = new WatchButtonUpThread();
        watchButtonUpThread.start();
    }

    protected void watchButtonUp() {
        watchButtonUpThread = new WatchButtonUpThread();
        watchButtonUpThread.start();
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void DisplayError(int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SerialPortActivity.this.finish();
            }
        });
        b.show();
    }


    private class HeartBeatThread extends Thread {
        @Override
        public void run() {
            super.run();

            while (!isInterrupted()) {
                try {
                    mOutputStream.write(("1").getBytes());
                    mOutputStream.write('\n');
                    Thread.sleep(sleepTime);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    interrupt();
                }
            }
        }
    }

    private class WatchButtonUpThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    interrupt();
                }
                if (checkEqual()) {
                    Log.d("VCC", "按钮抬起^^^^^^^^^^^");
                    triggerTime = 0;
                    onButtonUp();
                    break;
                }
            }
        }
    }

    protected abstract void onButtonUp();

    private long olderVal = 0;

    private boolean checkEqual() {
        if (olderVal == 0) {
            olderVal = triggerTime;
            return false;
        }
        if (olderVal == triggerTime) {
            Log.d("VCC", String.format("oldVal %d, newVal: %d", olderVal, triggerTime));
            olderVal = 0;
            return true;
        } else {
            olderVal = triggerTime;
        }
        return false;
    }

    protected void onDataReceived(final byte[] buffer, final int size) {
        triggerTime = System.currentTimeMillis();
        Log.d("VCC", triggerTime + "");
    }

    @Override
    protected void onDestroy() {
        Log.d("VCC", "SerialPortActivity Destroy");

        if (mReadThread != null) {
            mReadThread.interrupt();
        }

        if (watchButtonUpThread != null) {
            Log.d("VCC", "interrupt watchButtonUpThread");
            watchButtonUpThread.interrupt();
        }
        if (heartBeatThread != null) {
            Log.d("VCC", "interrupt heartBeatThread");
            heartBeatThread.interrupt();
        }

        mApplication.closeSerialPort();
        mSerialPort = null;

        super.onDestroy();
    }
}
