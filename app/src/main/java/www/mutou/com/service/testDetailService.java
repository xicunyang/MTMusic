package www.mutou.com.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import www.mutou.com.application.MyApplication;
import www.mutou.com.model.Mp3Info;
import www.mutou.com.utils.GetKuGouUrlByHash;
import www.mutou.com.utils.GetKuWoUrlByID;

public class testDetailService extends Service {
    private static final String TAG = "PlayerService";
    private String url;
    private MediaPlayer mediaPlayer;

    public testDetailService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ///storage/emulated/0/netease/cloudmusic/Music/孟大宝 房东的猫 - 秋又来了.mp3
        mediaPlayer = MediaPlayer.create(testDetailService.this, Uri.parse("file://" + "/storage/emulated/0/netease/cloudmusic/Music/孟大宝 房东的猫 - 秋又来了.mp3"));
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        mediaPlayer.getCurrentPosition();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }


    public class MyBind extends Binder {
        //获取歌曲长度
        public int getMusicDuration() {
            int duration = 0;
            if (mediaPlayer != null) {
                duration = mediaPlayer.getDuration();
            }
            return duration;
        }

        //获取当前播放位置
        public int getMusicCurrentPosition() {
            int pos = 0;
            if (mediaPlayer != null) {
                pos = mediaPlayer.getCurrentPosition();
            }
            return pos;
        }

        //设置指定位置
        public void seekTo(int position) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(position);
            }
        }
    }
}























