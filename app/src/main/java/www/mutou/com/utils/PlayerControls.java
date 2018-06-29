package www.mutou.com.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import www.mutou.com.application.MyApplication;
import www.mutou.com.mtmusic.MainActivity;
import www.mutou.com.mtmusic.R;
import www.mutou.com.service.PlayerService;

/**
 * Created by 木头 on 2018/6/28.
 */

public class PlayerControls {
    private static final String TAG = "PlayerControls";
    Context mContext;
    public PlayerControls(Context context){
        this.mContext = context;
    }
    public void prev(){
        if(MyApplication.isLocal){
            //本地
            //alllist为空
            if(MyApplication.allLocalmp3list== null){
                return;
            }
            //当前播放位置为空
            if(MyApplication.nowPosition == -1){
                return;
            }
            //如果点击下来的瞬间---当前位置已经是第一个了，那么就将当前位置设置为末尾+1
            if(MyApplication.nowPosition == 0){
                MyApplication.nowPosition = MyApplication.allLocalmp3list.size();
            }
            //当前播放位置-1
            MyApplication.nowPosition --;
            //设置当前播放的音乐实体
            MyApplication.nowMp3Info = MyApplication.allLocalmp3list.get(MyApplication.nowPosition);
            //通知Service播放音乐
            Intent intent = new Intent();
            intent.putExtra("PLAYFLAG","PREV");
            intent.putExtra("WHO","LOCAL");
            intent.setClass(mContext, PlayerService.class);
            mContext.startService(intent);
            /*//到了最后一首歌
            if(MyApplication.nowPosition == 0){
                Toast.makeText(MainActivity.this, "到了第一首歌 "+MyApplication.allLocalmp3list.get(
                        MyApplication.nowPosition
                ).getTitle(), Toast.LENGTH_SHORT).show();
            }*/
        }
        else{
            //当前播放位置为空------------------------------
            if(MyApplication.nowUrlPosition <0){
                return;
            }
            //如果点击下来的瞬间---当前位置已经是第一个了，那么就将当前位置设置为末尾+1
            if(MyApplication.nowUrlPosition == 0){
                switch (MyApplication.isWho){
                    case "kw":
                        MyApplication.nowUrlPosition = MyApplication.allUrlmp3list_kw.get(0).getAbslist().length;
                        break;
                    case "kg":
                        MyApplication.nowUrlPosition = MyApplication.allUrlmp3list_kg.get(0).getData()[0].getLists().length;
                        break;
                }
            }
            //当前播放位置-1
            MyApplication.nowUrlPosition --;
            //设置当前播放的音乐实体
            switch(MyApplication.isWho){
                case "kw":
                    MyApplication.nowUrlMp3Info_kw = MyApplication.allUrlmp3list_kw.get(0).getAbslist()[MyApplication.nowUrlPosition];
                    break;
                case "kg":
                    MyApplication.nowUrlMp3Info_kg = MyApplication.allUrlmp3list_kg.get(0).getData()[0].getLists()[MyApplication.nowUrlPosition];
                    break;
                default:
                    break;
            }
            //通知Service播放音乐
            Intent intent = new Intent();
            intent.putExtra("PLAYFLAG","PREV");
            intent.putExtra("WHO","URL");
            intent.setClass(mContext, PlayerService.class);
            mContext.startService(intent);
        }
    }

