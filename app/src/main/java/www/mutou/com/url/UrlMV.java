package www.mutou.com.url;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import www.mutou.com.application.MyApplication;
import www.mutou.com.mtmusic.R;
import www.mutou.com.utils.GetKuwoMvUrl;
import www.mutou.com.utils.PlayerControls;

import static android.content.ContentValues.TAG;

public class UrlMV extends Activity {

    private VideoView videoView;
    private MediaController controller;
    private String url;
    private ProgressBar mv_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义控件
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                ,WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        //横屏
        setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        );
        setContentView(R.layout.activity_url_mv);
        mv_progressBar = (ProgressBar) findViewById(R.id.mv_progressBar);
        mv_progressBar.setVisibility(View.VISIBLE);

        videoView = (VideoView) findViewById(R.id.myMvPlayer);
        controller = new MediaController(this);
        //设置播放的文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                url = new GetKuwoMvUrl().getUrl(MyApplication.nowPlayingMvUrl);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "onCreate: "+ url);
                        videoView.setVideoPath(url);
                        //关联VideoView和MediaController
                        controller.setMediaPlayer(videoView);
                        videoView.setMediaController(controller);
                        //开始播放
                        videoView.start();
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                                    @Override
                                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                                        if(percent>0){
                                            mv_progressBar.setVisibility(View.GONE);
                                        }
                                        Log.e(TAG, "onBufferingUpdate: --->"+percent);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }).start();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            PlayerControls playerControls = new PlayerControls(this);
            playerControls.playOrPause();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
