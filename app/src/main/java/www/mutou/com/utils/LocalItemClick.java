package www.mutou.com.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

import www.mutou.com.application.MyApplication;
import www.mutou.com.model.Mp3Info;
import www.mutou.com.mtmusic.R;
import www.mutou.com.service.PlayerService;

/**
 * Created by 木头 on 2018/6/28.
 */

public class LocalItemClick {
    List<Mp3Info> mp3Infos;
    int position;
    Context context;
    ListView v;

    public LocalItemClick(List<Mp3Info> mp3Infos,int position,Context context,ListView v){
        this.mp3Infos = mp3Infos;
        this.position = position;
        this.context = context;
        this.v = v;
    }
    public void localItemClick(){
        //*******在这里进行设置----点击了就算是现在是local了
        MyApplication.STOPING = false;
        MyApplication.isLocal = true;
        MyApplication.nowUrlPosition = -1;
        MyApplication.isPlaying_url = false;

        //获取到当前点击的item对应的MP3实体
        MyApplication.nowMp3Info = mp3Infos.get(position);

        //先更新位置
        MyApplication.oldPosition = MyApplication.nowPosition;
        MyApplication.nowPosition = position;


        Intent intent = new Intent();
        //先将停止的flag去掉---第一次播放STOP2PLAY
        if(MyApplication.isStoping_local){
            MyApplication.isStoping_local = false;
            MyApplication.isStoping_url = true;
            intent.putExtra("PLAYFLAG","STOP2PLAY");
            intent.putExtra("WHO","LOCAL");
            intent.setClass(context, PlayerService.class);
            context.startService(intent);
        }
        //如果两次点击的是同一条---就暂停PLAY2PAUSE
        else if(position == MyApplication.oldPosition){
            if(MyApplication.isPlaying_local){
                intent.putExtra("PLAYFLAG","PLAY2PAUSE");
                intent.putExtra("WHO","LOCAL");
                intent.setClass(context, PlayerService.class);
                context.startService(intent);
            }
            else{
                intent.putExtra("PLAYFLAG","PAUSE2PLAY");
                intent.putExtra("WHO","LOCAL");
                intent.setClass(context, PlayerService.class);
                context.startService(intent);
            }
        }
        else if(MyApplication.oldPosition!=-1 && position != MyApplication.oldPosition){
            intent.putExtra("PLAYFLAG","PLAYNEW");
            intent.putExtra("WHO","LOCAL");
            intent.setClass(context, PlayerService.class);
            context.startService(intent);
        }

        //改变点的状态---切换位置
        changePlayingFlag(position);
    }
    private void changePlayingFlag(int position) {
        int start = v.getFirstVisiblePosition();
        int end = v.getLastVisiblePosition();
        int oldPosition = MyApplication.oldPosition;
        int newPosition = MyApplication.nowPosition;

        if(oldPosition!=-1){
            //说明不是第一次点击items
            //先清除原来的
            //此时---oldPosition-start<count有两种情况
            //情况1---正常
            //情况2---多滑一点点，count会多1
            if(oldPosition>=start && oldPosition<=end){
                //说明在显示区域内---不在显示区域内的话就不管了
                //先去掉原来的
                //怎样获取到位置呢？
                //说明是第一次点击items
                //oldPosition-start来定位当前视图内的位置
                setItemFlagChange(oldPosition-start, View.GONE);
                //设置新的
                //newPosition-start来定位新按下的在视图内的位置
                setItemFlagChange(newPosition-start,View.VISIBLE);
            }
            else{
                //设置新的
                //newPosition-start来定位新按下的在视图内的位置
                setItemFlagChange(newPosition-start,View.VISIBLE);
            }

        }
        else{
            //说明是第一次点击items
            //position-start来定位当前摁下的在可见视图内的位置
            setItemFlagChange(position-start,View.VISIBLE);
        }
    }

    public synchronized void setItemFlagChange(int position,int visible){
        View v1 = v.getChildAt(position);
        ImageView iv = (ImageView) v1.findViewById(R.id.local_detail_playing_flag);
        iv.setVisibility(visible);
    }
}
