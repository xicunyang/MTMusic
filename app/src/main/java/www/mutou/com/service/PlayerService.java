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

public class PlayerService extends Service {
    private static final String TAG = "PlayerService";
    private Mp3Info mp3info;
    private MediaPlayer myPlayer = null;
    private MediaPlayer mediaPlayer_url = null;
    private String url;

    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getStringExtra("WHO")) {
            case "LOCAL":
                LOCAL(intent);
                break;
            case "URL":
                try {
                    URL(intent);
                } catch (Exception e) {

                }

                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //本地音乐的解决方案
    private void LOCAL(Intent intent) {
        if (mediaPlayer_url != null) {
            if (mediaPlayer_url.isLooping()) {
                mediaPlayer_url.stop();
                mediaPlayer_url.release();
                mediaPlayer_url = null;
            }
        }
        String PLAYFLAG = intent.getStringExtra("PLAYFLAG");
        mp3info = MyApplication.nowMp3Info;
        switch (PLAYFLAG) {
            case "STOP2PLAY":
                STOP2PLAY();
                MyApplication.isPlaying_local = true;
                break;
            case "PLAYNEW":
                myPlayer.stop();
                myPlayer.release();
                STOP2PLAY();
                MyApplication.isPlaying_local = true;
                break;
            case "PLAY2PAUSE":
                PLAY2PAUSE();
                MyApplication.isPlaying_local = false;
                break;
            case "PAUSE2PLAY":
                PAUSE2PLAY();
                MyApplication.isPlaying_local = true;
                break;
            case "STOP":
                STOP();
                MyApplication.isPlaying_local = false;
                break;
            case "NEXT":
                NEXT();
                MyApplication.isPlaying_local = true;
                break;
            case "PREV":
                PREV();
                MyApplication.isPlaying_local = true;
                break;
            default:
                break;
        }
        //发送广播通知更新UI
        sendBroadCastToUI();
    }

    //网络音乐的解决方案
    private void URL(Intent intent) {
        if (myPlayer != null) {
            if (myPlayer.isLooping()) {
                myPlayer.stop();
                myPlayer.release();
                myPlayer = null;
            }
        }

        String PLAYFLAG = intent.getStringExtra("PLAYFLAG");
        switch (PLAYFLAG) {
            case "STOP2PLAY":
                STOP2PLAYURL();
                MyApplication.isPlaying_url = true;
                break;
            case "PLAYNEW":
                mediaPlayer_url.stop();
                mediaPlayer_url.release();
                STOP2PLAYURL();
                MyApplication.isPlaying_url = true;
                break;
            case "PLAY2PAUSE":
                PLAY2PAUSEURL();
                MyApplication.isPlaying_url = false;
                break;
            case "PAUSE2PLAY":
                PAUSE2PLAYURL();
                MyApplication.isPlaying_url = true;
                break;
            case "STOP":
                STOPURL();
                MyApplication.isPlaying_url = false;
                break;
            case "NEXT":
                NEXTURL();
                MyApplication.isPlaying_url = true;
                break;
            case "PREV":
                PREVURL();
                MyApplication.isPlaying_url = true;
                break;
            default:
                break;
        }
        //发送广播通知更新UI
        sendBroadCastToUI();
    }

    private void PLAY2PAUSEURL() {
        mediaPlayer_url.pause();
    }

    private void PAUSE2PLAYURL() {
        mediaPlayer_url.start();
    }

    private void STOPURL() {
        mediaPlayer_url.stop();
    }

    private void NEXTURL() {
        mediaPlayer_url.stop();
        mediaPlayer_url.release();
        STOP2PLAYURL();
    }

    private void PREVURL() {
        mediaPlayer_url.stop();
        mediaPlayer_url.release();
        STOP2PLAYURL();
    }


    //停止-->运行
    private void STOP2PLAYURL() {
        switch (MyApplication.isWho) {
            case "kw":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        url = new GetKuWoUrlByID().getKWMp3Url(MyApplication.nowUrlMp3Info_kw.getMP3RID());
                        handler.sendMessage(Message.obtain());
                    }
                }).start();
                break;
            case "kg":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        url = new GetKuGouUrlByHash().getUrl(MyApplication.nowUrlMp3Info_kg.getFileHash());
                        handler.sendMessage(Message.obtain());
                    }
                }).start();
                break;

            default:
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mediaPlayer_url = new MediaPlayer();
            try {
                mediaPlayer_url.setDataSource(PlayerService.this, Uri.parse(url));
            } catch (IOException e) {
            }
            mediaPlayer_url.prepareAsync();
            mediaPlayer_url.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer_url.setLooping(true);
                    mediaPlayer_url.start();
                }
            });

            super.handleMessage(msg);
        }
    };

    //停止-->运行
    private void STOP2PLAY() {
        Log.e(TAG, "onStartCommand: 6");
        myPlayer = MediaPlayer.create(PlayerService.this, Uri.parse("file://" + mp3info.getFileUrl()));
        Log.e(TAG, "STOP2PLAY: ---url--->" + mp3info.getFileUrl());
        myPlayer.setLooping(true);
        myPlayer.start();
    }

    //运行-->暂停
    private void PLAY2PAUSE() {
        myPlayer.pause();
    }

    //暂停-->运行
    private void PAUSE2PLAY() {
        myPlayer.start();
    }

    //停止
    private void STOP() {
        myPlayer.stop();
    }

    //下一曲
    private void NEXT() {
        myPlayer.stop();
        myPlayer.release();
        myPlayer = MediaPlayer.create(PlayerService.this, Uri.parse("file://" + mp3info.getFileUrl()));
        myPlayer.setLooping(true);
        myPlayer.start();
    }

    //上一曲
    private void PREV() {
        myPlayer.stop();
        myPlayer.release();
        myPlayer = MediaPlayer.create(PlayerService.this, Uri.parse("file://" + mp3info.getFileUrl()));
        myPlayer.setLooping(true);
        myPlayer.start();
    }


    //发送广播---目的：在修改播放状态后希望更新到UI
    Intent changePlayStuate = new Intent("android.PlayServiceBroad");

    private void sendBroadCastToUI() {
        //不传具体参数了，根据全局isPlaying来判断
        changePlayStuate.putExtra("WHAT", "changePlayOrPause");
        sendBroadcast(changePlayStuate);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }

    public class MyBind extends Binder {
        //获取歌曲时长
        public int getDuration() {
            Log.e(TAG, "getDuration: isPlaying_url?--->" + MyApplication.isPlaying_url);
            Log.e(TAG, "getDuration: isPlaying_local?--->" + MyApplication.isPlaying_local);
            int duration = 0;
            if (MyApplication.isLocal) {
                if (myPlayer != null) {
                    duration = myPlayer.getDuration();
                }
            } else {
                if (mediaPlayer_url != null) {
                    duration = mediaPlayer_url.getDuration();
                }
            }
            return duration;
        }

        //获取实时位置
        public int getCurrentPosition() {
            int pos = 0;
            if (MyApplication.isLocal) {
                if (myPlayer != null) {
                    pos = myPlayer.getCurrentPosition();
                }
            } else {
                if (mediaPlayer_url != null) {
                    pos = mediaPlayer_url.getCurrentPosition();
                }
            }
            return pos;
        }

        //设置歌曲播放位置
        public void setSeekTo(int position) {
            if (MyApplication.isLocal) {
                if (myPlayer != null) {
                    myPlayer.seekTo(position);
                }
            } else {
                if (mediaPlayer_url != null) {
                    mediaPlayer_url.seekTo(position);
                }
            }
        }
    }
}























