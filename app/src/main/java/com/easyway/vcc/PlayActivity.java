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
import com.alivc.player.AccessKey;
import com.alivc.player.AccessKeyCallback;
import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;

import java.util.HashMap;
import java.util.Map;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SERVER_IP = "10.100.103.13";
    private SurfaceView svPlay;
//    private SurfaceView svView;
    private AlivcMediaRecorder mMediaRecorder;
    private AliVcMediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

//        svView = (SurfaceView) findViewById(R.id.sv_view);//预览推送视频
        svPlay = (SurfaceView) findViewById(R.id.sv_play);//拉取播放视频

        AliVcMediaPlayer.init(getApplicationContext(), "", new AccessKeyCallback() {
            public AccessKey getAccessToken() {
                return new AccessKey("", "");
            }
        });

        initUI();

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);


    }

    private void initUI() {
        /*svView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mMediaRecorder = AlivcMediaRecorderFactory.createMediaRecorder();
                mMediaRecorder.init(PlayActivity.this);
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
        });*/


        svPlay.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mPlayer = new AliVcMediaPlayer(PlayActivity.this, svPlay);

                // 设置图像适配屏幕，适配最长边
                mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                // 设置图像适配屏幕，适配最短边，超出部分裁剪
                mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

                mPlayer.setMaxBufferDuration(-1);

                //设置缺省编码类型：0表示硬解；1表示软解；
                //如果缺省为硬解，在使用硬解时如果解码失败，会尝试使用软解
                //如果缺省为软解，则一直使用软解，软解较为耗电，建议移动设备尽量使用硬解
                mPlayer.setDefaultDecoder(0);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mPlayer.stop();
                mPlayer.destroy();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                /*try {
                    mMediaRecorder.startRecord(String.format("rtmp://%s/live/request", SERVER_IP));
                    Toast.makeText(PlayActivity.this, "~~~~~~推送视频~~~~~~", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                mPlayer.prepareAndPlay(String.format("rtmp://%s/live/mel", SERVER_IP));
                Toast.makeText(PlayActivity.this, "~~~~~~拉取视频~~~~~~", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_stop:
//                mMediaRecorder.stopRecord();
                mPlayer.stop();
                break;
        }
    }
}
