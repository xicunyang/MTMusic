package www.mutou.com.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import www.mutou.com.application.MyApplication;
import www.mutou.com.model.KuGouInfo;
import www.mutou.com.model.KuWoInfo;
import www.mutou.com.model.Mp3Info;
import www.mutou.com.mtmusic.R;
import www.mutou.com.service.PlayerService;
import www.mutou.com.url.UrlMain;

/**
 * Created by 木头 on 2018/6/28.
 */

public class UrlItemClick {
    List<KuWoInfo> kuwo_list;
    List<KuGouInfo>kugou_list;
    ListView url_listview;
    int position;
    Context mContext;


    public UrlItemClick(
            List<KuWoInfo> kuwo_list,
            List<KuGouInfo>kugou_list,
            ListView url_listview,
            int position,
            Context mContext){
        this.kuwo_list = kuwo_list;
        this.kugou_list = kugou_list;
        this.url_listview = url_listview;
        this.position = position;
        this.mContext = mContext;
    }
    public void urlItemClick(){
        //设置isLocal为false---证明现在就是isUrl，要清空其变量的值吗？我觉得可以不清空
        MyApplication.isLocal = false;
        MyApplication.nowPosition = -1;
        MyApplication.STOPING = false;
        MyApplication.isPlaying_local = false;

        //1.获取到position---使用position进行获取到此条目

        View v = url_listview.getChildAt(position-url_listview.getFirstVisiblePosition());
        TextView tv_who = (TextView) v.findViewById(R.id.url_detail_who);
        switch (tv_who.getText().toString()){
            case "kw":
                MyApplication.isWho = "kw";
                if(kuwo_list==null){
                    kuwo_list = MyApplication.allUrlmp3list_kw;
                }
                MyApplication.nowUrlMp3Info_kw = kuwo_list.get(0).getAbslist()[position];
                break;
            case "kg":
                MyApplication.isWho = "kg";
                if(kugou_list == null){
                    kugou_list = MyApplication.allUrlmp3list_kg;
                }
                MyApplication.nowUrlMp3Info_kg = kugou_list.get(0).getData()[0].getLists()[position];
                break;
            default:
                break;
        }

        //先更新位置
        MyApplication.oldUrlPosition = MyApplication.nowUrlPosition;
        MyApplication.nowUrlPosition = position;

        linkToService(position);
    }

    private synchronized void linkToService(int position){
        Intent intent = new Intent();
        //先将停止的flag去掉---第一次播放STOP2PLAY
        //这里的isstoping会有冲突---第一首歌播放完之后就将此改为false了
        if(MyApplication.isStoping_url){
            MyApplication.isStoping_url = false;
            MyApplication.isStoping_local = true;
            intent.putExtra("PLAYFLAG","STOP2PLAY");
            intent.putExtra("WHO","URL");
            intent.setClass(mContext, PlayerService.class);
            mContext.startService(intent);
        }
        //如果两次点击的是同一条---就暂停PLAY2PAUSE
        else if(position == MyApplication.oldUrlPosition){
            if(MyApplication.isPlaying_url){
                intent.putExtra("PLAYFLAG","PLAY2PAUSE");
                intent.putExtra("WHO","URL");
                intent.setClass(mContext, PlayerService.class);
                mContext.startService(intent);
            }
            else{
                intent.putExtra("PLAYFLAG","PAUSE2PLAY");
                intent.putExtra("WHO","URL");
                intent.setClass(mContext, PlayerService.class);
                mContext.startService(intent);
            }
        }
        else if(MyApplication.oldUrlPosition!=-1 && position != MyApplication.oldUrlPosition){
            intent.putExtra("PLAYFLAG","PLAYNEW");
            intent.putExtra("WHO","URL");
            intent.setClass(mContext, PlayerService.class);
            mContext.startService(intent);
        }
        //改变点的状态---切换位置
        changePlayingFlag(position);
    }
    private void changePlayingFlag(int position) {
        int start = url_listview.getFirstVisiblePosition();
        int end = url_listview.getLastVisiblePosition();
        int oldPosition = MyApplication.oldUrlPosition;
        int newPosition = MyApplication.nowUrlPosition;

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
                setItemFlagChange(oldPosition-start,View.GONE);
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
        View v = url_listview.getChildAt(position);
        ImageView iv = (ImageView) v.findViewById(R.id.url_detail_playing_flag);
        iv.setVisibility(visible);
    }
}
