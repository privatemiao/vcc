package com.easyway.vcc;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.baidu.cloud.media.player.BDCloudMediaPlayer;
import com.baidu.cloud.media.player.IMediaPlayer;
import com.baidu.recorder.api.LiveConfig;
import com.baidu.recorder.api.LiveSession;
import com.baidu.recorder.api.LiveSessionHW;
import com.baidu.recorder.api.SessionStateListener;
import com.easyway.vcc.net.Application;
import com.ewivt.vhs.dto.request.EndHelpRequest;
import com.ewivt.vhs.dto.request.HelpRequest;
import com.ewivt.vhs.dto.response.HelpResponse;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class VideoChatActivity extends AppCompatActivity implements View.OnClickListener {

    SurfaceView svPlay;
    SurfaceView svPublish;

    private LiveSession mLiveSession = null;//发布

    private BDCloudMediaPlayer mMediaPlayer;//播放

    private String type;

    private Application application;
    private CompositeDisposable _disposables;

    public static final String STREAM_SERVER = "rtmp://10.100.103.13/live";

    public boolean isPublished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        application = (Application) this.getApplication();


        svPlay = (SurfaceView) findViewById(R.id.sv_play);
        svPublish = (SurfaceView) findViewById(R.id.sv_publish);

        svPublish.setZOrderOnTop(true);

        type = getIntent().getStringExtra("type");


        initUI();


        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);

        initCommProcessor();

//        register();
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
        //publish
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
        mLiveSession.bindPreviewDisplay(svPublish.getHolder());
        mLiveSession.prepareSessionAsync();
        mLiveSession.setStateListener(new SessionStateListener() {
            @Override
            public void onSessionPrepared(int i) {
                // TODO 设置开始按钮状态为有效
            }

            @Override
            public void onSessionStarted(int i) {

            }

            @Override
            public void onSessionStopped(int i) {

            }

            @Override
            public void onSessionError(int i) {

            }
        });
    }


    private void play(String url) {
        Toast.makeText(VideoChatActivity.this, String.format("播放视频 %s", url), Toast.LENGTH_LONG).show();
        mMediaPlayer = new BDCloudMediaPlayer(this.getApplicationContext());
        try {
            mMediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mMediaPlayer.setDisplay(svPlay.getHolder());
                mMediaPlayer.start();
            }
        });

        mMediaPlayer.prepareAsync();


    }


    private void publish(final String url) {
        mLiveSession.startRtmpSession(url);
    }

    private String getRequestUrl() {
        return STREAM_SERVER;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveSession.stopRtmpSession();
//        mLiveSession.destroyRtmpSession();
        mMediaPlayer.release();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mLiveSession.stopRtmpSession();
//        mLiveSession.destroyRtmpSession();
        mMediaPlayer.release();
    }


    /*private void register() {
        Utils.processDelay(new IProcess() {
            @Override
            public Message doProcess() {
                Message message = new Message();


                RegisterRequest request = new RegisterRequest();
                request.setRtmpAddress(getRequestUrl());
                request.setClientId("0001");
                request.setClientType(1);
                request.setClientName("Client0001");
                application.demoClientHandler.sendRequest(request);

                message.obj = "Send register request success";
                return message;
            }
        }, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(VideoChatActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                if (!isPublished) {
                    publish(getRequestUrl() + "/" + "Client0001");
                    isPublished = true;
                }

                Utils.processDelay(new IProcess() {
                    @Override
                    public Message doProcess() {
                        Message message = new Message();

                        HelpRequest request = new HelpRequest();
                        request.setRtmpAddress(getRequestUrl());
                        request.setClientId("0001");
                        request.setClientType(1);
                        request.setClientName("Client0001");
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


                break;
            case R.id.btn_stop:
                Utils.processDelay(new IProcess() {
                    @Override
                    public Message doProcess() {
                        Message message = new Message();

                        EndHelpRequest request = new EndHelpRequest();
                        request.setClientId("0001");
                        request.setClientType(1);
                        application.demoClientHandler.sendRequest(request);

                        message.obj = "发送停止帮助请求";
                        return message;
                    }
                }, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        Toast.makeText(VideoChatActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                mLiveSession.stopRtmpSession();
                isPublished = false;
                mMediaPlayer.stop();
                mMediaPlayer.release();
                break;
        }
    }
}
