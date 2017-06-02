package com.easyway.vcc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.baidu.recorder.api.LiveConfig;
import com.baidu.recorder.api.LiveSession;
import com.baidu.recorder.api.LiveSessionHW;
import com.baidu.recorder.api.LiveSessionSW;
import com.baidu.recorder.api.SessionStateListener;

import java.util.Date;

public class StreamActivity extends AppCompatActivity implements View.OnClickListener {

    private LiveSession mLiveSession = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);

        SurfaceView svPublish = (SurfaceView) findViewById(R.id.sv_view);


        LiveConfig liveConfig = new LiveConfig.Builder()
                .setCameraId(LiveConfig.CAMERA_FACING_FRONT) // 选择摄像头为前置摄像头
                .setCameraOrientation(1) // 设置摄像头为竖向
                .setVideoWidth(1280) // 设置推流视频宽度, 需传入长的一边
                .setVideoHeight(720) // 设置推流视频高度，需传入短的一边
                .setVideoFPS(15) // 设置视频帧率
                .setInitVideoBitrate(1024000) // 设置视频码率，单位为bit per seconds
                .setAudioBitrate(64 * 1000) // 设置音频码率，单位为bit per seconds
                .setAudioSampleRate(LiveConfig.AUDIO_SAMPLE_RATE_44100) // 设置音频采样率
                .setGopLengthInSeconds(2) // 设置I帧间隔，单位为秒
                .setQosEnabled(true) // 开启码率自适应，默认为true，即默认开启
                .setMinVideoBitrate(200 * 1000) // 码率自适应，最低码率
                .setMaxVideoBitrate(1024 * 1000) // 码率自适应，最高码率
                .setQosSensitivity(5) // 码率自适应，调整的灵敏度，单位为秒，可接受[5, 10]之间的整数值
                .build();

        mLiveSession = new LiveSessionHW(this, liveConfig);
//        mLiveSession = new LiveSessionSW(this, liveConfig);

        mLiveSession.bindPreviewDisplay(svPublish.getHolder());
        mLiveSession.prepareSessionAsync();

        mLiveSession.setStateListener(new SessionStateListener() {
            @Override
            public void onSessionPrepared(int i) {
//                mLiveSession.startRtmpSession("rtmp://192.168.45.107/live/request");
            }

            @Override
            public void onSessionStarted(int i) {

            }

            @Override
            public void onSessionStopped(int i) {

            }

            @Override
            public void onSessionError(int i) {
                Toast.makeText(StreamActivity.this, String.format("Error %d", i), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mLiveSession.destroyRtmpSession();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveSession.destroyRtmpSession();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                mLiveSession.startRtmpSession("rtmp://192.168.45.107/live/request" + new Date().getTime());
                break;
            case R.id.btn_stop:
                mLiveSession.stopRtmpSession();
                break;
        }
    }
}
