package www.mutou.com.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

public class testUrlPlayer extends Service {
    private static final String TAG = "testUrlPlayer";
    public testUrlPlayer() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        MediaPlayer mediaPlayer = MediaPlayer.create(testUrlPlayer.this, Uri.parse("http://win.web.ri01.sycdn.kuwo.cn/f86334da062a324b4d7c4e80a430f8fc/5b30a719/resource/n2/43/84/3711904424.mp3"));
//        mediaPlayer.prepareAsync();

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.d(TAG, "onBufferingUpdate: yxc---"+percent);
            }
        });
        mediaPlayer.start();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //Log.d(TAG, "onPrepared: --->okokok");
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
