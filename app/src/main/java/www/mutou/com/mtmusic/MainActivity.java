package www.mutou.com.mtmusic;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tandong.swichlayout.SwitchLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import www.mutou.com.application.MyApplication;
import www.mutou.com.local.LocalMain;
import www.mutou.com.model.Mp3Info;
import www.mutou.com.service.PlayerService;
import www.mutou.com.url.UrlMain;
import www.mutou.com.utils.DensityUtil;

public class MainActivity extends AppCompatActivity{

    private CircleImageView localView;
    private CircleImageView urlView;
    private FloatingActionButton main_fab;
    private boolean fab_ckeched = false;
    private FloatingActionButton prev;
    private FloatingActionButton playOrPause;
    private FloatingActionButton next;
    private LinearLayout fab_items;
    private static final String TAG = "MainActivity";
    private LinearLayout ll_item;
    private TextView tv_item_title;
    private TextView tv_item_singerAlum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //淡入效果，很不错
        Transition explode = TransitionInflater.from(this).inflateTransition(android.R.transition.fade);
        getWindow().setEnterTransition(explode);
        setContentView(R.layout.activity_main);

        //找到控件
        initViews();

        //权限请求
        getPermission();

        //显示侧边栏三个横线图标
        getActionBarImage();


    }

    //设置两个选项的点击事件
    private void addEvent() {
        localView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,LocalMain.class);
                startActivityForResult(intent, 0, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
        urlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TwoAnimation(2);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,UrlMain.class);
                startActivityForResult(intent, 0, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
    }

    private void TwoAnimation(int flag){
       //还是抖动一下比较好---因为用户还会返回回来
        Object thisObject = null;
        if(flag==1){
            thisObject = localView;
        }
        else if(flag == 2){
            thisObject = urlView;
        }
        ObjectAnimator animator_scaleX = ObjectAnimator.ofFloat(thisObject,"scaleX",1f,0.7f,1f);
        ObjectAnimator animator_scaleY = ObjectAnimator.ofFloat(thisObject,"scaleY",1f,0.7f,1f);
        AnimatorSet set = new AnimatorSet();
        set.play(animator_scaleX).with(animator_scaleY);
        set.setDuration(400);
        set.start();
    }

    //找到控件
    private void initViews() {
        localView = (CircleImageView) findViewById(R.id.circleView_local);
        urlView = (CircleImageView) findViewById(R.id.circleView_url);
        main_fab = (FloatingActionButton) findViewById(R.id.main_fab);

        //按钮组
        fab_items = (LinearLayout) findViewById(R.id.fab_items);
        prev = (FloatingActionButton) findViewById(R.id.prev);
        playOrPause = (FloatingActionButton) findViewById(R.id.playOrPause);
        next = (FloatingActionButton) findViewById(R.id.next);

        //主悬浮按钮左边的item
        ll_item = (LinearLayout) findViewById(R.id.ll_nowPlayItem);
        tv_item_title = (TextView) findViewById(R.id.main_item_title);
        tv_item_singerAlum = (TextView) findViewById(R.id.main_item_singerAlum);

        main_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果按钮没被点击
                if(!fab_ckeched){
                    showFabItems();
                    fab_ckeched = true;
                    //设置动画  item显示
                    if(MyApplication.nowMp3Info!=null){
                        ObjectAnimator item_alpha = ObjectAnimator.ofFloat(ll_item,"alpha",0f,0f,1f);
                        ObjectAnimator item_translationX = ObjectAnimator.ofFloat(ll_item,"translationX",300f,0f);
                        AnimatorSet set = new AnimatorSet();
                        set.play(item_alpha).with(item_translationX);
                        set.setDuration(500);
                        set.start();
                        ll_item.setVisibility(View.VISIBLE);
                    }
                }
                //如果按钮以被点击
                else{
                    closeFabItems();
                    //设置动画  item退出
                    if(MyApplication.nowMp3Info!=null) {
                        ObjectAnimator item_alpha = ObjectAnimator.ofFloat(ll_item, "alpha", 1f, 0f, 0f);
                        ObjectAnimator item_translationX = ObjectAnimator.ofFloat(ll_item, "translationX", 0f, 300f);
                        AnimatorSet set = new AnimatorSet();
                        set.play(item_alpha).with(item_translationX);
                        set.setDuration(500);
                        set.start();
                    }
                    //rl_item.setVisibility(View.GONE);
                    fab_ckeched = false;
                }
            }
        });

        main_fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "长摁成功......", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //上一首
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                intent.setClass(MainActivity.this, PlayerService.class);
                startService(intent);

                Toast.makeText(MainActivity.this, "当前歌曲 "+MyApplication.allLocalmp3list.get(
                        MyApplication.nowPosition
                ).getTitle(), Toast.LENGTH_SHORT).show();
                //到了最后一首歌
                if(MyApplication.nowPosition == 0){
                    Toast.makeText(MainActivity.this, "到了第一首歌 "+MyApplication.allLocalmp3list.get(
                            MyApplication.nowPosition
                    ).getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //下一首
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Toast.makeText(MainActivity.this, "当前歌曲 "+MyApplication.allLocalmp3list.get(
                        MyApplication.nowPosition
                ).getTitle(), Toast.LENGTH_SHORT).show();
                //设置当前播放的音乐实体
                MyApplication.nowMp3Info = MyApplication.allLocalmp3list.get(MyApplication.nowPosition);
                //通知Service播放音乐
                Intent intent = new Intent();
                intent.putExtra("PLAYFLAG","NEXT");
                intent.putExtra("WHO","LOCAL");
                intent.setClass(MainActivity.this, PlayerService.class);
                startService(intent);
                //到了最后一首歌
                if(MyApplication.nowPosition == MyApplication.allLocalmp3list.size()-1){
                    Toast.makeText(MainActivity.this, "到了最后一首歌 "+MyApplication.allLocalmp3list.get(
                            MyApplication.nowPosition
                    ).getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //暂停---播放切换
        playOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否是刚打开软件---停止的状态？

                /*/*//*****在正式的时候需要将此代码去掉
                MyApplication.isStoping = false; /*//*
                /*//*********************************/

                if(!MyApplication.isStoping){
                    //暂停--->播放
                    if(!MyApplication.isPlaying){
                        //将图片改为播放
                        playOrPause.setImageResource(R.drawable.playing);
                        MyApplication.isPlaying = true;

                        //开启Service
                        Intent intent = new Intent();
                        intent.putExtra("PLAYFLAG","PAUSE2PLAY");
                        intent.putExtra("WHO","LOCAL");
                        intent.setClass(MainActivity.this, PlayerService.class);
                        startService(intent);
                    }
                    //播放--->暂停
                    else{
                        //将图片改为暂停
                        playOrPause.setImageResource(R.drawable.pausing);
                        MyApplication.isPlaying = false;

                        //开启Service
                        Intent intent = new Intent();
                        intent.putExtra("PLAYFLAG","PLAY2PAUSE");
                        intent.putExtra("WHO","LOCAL");
                        intent.setClass(MainActivity.this, PlayerService.class);
                        startService(intent);
                    }
                }

            }
        });
    }

    //显示悬浮按钮组动画
    private void showFabItems(){
        //给主按钮加一个旋转
        ObjectAnimator mainFAB_rotation = ObjectAnimator.ofFloat(main_fab,"rotation",0f,360f);
        ObjectAnimator prev_alpha = ObjectAnimator.ofFloat(prev,"alpha",0f,0f,0f,1f);
        ObjectAnimator prev_translationY = ObjectAnimator.ofFloat(prev,"translationY",80f,80f,80f,0f);
        ObjectAnimator playOrPause_alpha = ObjectAnimator.ofFloat(playOrPause,"alpha",0f,0f,1f);
        ObjectAnimator playOrPause_translationY = ObjectAnimator.ofFloat(playOrPause,"translationY",80f,0f);
        ObjectAnimator next_alpha = ObjectAnimator.ofFloat(next,"alpha",0f,1f);
        AnimatorSet set = new AnimatorSet();
        set.play(prev_alpha).with(playOrPause_alpha).with(next_alpha)
                .with(prev_translationY).with(playOrPause_translationY).with(mainFAB_rotation);
        set.setDuration(500);
        set.start();
        fab_items.setVisibility(View.VISIBLE);
    }

    //隐藏悬浮按钮组动画
    private void closeFabItems(){
        //给主按钮加一个旋转
        ObjectAnimator mainFAB_rotation = ObjectAnimator.ofFloat(main_fab,"rotation",360f,0f);
        ObjectAnimator prev_alpha = ObjectAnimator.ofFloat(prev,"alpha",1f,0.0f,0f);
        ObjectAnimator prev_translationY = ObjectAnimator.ofFloat(prev,"translationY",0f,80f,80f);

        ObjectAnimator playOrPause_alpha = ObjectAnimator.ofFloat(playOrPause,"alpha",1f,0.5f,0f);
        ObjectAnimator playOrPause_translationY = ObjectAnimator.ofFloat(playOrPause,"translationY",0f,0f,0f,80f);

        ObjectAnimator next_alpha = ObjectAnimator.ofFloat(next,"alpha",1f,1f,1f,1f,0f);
        AnimatorSet set = new AnimatorSet();
        set.play(prev_alpha).with(playOrPause_alpha).with(next_alpha)
                .with(prev_translationY).with(playOrPause_translationY).with(mainFAB_rotation);
        set.setDuration(500);
        set.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab_items.setVisibility(View.GONE);
            }
        },500);

    }

    //开始进场动画
    private void inAnimation(){
        float pianyi = DensityUtil.dip2px(this,120);

        ObjectAnimator local_scaleX = ObjectAnimator.ofFloat(localView,"scaleX",0.5f,1f);
        ObjectAnimator local_scaleY = ObjectAnimator.ofFloat(localView,"scaleY",0.5f,1f);
        ObjectAnimator local_translationY = ObjectAnimator.ofFloat(localView,"translationY",0f,-150f,-300f,-pianyi);
        ObjectAnimator local_alpha = ObjectAnimator.ofFloat(localView,"alpha",0,0.3f,0.6f,1f);

        ObjectAnimator url_scaleX = ObjectAnimator.ofFloat(urlView,"scaleX",0.5f,1f);
        ObjectAnimator url_scaleY = ObjectAnimator.ofFloat(urlView,"scaleY",0.5f,1f);
        ObjectAnimator url_translationY = ObjectAnimator.ofFloat(urlView,"translationY",0f,150f,300f,pianyi);
        ObjectAnimator url_alpha = ObjectAnimator.ofFloat(urlView,"alpha",0,0.3f,0.6f,1f);


        AnimatorSet set = new AnimatorSet();
        set.setDuration(1300);
        set.play(local_scaleX).with(local_scaleY).with(local_translationY).with(local_alpha)
        .with(url_scaleX).with(url_scaleY).with(url_translationY).with(url_alpha);
        set.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator fab_translationY = ObjectAnimator.ofFloat(main_fab,"translationY",200f,150f,-50f,0f);
                ObjectAnimator fab_alpha = ObjectAnimator.ofFloat(main_fab,"alpha",0f,0f,1f);
                AnimatorSet set = new AnimatorSet();
                set.play(fab_translationY).with(fab_alpha);
                set.setDuration(700);
                set.start();
                main_fab.setVisibility(View.VISIBLE);
            }
        },1000);
    }

    //显示侧边栏三个横线图标
    private void getActionBarImage(){
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        //将标题隐藏，否则会遮挡小图标
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,drawerLayout,toolbar,R.string.open,R.string.close
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    private void getPermission(){
        //做权限列表
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                !=  PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.INTERNET);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                !=  PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=  PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //如果权限列表为空----代表全被授权---否则就回调
        if(!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
        else{
            //如果为空--说明第一次有授权了--这次就不需要授权了，就开始进行服务
            startMethods();
        }
    }
    //权限问题--检查
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //返回值的检查
        switch (requestCode){
            case 1:
                //如果List不为空说明有权限需要处理
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须全部授权才能继续使用", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    startMethods();
                }else{
                    Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    //服务开始---起点
    private void startMethods() {
        //开始进场动画
        inAnimation();

        //设置两个选项的点击事件
        addEvent();

        //设置广播---以接收广播更新UI
        initBroadCast();
    }
    //设置返回按钮：不应该退出程序---而是返回桌面
    //复写onKeyDown事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
    public class MyBroadReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "onReceive: yxc-->receive OK");
            //成功接受到广播---开始进行判断
            if(intent.getStringExtra("WHAT").equals("changePlayOrPause")){
                //开始更新页面
                //根据全局变量isPlaying的状态判断是应该显示什么
                //正在播放
                //然后添加动画---一直旋转
                if(MyApplication.MainFABRotation==null){
                    MyApplication.MainFABRotation = ObjectAnimator.ofFloat(main_fab,"rotation",0f,360f);
                    //重复次数
                    MyApplication.MainFABRotation.setRepeatCount(ValueAnimator.INFINITE);
                    //执行时间
                    MyApplication.MainFABRotation.setDuration(3000);
                    //设置动画匀速运动
                    LinearInterpolator lin = new LinearInterpolator();
                    MyApplication.MainFABRotation.setInterpolator(lin);
                    //结束后的状态
                    MyApplication.MainFABRotation.setRepeatMode(ValueAnimator.RESTART);
                }

                if(MyApplication.isPlaying){
                    //设置成playing的图片
                    playOrPause.setImageResource(R.drawable.playing);
                    MyApplication.MainFABRotation.start();
                }
                else{
                    playOrPause.setImageResource(R.drawable.pausing);
                    //然后添加动画---停止旋转
                    if(MyApplication.MainFABRotation.isRunning()){
                        MyApplication.MainFABRotation.pause();
                    }
                }

                //更新歌曲信息到item中
                Mp3Info mp3Info = MyApplication.nowMp3Info;
                tv_item_title.setText(mp3Info.getTitle());
                tv_item_singerAlum.setText(mp3Info.getSinger()+"-"+mp3Info.getAlbum());
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadReceiver);
        Intent intent = new Intent();
        intent.putExtra("PLAYFLAG","STOP");
        intent.setClass(MainActivity.this, PlayerService.class);
        startService(intent);
    }
}
