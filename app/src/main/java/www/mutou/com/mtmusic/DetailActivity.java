package www.mutou.com.mtmusic;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import www.mutou.com.adapter.AdapterLocalListView;
import www.mutou.com.adapter.AdapterUrlListView_Kugou;
import www.mutou.com.adapter.AdapterUrlListView_Kuwo;
import www.mutou.com.application.MyApplication;
import www.mutou.com.model.KuGouInfo;
import www.mutou.com.model.KuWoInfo;
import www.mutou.com.model.Mp3Info;
import www.mutou.com.service.PlayerService;
import www.mutou.com.utils.LocalItemClick;
import www.mutou.com.utils.PlayerControls;
import www.mutou.com.utils.UrlItemClick;

public class DetailActivity extends SwipeBackActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private SwipeBackLayout mSwipeBackLayout;
    private SeekBar seekBar;
    private Button btn_startService;
    private static final String TAG = "DetailActivity";
    private ServiceConnection serviceConnection;
    private PlayerService.MyBind myBind;
    boolean isTouch = false;
    private Timer timer;
    private TimerTask timerTask;
    private ImageView iv_playOrPause;
    private ImageView iv_yaobi;
    private CircleImageView changpian;
    private ObjectAnimator changpian_rotate;
    private PlayerControls playerControls;
    private ImageView iv_prev;
    private ImageView iv_next;
    private TextView tv_songName;
    private TextView tv_singer;
    private TextView tv_alum;
    private ImageView iv_list;
    private Dialog dialog;
    private ImageView iv_changePlayState;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSwipeBack();
        setContentView(R.layout.activity_detail);

        playerControls = new PlayerControls(this);
        initViews();
        initEvents();
        initService();
        initChangpianRotate();
        initBroadCast();
        putDataToPage();
        setToolBar();
        //让唱片旋转起来
        if(MyApplication.isPlaying_local||MyApplication.isPlaying_url){
            changPianRotate_Start();
        }
    }

    private void initSwipeBack(){
        // 可以调用该方法，设置是否允许滑动退出
        setSwipeBackEnable(true);
        mSwipeBackLayout = getSwipeBackLayout();
        // 设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        // 滑动退出的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法
        mSwipeBackLayout.setEdgeSize(50);
    }

    //将数据填入页面
    private void putDataToPage(){
        String songName = "";
        String singer = "";
        String album = "";
        if(MyApplication.isLocal){
            //本地
            Mp3Info mp3Info = MyApplication.nowMp3Info;
            songName = mp3Info.getTitle();
            singer = mp3Info.getSinger();
            album = mp3Info.getAlbum();
        }
        else{
            //网络
            switch (MyApplication.isWho){
                case "kw":
                    KuWoInfo.abslist kuwoinfo = MyApplication.nowUrlMp3Info_kw;
                    songName = kuwoinfo.getSONGNAME();
                    singer = kuwoinfo.getARTIST();
                    album = kuwoinfo.getALBUM();
                    break;

                case "kg":
                    KuGouInfo.data.lists kugouinfo = MyApplication.nowUrlMp3Info_kg;
                    songName = kugouinfo.getSongName();
                    singer = kugouinfo.getSingerName();
                    album = kugouinfo.getAlbumName();
                    break;
            }
        }
        tv_songName.setText(songName);
        tv_singer.setText(singer);
        tv_alum.setText("·"+album);

        //判断是不是正在播放---设置播放/暂停按钮
        if(MyApplication.isPlaying_local||MyApplication.isPlaying_url){
            iv_playOrPause.setImageResource(R.drawable.detail_playing);
        }
        else{
            iv_playOrPause.setImageResource(R.drawable.detail_pausing);
        }
    }

    //初始化控件
    private void initViews() {
        seekBar = (SeekBar) findViewById(R.id.detail_seekbar);
        btn_startService = (Button) findViewById(R.id.openService);
        iv_playOrPause = (ImageView) findViewById(R.id.detail_playOrPause);
        iv_yaobi = (ImageView) findViewById(R.id.detail_yaobi);
        changpian = (CircleImageView) findViewById(R.id.detail_centerImage);
        iv_prev = (ImageView) findViewById(R.id.detail_prev);
        iv_next = (ImageView) findViewById(R.id.detail_next);
        tv_songName = (TextView) findViewById(R.id.detail_songName);
        tv_singer = (TextView) findViewById(R.id.detail_singer);
        tv_alum = (TextView) findViewById(R.id.detail_alum);
        iv_list = (ImageView) findViewById(R.id.detail_list);
        iv_changePlayState = (ImageView) findViewById(R.id.detail_changePlayState);
    }

    //初始化事件
    private void initEvents() {
        iv_playOrPause.setOnClickListener(this);
        iv_prev.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_yaobi.setOnClickListener(this);
        iv_list.setOnClickListener(this);
        iv_changePlayState.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detail_playOrPause:
                //暂停  or  播放
                if(playerControls.playOrPause() == true){
                    //--->2play
                    iv_playOrPause.setImageResource(R.drawable.detail_playing);
                    changPianRotate_Pause2Play();
                }
                else{
                    //--->2pause
                    iv_playOrPause.setImageResource(R.drawable.detail_pausing);
                    changPianRotate_Pause();
                }
                break;
            case R.id.detail_prev:
                //上一曲
                myClickAnimation(iv_prev);
                changPianRotate_Pause();
                prev();
                break;

            case R.id.detail_next:
                //下一曲
                myClickAnimation(iv_next);
                changPianRotate_Pause();
                next();
                break;
            case R.id.detail_yaobi:
                //暂停  or  播放
                if(playerControls.playOrPause() == true){
                    //--->2play
                    iv_playOrPause.setImageResource(R.drawable.detail_playing);
                    changPianRotate_Pause2Play();
                }
                else{
                    //--->2pause
                    iv_playOrPause.setImageResource(R.drawable.detail_pausing);
                    changPianRotate_Pause();
                }
                break;
            case R.id.detail_list:
                //呼出底部菜单
                setDialog();
                break;
            case R.id.detail_changePlayState:
                //呼出底部菜单
                changePlayState();
                break;

            default:
                break;
        }
    }
    int playState = 0;
    //更换播放的状态---单曲循环--列表循环等等
    private void changePlayState() {
        playState++;
        switch (playState){
            case 1:
                iv_changePlayState.setImageResource(R.drawable.detail_danqu);
                Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                iv_changePlayState.setImageResource(R.drawable.detail_xunhuan);
                Toast.makeText(this, "列表循环", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                iv_changePlayState.setImageResource(R.drawable.detail_suiji);
                Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                playState = 0;
                break;
            default:
                break;
        }
    }

    //自定义点击动画(缩小加淡入淡出)
    private void myClickAnimation(View view){
        ObjectAnimator animator_scaleX = ObjectAnimator.ofFloat(view,"scaleX",1f,0.5f,1f);
        ObjectAnimator animator_scaleY = ObjectAnimator.ofFloat(view,"scaleY",1f,0.5f,1f);
        ObjectAnimator animator_alpha = ObjectAnimator.ofFloat(view,"alpha",1f,0.2f,1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(400);
        set.play(animator_alpha).with(animator_scaleX).with(animator_scaleY);
        set.start();

    }

    //上一曲
    private void prev(){
        closeTimer();
        playerControls.prev();
        seekBar.setProgress(0);
    }

    //下一曲
    private void next(){
        closeTimer();
        playerControls.next();
        seekBar.setProgress(0);
    }

    //唱片动画初始化
    private  void initChangpianRotate(){
        changpian_rotate = ObjectAnimator.ofFloat(changpian,"rotation",0,360f);
        //重复次数
        changpian_rotate.setRepeatCount(ValueAnimator.INFINITE);
        //执行时间
        changpian_rotate.setDuration(8000);
        //设置动画匀速运动
        LinearInterpolator lin = new LinearInterpolator();
        changpian_rotate.setInterpolator(lin);
        //结束后的状态
        changpian_rotate.setRepeatMode(ValueAnimator.RESTART);
    }

    //唱片旋转--开始
    private void changPianRotate_Start(){
        //先让摇臂下来
        Animation yaobi_down = AnimationUtils.loadAnimation(this,R.anim.detail_yaobi_down);
        iv_yaobi.startAnimation(yaobi_down);

        //等待700ms然后唱片开始动
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                changpian_rotate.start();
            }
        },600);
    }
    //唱片旋转--暂停继续
    private void changPianRotate_Pause2Play(){
        //先让摇臂下来
        Animation yaobi_down = AnimationUtils.loadAnimation(this,R.anim.detail_yaobi_down);
        iv_yaobi.startAnimation(yaobi_down);
        if(changpian_rotate.isStarted()){

            changpian_rotate.resume();
        }else{

            changpian_rotate.start();
        }
    }
    //唱片旋转--暂停
    private void changPianRotate_Pause(){
        //先让摇臂升起
        Animation yaobi_up = AnimationUtils.loadAnimation(this,R.anim.detail_yaobi_up);
        iv_yaobi.startAnimation(yaobi_up);
        changpian_rotate.pause();
    }
    //连接Service
    private void initService() {
        Intent intent = new Intent();
        intent.setClass(DetailActivity.this, PlayerService.class);
        intent.putExtra("WHO","DETAIL");
        startService(intent);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myBind = (PlayerService.MyBind) service;
                //设置进度条的最大长度
                int max = myBind.getDuration();
                seekBar.setMax(max);
                Log.e(TAG, "onServiceConnected: max"+max);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(isTouch){
                            myBind.setSeekTo(progress);
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        isTouch = true;
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        isTouch = false;
                    }
                });
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        int position = myBind.getCurrentPosition();
                        seekBar.setProgress(position);
                    }
                };
                timer.schedule(timerTask,0,10);
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //资源的关闭
        if(serviceConnection!=null){
            unbindService(serviceConnection);
        }
        closeTimer();
        unregisterReceiver(myBroadReceiver);
    }

    //计时关闭计时器---否则会出现继续请求为空的情况
    private void closeTimer(){
        if (timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
    //接受广播以更行UI
    private IntentFilter intentFilter;
    private MyBroadReceiver myBroadReceiver;

    private void initBroadCast(){
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.PlayServiceBroad");
        myBroadReceiver = new MyBroadReceiver();
        registerReceiver(myBroadReceiver,intentFilter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        closeTimer();
        seekBar.setProgress(0);
        if(MyApplication.isLocal){
            LocalItemClick localItemClick = new LocalItemClick(
                    MyApplication.allLocalmp3list,position,this,listView
            );
            localItemClick.localItemClick();
        }
        else{
            UrlItemClick urlItemClick = new UrlItemClick(
                    MyApplication.allUrlmp3list_kw,
                    MyApplication.allUrlmp3list_kg,
                    listView,position,this);
            urlItemClick.urlItemClick();
        }
    }

    /**
     * LocalItemClick localItemClick = new LocalItemClick(mp3Infos,position,getContext(),listView_localMain);
     localItemClick.localItemClick();


     ObjectAnimator fab_translationY = ObjectAnimator.ofFloat(fab,"translationY",100f,-20f,0f);
     fab_translationY.setDuration(500);
     fab_translationY.start();
     fab.setVisibility(CV);
     */

    public class MyBroadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //成功接受到广播---开始进行判断
            if(intent.getStringExtra("WHAT").equals("changePlayOrPause")) {
                if(MyApplication.isPlaying_url||MyApplication.isPlaying_local){
                    putDataToPage();
                    iv_playOrPause.setImageResource(R.drawable.detail_playing);
                    changPianRotate_Pause2Play();
                }
                else {
                    iv_playOrPause.setImageResource(R.drawable.detail_pausing);
                }
            }
            else if(intent.getStringExtra("WHAT").equals("cacheOK")) {
                //再请求连接service
                initService();
            }

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

    private void setDialog(){

        dialog = new Dialog(this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.detail_bottom_list_dialog,null
        );
        listView = (ListView) root.findViewById(R.id.detail_bottom_list);
        if(MyApplication.isLocal){
            AdapterLocalListView adapterLocalListView = new AdapterLocalListView(this,MyApplication.allLocalmp3list);
            listView.setAdapter(adapterLocalListView);
            if(MyApplication.nowPosition>2){
                listView.setSelection(MyApplication.nowPosition-2);
            }
        }
        else{
            switch (MyApplication.isWho){
                case "kw":
                    AdapterUrlListView_Kuwo adapterUrlListView_kuwo = new AdapterUrlListView_Kuwo(this,MyApplication.allUrlmp3list_kw);
                    listView.setAdapter(adapterUrlListView_kuwo);
                    break;
                case "kg":
                    AdapterUrlListView_Kugou adapterUrlListView_kugou = new AdapterUrlListView_Kugou(this,MyApplication.allUrlmp3list_kg);
                    listView.setAdapter(adapterUrlListView_kugou);
                    break;
                default:
                    break;
            }
            if(MyApplication.nowUrlPosition>2){
                listView.setSelection(MyApplication.nowUrlPosition-2);
            }
        }
        listView.setOnItemClickListener(DetailActivity.this);
        dialog.setContentView(root);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;

        lp.width = getResources().getDisplayMetrics().widthPixels;
        root.measure(0,0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }
}






