    /**
     * 具有返回值---返回值的含义：
     * true--->2play
     * false--->2pause
     */
    public boolean playOrPause(){
        Log.e(TAG, "hhh playOrPause: "+MyApplication.isLocal );
        Log.e(TAG, "hhh playOrPause: "+MyApplication.isStoping_url );
        Log.e(TAG, "hhh playOrPause: "+MyApplication.isPlaying_url );
        boolean result = true;
        if(MyApplication.isLocal){
            if(!MyApplication.isStoping_local){
                //暂停--->播放
                if(!MyApplication.isPlaying_local){
                    //将图片改为播放

                    result = true;
                    MyApplication.isPlaying_local = true;
                    //开启Service
                    Intent intent = new Intent();
                    intent.putExtra("PLAYFLAG","PAUSE2PLAY");
                    intent.putExtra("WHO","LOCAL");
                    intent.setClass(mContext, PlayerService.class);
                    mContext.startService(intent);
                }
                //播放--->暂停
                else{
                    //将图片改为暂停
                    result = false;
                    MyApplication.isPlaying_local = false;
                    //开启Service
                    Intent intent = new Intent();
                    intent.putExtra("PLAYFLAG","PLAY2PAUSE");
                    intent.putExtra("WHO","LOCAL");
                    intent.setClass(mContext, PlayerService.class);
                    mContext.startService(intent);
                }
            }
        }
        else{
            if(!MyApplication.isStoping_url){
                //暂停--->播放
                if(MyApplication.isPlaying_url == false){
                    //将图片改为播放
                    result = true;
                    MyApplication.isPlaying_url = true;
                    //开启Service
                    Intent intent = new Intent();
                    intent.putExtra("PLAYFLAG","PAUSE2PLAY");
                    intent.putExtra("WHO","URL");
                    intent.setClass(mContext, PlayerService.class);
                    mContext.startService(intent);
                }
                //播放--->暂停
                else{
                    //将图片改为暂停
                    result = false;
                    MyApplication.isPlaying_url = false;
                    //开启Service
                    Intent intent = new Intent();
                    intent.putExtra("PLAYFLAG","PLAY2PAUSE");
                    intent.putExtra("WHO","URL");
                    intent.setClass(mContext, PlayerService.class);
                    mContext.startService(intent);
                }
            }
        }
        return result;
    }
    public void next(){
        if(MyApplication.isLocal){
            //alllist为空
            if(MyApplication.allLocalmp3list== null){
                return;
            }
            //当前播放位置为空
            if(MyApplication.nowPosition == -1){
                return;
            }
            //如果点击下来的瞬间---当前位置已经是末尾了，那么就将当前位置设置顶部-1
            if(MyApplication.nowPosition == MyApplication.allLocalmp3list.size()-1){
                MyApplication.nowPosition = -1;
            }
            //当前播放位置+1
            MyApplication.nowPosition ++;
            //设置当前播放的音乐实体
            MyApplication.nowMp3Info = MyApplication.allLocalmp3list.get(MyApplication.nowPosition);
            //通知Service播放音乐
            Intent intent = new Intent();
            intent.putExtra("PLAYFLAG","NEXT");
            intent.putExtra("WHO","LOCAL");
            intent.setClass(mContext, PlayerService.class);
            mContext.startService(intent);
            //到了最后一首歌
            if(MyApplication.nowPosition == MyApplication.allLocalmp3list.size()-1){
                /*Toast.makeText(MainActivity.this, "到了最后一首歌 "+MyApplication.allLocalmp3list.get(
                        MyApplication.nowPosition
                ).getTitle(), Toast.LENGTH_SHORT).show();*/
            }
        }
        else{
            //当前播放位置为空------------------------------
            if(MyApplication.nowUrlPosition < 0){
                return;
            }
            //如果点击下来的瞬间---当前位置已经是第一个了，那么就将当前位置设置为末尾+1
            switch (MyApplication.isWho){
                case "kw":
                    if(MyApplication.nowUrlPosition == MyApplication.allUrlmp3list_kw.get(0).getAbslist().length-1){
                        MyApplication.nowUrlPosition = -1;
                    }
                    break;
                case "kg":
                    if(MyApplication.nowUrlPosition == MyApplication.allUrlmp3list_kg.get(0).getData()[0].getLists().length-1){
                        MyApplication.nowUrlPosition = -1;
                    }
                    break;
                default:
                    break;
            }

            //当前播放位置-1
            MyApplication.nowUrlPosition ++;
            //设置当前播放的音乐实体
            switch (MyApplication.isWho){
                case "kw":
                    MyApplication.nowUrlMp3Info_kw = MyApplication.allUrlmp3list_kw.get(0).getAbslist()[MyApplication.nowUrlPosition];
                    break;
                case "kg":
                    MyApplication.nowUrlMp3Info_kg = MyApplication.allUrlmp3list_kg.get(0).getData()[0].getLists()[MyApplication.nowUrlPosition];
                    break;
                default:
                    break;
            }
            //通知Service播放音乐
            Intent intent = new Intent();
            intent.putExtra("PLAYFLAG","NEXT");
            intent.putExtra("WHO","URL");
            intent.setClass(mContext, PlayerService.class);
            mContext.startService(intent);
        }
    }
}
