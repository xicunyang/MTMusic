package www.mutou.com.local;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.Visibility;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import www.mutou.com.adapter.AdapterLocalListView;
import www.mutou.com.application.MyApplication;
import www.mutou.com.model.Mp3Info;
import www.mutou.com.mtmusic.MainActivity;
import www.mutou.com.mtmusic.R;
import www.mutou.com.service.PlayerService;
import www.mutou.com.utils.AudioUtils;

public class LocalMain extends SwipeBackActivity implements AdapterView.OnItemClickListener{

    private ListView listView_localMain;
    private static final String TAG = "LocalMain";
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 可以调用该方法，设置是否允许滑动退出
        setSwipeBackEnable(true);
        mSwipeBackLayout = getSwipeBackLayout();
        // 设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        // 滑动退出的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法
        //mSwipeBackLayout.setEdgeSize(200);

        //淡入效果，很不错
        Transition explode = TransitionInflater.from(this).inflateTransition(android.R.transition.fade);
        getWindow().setEnterTransition(explode);
        setContentView(R.layout.activity_local_main);

        //设置状态栏的返回箭头显示
        setToolBar();

        //获取到本地所有MP3文件
        getAllMp3Files();
    }
    //获取到本地所有MP3文件
    private void getAllMp3Files(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Mp3Info> mp3Infos = AudioUtils.getAllSongs(LocalMain.this);
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
            List<Mp3Info> mp3Infos = (List<Mp3Info>) bundle.getSerializable("mp3infos");
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
            putDataToListView(mp3Infos);
        }
    };

    //将list数据存入listView上
    private void putDataToListView(List<Mp3Info> mp3Infos) {
        listView_localMain = (ListView) findViewById(R.id.local_listview);
        AdapterLocalListView adapterLocalListView = new AdapterLocalListView(LocalMain.this,mp3Infos);
        listView_localMain.setAdapter(adapterLocalListView);

        //给listView设置点击事件
        listView_localMain.setOnItemClickListener(this);

        if(MyApplication.nowPosition>5){
            listView_localMain.setSelection(MyApplication.nowPosition-5);
        }
    }

    //显示ToolBar的返回按钮
    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_localMain);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    //Toolbar的事件---返回
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    //item点击事件&&播放flag图片的实现
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //先更新位置
        MyApplication.oldPosition = MyApplication.nowPosition;
        MyApplication.nowPosition = position;



        Intent intent = new Intent();
        //先将停止的flag去掉---第一次播放STOP2PLAY
        if(MyApplication.isStoping){
            MyApplication.isStoping = false;

            intent.putExtra("PLAYFLAG","STOP2PLAY");
            intent.setClass(LocalMain.this, PlayerService.class);
            startService(intent);
        }
        //如果两次点击的是同一条---就暂停PLAY2PAUSE
        else if(position == MyApplication.oldPosition){
            if(MyApplication.isPlaying){
                intent.putExtra("PLAYFLAG","PLAY2PAUSE");
                intent.setClass(LocalMain.this, PlayerService.class);
                startService(intent);
            }
            else{
                intent.putExtra("PLAYFLAG","PAUSE2PLAY");
                intent.setClass(LocalMain.this, PlayerService.class);
                startService(intent);
            }

        }
        else if(MyApplication.oldPosition!=-1 && position != MyApplication.oldPosition){
            intent.putExtra("PLAYFLAG","STOP2PLAY");
            intent.setClass(LocalMain.this, PlayerService.class);
            startService(intent);
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
