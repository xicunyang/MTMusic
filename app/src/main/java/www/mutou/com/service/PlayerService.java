package www.mutou.com.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import www.mutou.com.application.MyApplication;
import www.mutou.com.model.Mp3Info;

public class PlayerService extends Service {
    private static final String TAG = "PlayerService";
    private Mp3Info mp3info;
    private MediaPlayer myPlayer;

    public PlayerService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getStringExtra("WHO")){
            case "LOCAL":
                LOCAL(intent);
                break;

            case "URL":
                URL(intent);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //本地音乐的解决方案
    private void LOCAL(Intent intent){
        String PLAYFLAG = intent.getStringExtra("PLAYFLAG");
        mp3info = MyApplication.nowMp3Info;
        switch (PLAYFLAG){
            case "STOP2PLAY":
                Log.d(TAG, "onStartCommand: yxc--->STOP2PLAY");
                STOP2PLAY();
                MyApplication.isPlaying = true;
                break;
            case "PLAYNEW":
                Log.d(TAG, "onStartCommand: yxc--->PLAYNEW");
                myPlayer.stop();
                myPlayer.release();
                STOP2PLAY();
                MyApplication.isPlaying = true;
                break;
            case "PLAY2PAUSE":
                Log.d(TAG, "onStartCommand: yxc--->PLAY2PAUSE");
                PLAY2PAUSE();
                MyApplication.isPlaying = false;
                break;
            case "PAUSE2PLAY":
                Log.d(TAG, "onStartCommand: yxc--->PAUSE2PLAY");
                PAUSE2PLAY();
                MyApplication.isPlaying = true;
                break;
            case "STOP":
                Log.d(TAG, "onStartCommand: yxc--->STOP");
                STOP();
                MyApplication.isPlaying = false;
                break;
            case "NEXT":
                Log.d(TAG, "onStartCommand: yxc--->NEXT");
                NEXT();
                MyApplication.isPlaying = true;
                break;
            case "PREV":
                Log.d(TAG, "onStartCommand: yxc--->PREV");
                PREV();
                MyApplication.isPlaying = true;
                break;
            default:
                break;
        }
        //发送广播通知更新UI
        sendBroadCastToUI();
    }

    //网络音乐的解决方案
    private void URL(Intent intent){

    }

    //停止-->运行
    private void STOP2PLAY(){
        myPlayer = MediaPlayer.create(PlayerService.this, Uri.parse("file://"+mp3info.getFileUrl()));
        myPlayer.setLooping(true);
        myPlayer.start();
    }
    //运行-->暂停
    private void PLAY2PAUSE(){
        myPlayer.pause();
    }
    //暂停-->运行
    private void PAUSE2PLAY(){
        myPlayer.start();
    }
    //停止
    private void STOP(){
        myPlayer.stop();
    }
    //下一曲
    private void NEXT(){
        myPlayer.stop();
        myPlayer.release();
        myPlayer = MediaPlayer.create(PlayerService.this, Uri.parse("file://"+mp3info.getFileUrl()));
        myPlayer.setLooping(true);
        myPlayer.start();
    }
    //上一曲
    private void PREV() {
        myPlayer.stop();
        myPlayer.release();
        myPlayer = MediaPlayer.create(PlayerService.this, Uri.parse("file://"+mp3info.getFileUrl()));
        myPlayer.setLooping(true);
        myPlayer.start();
    }


    //发送广播---目的：在修改播放状态后希望更新到UI
    Intent changePlayStuate = new Intent("android.PlayServiceBroad");
    private void sendBroadCastToUI(){
        //不传具体参数了，根据全局isPlaying来判断
        changePlayStuate.putExtra("WHAT","changePlayOrPause");
        sendBroadcast(changePlayStuate);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}























