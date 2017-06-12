package com.easyway.vcc;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.alibaba.livecloud.live.AlivcMediaFormat;
import com.alibaba.livecloud.live.AlivcMediaRecorder;
import com.alibaba.livecloud.live.AlivcMediaRecorderFactory;

import java.util.HashMap;
import java.util.Map;

public class StreamActivity extends AppCompatActivity implements View.OnClickListener {


    private AlivcMediaRecorder mMediaRecorder;
    private SurfaceView svView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stream);

        svView = (SurfaceView) findViewById(R.id.sv_view);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);

        initUI();
    }

    public void initUI() {
        svView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                holder.setKeepScreenOn(true);
                Map<String, Object> mConfigure = new HashMap<>();
                mConfigure.put(AlivcMediaFormat.KEY_CAMERA_FACING, AlivcMediaFormat.CAMERA_FACING_FRONT);
                mConfigure.put(AlivcMediaFormat.KEY_MAX_ZOOM_LEVEL, 3);
                mConfigure.put(AlivcMediaFormat.KEY_OUTPUT_RESOLUTION, AlivcMediaFormat.OUTPUT_RESOLUTION_240P);
                mMediaRecorder.prepare(mConfigure, svView.getHolder().getSurface());
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mMediaRecorder.setPreviewSize(width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mMediaRecorder.stopRecord();
                mMediaRecorder.reset();
            }
        });

        mMediaRecorder = AlivcMediaRecorderFactory.createMediaRecorder();
        mMediaRecorder.init(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                Toast.makeText(StreamActivity.this, "~~~~~~推送视频~~~~~~", Toast.LENGTH_SHORT).show();
                try {
                    mMediaRecorder.startRecord("rtmp://10.100.103.13/live/request");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_stop:
                mMediaRecorder.stopRecord();
                break;
        }
    }
}
