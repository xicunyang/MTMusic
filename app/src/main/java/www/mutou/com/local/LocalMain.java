package www.mutou.com.local;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import www.mutou.com.adapter.AdapterLocalListView;
import www.mutou.com.model.Mp3Info;
import www.mutou.com.mtmusic.R;
import www.mutou.com.utils.AudioUtils;

public class LocalMain extends SwipeBackActivity{

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
           /* Log.d(TAG, "handleMessage: yxc"
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
}
