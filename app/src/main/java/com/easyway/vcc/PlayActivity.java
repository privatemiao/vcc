package com.easyway.vcc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.cloud.media.player.BDCloudMediaPlayer;
import com.baidu.cloud.media.player.IMediaPlayer;
import com.easyway.vcc.widget.BDCloudVideoView;

import java.io.IOException;

public class PlayActivity extends AppCompatActivity {

    SurfaceView svPlay;
    private BDCloudMediaPlayer mMediaPlayer;//播放

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        SurfaceView svPlay = (SurfaceView) findViewById(R.id.sv_play);

        play("rtmp://192.168.45.107/live/request");

    }

    private void play(String url) {
        Toast.makeText(PlayActivity.this, String.format("播放视频 %s", url), Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        super.onBackPressed();
    }
}
