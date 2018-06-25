package www.mutou.com.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import www.mutou.com.application.MyApplication;

public class PlayerService extends Service {
    private static final String TAG = "PlayerService";
    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String PLAYFLAG = intent.getStringExtra("PLAYFLAG");
        switch (PLAYFLAG){
            case "STOP2PLAY":
                Log.d(TAG, "onStartCommand: yxc--->STOP2PLAY");
                MyApplication.isPlaying = true;
                break;
            case "PLAY2PAUSE":
                Log.d(TAG, "onStartCommand: yxc--->PLAY2PAUSE");
                MyApplication.isPlaying = false;
                break;
            case "PAUSE2PLAY":
                Log.d(TAG, "onStartCommand: yxc--->PAUSE2PLAY");
                MyApplication.isPlaying = true;
                break;
            case "STOP":
                Log.d(TAG, "onStartCommand: yxc--->STOP");
                MyApplication.isPlaying = false;
                break;
            case "NEXT":
                Log.d(TAG, "onStartCommand: yxc--->NEXT");
                MyApplication.isPlaying = true;
                break;
            case "PREV":
                Log.d(TAG, "onStartCommand: yxc--->PREV");
                MyApplication.isPlaying = true;
                break;
            default:
                break;
        }
        //发送广播通知更新UI
        sendBroadCastToUI();

        return super.onStartCommand(intent, flags, startId);
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























