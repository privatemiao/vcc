package com.easyway.vcc;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.livecloud.live.AlivcMediaFormat;
import com.alibaba.livecloud.live.AlivcMediaRecorder;
import com.alibaba.livecloud.live.AlivcMediaRecorderFactory;
import com.alivc.player.AccessKey;
import com.alivc.player.AccessKeyCallback;
import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.easyway.vcc.net.Application;
import com.ewivt.vhs.dto.request.EndHelpRequest;
import com.ewivt.vhs.dto.request.HelpRequest;
import com.ewivt.vhs.dto.response.HelpResponse;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class VideoChatActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String STREAM_SERVER = "rtmp://10.100.103.13/live";
    public static final String CLIENT_NAME = "Client0001";
    public static final String CLIENT_ID = "0001";

    private SurfaceView svPlay;
    private SurfaceView svPublish;

    private Application application;

    private AlivcMediaRecorder mMediaRecorder;
    private AliVcMediaPlayer mPlayer;

    private CompositeDisposable _disposables;

    private EditText txtLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        application = (Application) this.getApplication();

        svPlay = (SurfaceView) findViewById(R.id.sv_play);
        svPublish = (SurfaceView) findViewById(R.id.sv_publish);

        svPublish.setZOrderOnTop(true);


        txtLog = (EditText) findViewById(R.id.txt_log);

        AliVcMediaPlayer.init(getApplicationContext(), "", new AccessKeyCallback() {
            public AccessKey getAccessToken() {
                return new AccessKey("", "");
            }
        });

        initUI();

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);

        initCommProcessor();

        findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(null);
            }
        });

    }


    private void initCommProcessor() {
        _disposables = new CompositeDisposable();

        _disposables.add(
                application.getRxBusSingleton()
                        .asFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(@NonNull Object o) throws Exception {
                                Toast.makeText(VideoChatActivity.this, o.toString(), Toast.LENGTH_SHORT).show();
                                log(String.format("Response: %s", o.toString()));
                                if (o instanceof HelpResponse) {
                                    String responseURL = ((HelpResponse) o).getRtmpAddress();
                                    String name = ((HelpResponse) o).getStaffName();
                                    Toast.makeText(VideoChatActivity.this, responseURL + "    " + name + "    " + ((HelpResponse) o).getFlag(), Toast.LENGTH_SHORT).show();
                                    System.err.println("PrivateX " + responseURL + "/" + name);
                                    if (responseURL != null && responseURL.length() > 0) {
                                        play(responseURL + "/" + name);
                                    }
                                }
                            }
                        }));

    }


    private void initUI() {
        svPublish.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mMediaRecorder = AlivcMediaRecorderFactory.createMediaRecorder();
                mMediaRecorder.init(VideoChatActivity.this);
                holder.setKeepScreenOn(true);
                Map<String, Object> mConfigure = new HashMap<>();
                mConfigure.put(AlivcMediaFormat.KEY_CAMERA_FACING, AlivcMediaFormat.CAMERA_FACING_FRONT);
                mConfigure.put(AlivcMediaFormat.KEY_MAX_ZOOM_LEVEL, 3);
                mConfigure.put(AlivcMediaFormat.KEY_OUTPUT_RESOLUTION, AlivcMediaFormat.OUTPUT_RESOLUTION_240P);
                mMediaRecorder.prepare(mConfigure, svPublish.getHolder().getSurface());
                log("Publish UI Done.");
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


        svPlay.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mPlayer = new AliVcMediaPlayer(VideoChatActivity.this, svPlay);

                // 设置图像适配屏幕，适配最长边
                mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                // 设置图像适配屏幕，适配最短边，超出部分裁剪
                mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

                mPlayer.setMaxBufferDuration(-1);

                //设置缺省编码类型：0表示硬解；1表示软解；
                //如果缺省为硬解，在使用硬解时如果解码失败，会尝试使用软解
                //如果缺省为软解，则一直使用软解，软解较为耗电，建议移动设备尽量使用硬解
                mPlayer.setDefaultDecoder(0);
                log("Play UI Done.");
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


    String clientURL = null;
    private void play(String url) {
        if (url == null){
            if (clientURL != null){
                url = clientURL;
            }
        }
        clientURL = url;
        mPlayer.prepareAndPlay(url);
        Toast.makeText(VideoChatActivity.this, "~~~~~~拉取视频~~~~~~", Toast.LENGTH_SHORT).show();
        log(String.format("Pull Stream %s", url));
    }


    private void publish(final String url) {
        mMediaRecorder.startRecord(url);
        Toast.makeText(VideoChatActivity.this, "~~~~~~推送视频~~~~~~", Toast.LENGTH_SHORT).show();
        log(String.format("Push Stream %s", url));
    }

    private String getRequestUrl() {
        return STREAM_SERVER;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                publish(getRequestUrl() + "/" + CLIENT_NAME);

                Utils.processDelay(new IProcess() {
                    @Override
                    public Message doProcess() {
                        Message message = new Message();

                        HelpRequest request = new HelpRequest();
                        request.setRtmpAddress(getRequestUrl());
                        request.setClientId(CLIENT_ID);
                        request.setClientName(CLIENT_NAME);
                        request.setClientType(1);
                        application.demoClientHandler.sendRequest(request);

                        message.obj = "发送帮助请求";
                        return message;
                    }
                }, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        Toast.makeText(VideoChatActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                });



//                play(getRequestUrl() + "/" + CLIENT_NAME);


                break;
            case R.id.btn_stop:
                mPlayer.stop();
                mMediaRecorder.stopRecord();
                log("Stop Pull Stream.");
                log("Stop Push Stream.");

                Utils.processDelay(new IProcess() {
                    @Override
                    public Message doProcess() {
                        Message message = new Message();

                        EndHelpRequest request = new EndHelpRequest();
                        request.setClientId(CLIENT_ID);
                        request.setClientType(1);
                        application.demoClientHandler.sendRequest(request);

                        message.obj = "发送停止帮助请求";
                        return message;
                    }
                }, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        Toast.makeText(VideoChatActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        log(msg.obj.toString());
                    }
                });

                break;
        }
    }


    private void log(String msg) {
        txtLog.append(msg + "\r\n");
    }
}
