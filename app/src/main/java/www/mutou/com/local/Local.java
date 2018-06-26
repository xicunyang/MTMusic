package www.mutou.com.local;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import www.mutou.com.adapter.AdapterLocalListView;
import www.mutou.com.application.MyApplication;
import www.mutou.com.model.Mp3Info;
import www.mutou.com.mtmusic.R;
import www.mutou.com.service.PlayerService;
import www.mutou.com.utils.AudioUtils;

public class Local extends Fragment implements AdapterView.OnItemClickListener{
    private ListView listView_localMain;
    private static final String TAG = "Local";
    private List<Mp3Info> mp3Infos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_local,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAllMp3Files();
    }
    //获取到本地所有MP3文件
    private void getAllMp3Files(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Mp3Info> mp3Infos = AudioUtils.getAllSongs(getActivity());
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putSerializable("mp3infos", (Serializable) mp3Infos);
                message.setData(bundle);
                handler_getMp3Files.sendMessage(message);
            }
        }).start();
    }

    Handler handler_getMp3Files = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            mp3Infos = (List<Mp3Info>) bundle.getSerializable("mp3infos");
            //Log.d(TAG, "handleMessage: yxc--->"+mp3Infos.size());
            //成功获取到所有的MP3文件---存入了List中
            //然后就是将list数据存入listView上
            /*Log.d(TAG, "handleMessage: yxc"
                    +mp3Infos.get(1).getId()+"---"
                    +mp3Infos.get(0).getAlbum()+"---"
                    +mp3Infos.get(0).getFileName()+"---"
                    +mp3Infos.get(0).getFileUrl()+"---"
                    +mp3Infos.get(0).getSinger()+"---"
                    +mp3Infos.get(0).getSize()+"---"
                    +mp3Infos.get(0).getTitle()+"---"
                    +mp3Infos.get(0).getType()+"---"
                    +mp3Infos.get(0).getYear()+"---"
                    +mp3Infos.get(0).getDuration()+"---"
            );*/
            MyApplication.allLocalmp3list = mp3Infos;
            putDataToListView(mp3Infos);
        }
    };

    //将list数据存入listView上
    private void putDataToListView(List<Mp3Info> mp3Infos) {
        listView_localMain = (ListView) getActivity().findViewById(R.id.local_listview);
        AdapterLocalListView adapterLocalListView = new AdapterLocalListView(getActivity(),mp3Infos);
        listView_localMain.setAdapter(adapterLocalListView);

        //给listView设置点击事件
        listView_localMain.setOnItemClickListener(this);

        if(MyApplication.nowPosition>5){
            listView_localMain.setSelection(MyApplication.nowPosition-5);
        }
    }

    //item点击事件&&播放flag图片的实现
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //*******在这里进行设置----点击了就算是现在是local了
        MyApplication.STOPING = false;
        MyApplication.isLocal = true;
        MyApplication.nowUrlPosition = -1;

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
            intent.setClass(getActivity(), PlayerService.class);
            getActivity().startService(intent);
        }
        //如果两次点击的是同一条---就暂停PLAY2PAUSE
        else if(position == MyApplication.oldPosition){
            if(MyApplication.isPlaying_local){
                intent.putExtra("PLAYFLAG","PLAY2PAUSE");
                intent.putExtra("WHO","LOCAL");
                intent.setClass(getActivity(), PlayerService.class);
                getActivity().startService(intent);
            }
            else{
                intent.putExtra("PLAYFLAG","PAUSE2PLAY");
                intent.putExtra("WHO","LOCAL");
                intent.setClass(getActivity(), PlayerService.class);
                getActivity().startService(intent);
            }
        }
        else if(MyApplication.oldPosition!=-1 && position != MyApplication.oldPosition){
            intent.putExtra("PLAYFLAG","PLAYNEW");
            intent.putExtra("WHO","LOCAL");
            intent.setClass(getActivity(), PlayerService.class);
            getActivity().startService(intent);
        }

        //改变点的状态---切换位置
        changePlayingFlag(position);

    }

    private void changePlayingFlag(int position) {
        int start = listView_localMain.getFirstVisiblePosition();
        int end = listView_localMain.getLastVisiblePosition();
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
        View v = listView_localMain.getChildAt(position);
        ImageView iv = (ImageView) v.findViewById(R.id.local_detail_playing_flag);
        iv.setVisibility(visible);
    }
}
