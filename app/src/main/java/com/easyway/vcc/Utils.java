package com.easyway.vcc;

import android.os.Handler;
import android.os.Message;

/**
 * Created by mel on 5/12/2017.
 */
public class Utils {
    public static void processDelay(final IProcess process, final Handler handler) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Message message = process.doProcess();
                        handler.sendMessage(message);
                    }
                }
        ).start();
    }

    public static void processDelay(final IProcess process, final Handler handler, final Long time) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Message message = process.doProcess();
                        try {
                            Thread.sleep(time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendMessage(message);
                    }
                }
        ).start();
    }
}
