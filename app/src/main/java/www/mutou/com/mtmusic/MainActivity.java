package www.mutou.com.mtmusic;

import android.Manifest;
import android.animation.Animator;
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
import android.os.IBinder;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuPresenter;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tandong.swichlayout.SwitchLayout;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import www.mutou.com.application.MyApplication;
import www.mutou.com.local.LocalMain;
import www.mutou.com.model.KuGouInfo;
import www.mutou.com.model.KuWoInfo;
import www.mutou.com.model.Mp3Info;
import www.mutou.com.service.PlayerService;
import www.mutou.com.url.UrlMain;
import www.mutou.com.utils.DensityUtil;
import www.mutou.com.utils.PlayerControls;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

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
    private NavigationView main_nav;
    private TextView header_tv;
    private View nav_header;
    private ImageView iv_header_userPic;
    private TextView tv_header_username;
    private PlayerControls playerControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //淡入效果，很不错
        Transition explode = TransitionInflater.from(this).inflateTransition(android.R.transition.fade);
        getWindow().setEnterTransition(explode);
        setContentView(R.layout.activity_main);

        //先得到player控制器
        playerControls = new PlayerControls(this);


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
                startActivity(intent);
//                startActivityForResult(intent, 0, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
        urlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,UrlMain.class);
                startActivityForResult(intent, 0, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
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
        NavigationView nav = (NavigationView) findViewById(R.id.main_nav);
        View nav_header = nav.getHeaderView(0);
        nav.setItemIconTintList(null);
        setNavigationMenuLineStyle(nav,Color.parseColor("#999999"),1);
        iv_header_userPic = (ImageView) nav_header.findViewById(R.id.iv_header_userpic);
        tv_header_username = (TextView) nav_header.findViewById(R.id.tv_header_username);


        main_fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(MyApplication.isPlaying_url||MyApplication.isPlaying_local){
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,DetailActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        //主按钮点击
        main_fab.setOnClickListener(this);

        //上一首
        prev.setOnClickListener(this);

        //下一首
        next.setOnClickListener(this);

        //暂停---播放切换
        playOrPause.setOnClickListener(this);

        //进入歌曲详细页面
        ll_item.setOnClickListener(this);
    }

    public static void setNavigationMenuLineStyle(NavigationView navigationView, @ColorInt final int color, final int height) {
        try {
            Field fieldByPressenter = navigationView.getClass().getDeclaredField("mPresenter");
            fieldByPressenter.setAccessible(true);
            NavigationMenuPresenter menuPresenter = (NavigationMenuPresenter) fieldByPressenter.get(navigationView);
            Field fieldByMenuView = menuPresenter.getClass().getDeclaredField("mMenuView");
            fieldByMenuView.setAccessible(true);
            final NavigationMenuView mMenuView = (NavigationMenuView) fieldByMenuView.get(menuPresenter);
            mMenuView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    RecyclerView.ViewHolder viewHolder = mMenuView.getChildViewHolder(view);
                    if (viewHolder != null && "SeparatorViewHolder".equals(viewHolder.getClass().getSimpleName()) && viewHolder.itemView != null) {
                        if (viewHolder.itemView instanceof FrameLayout) {
                            FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
                            View line = frameLayout.getChildAt(0);
                            line.setBackgroundColor(color);
                            line.getLayoutParams().height = height;
                            line.setLayoutParams(line.getLayoutParams());
                        }
                    }
                }

                @Override
                public void onChildViewDetachedFromWindow(View view) {

                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_fab:
                //主按钮点击
                main_fab_Click();
                break;
            case R.id.prev:
                //上一曲点击
                prev();
                break;
            case R.id.playOrPause:
                //播放||暂停
                playOrPause();
                break;
            case R.id.next:
                //下一曲
                next();
                break;
            case R.id.ll_nowPlayItem:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,DetailActivity.class);
                startActivity(intent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ButtonGroupCloseAnimation();
                    }
                },1000);
                break;
            default:
                break;
        }
    }

    //主悬浮按钮点击事件执行代码
    private void main_fab_Click(){
        //如果按钮没被点击
        if(!fab_ckeched){
            /*showFabItems();
            fab_ckeched = true;
            //设置动画  item显示
            if(!MyApplication.STOPING){
                ObjectAnimator item_alpha = ObjectAnimator.ofFloat(ll_item,"alpha",0f,0f,1f);
                ObjectAnimator item_translationX = ObjectAnimator.ofFloat(ll_item,"translationX",300f,0f);
                AnimatorSet set = new AnimatorSet();
                set.play(item_alpha).with(item_translationX);
                set.setDuration(500);
                set.start();
                ll_item.setVisibility(View.VISIBLE);
            }*/
            ButtonGroupShowAnimation();
        }
        //如果按钮以被点击
        else{
           /* closeFabItems();
            //设置动画  item退出
            ObjectAnimator item_alpha = ObjectAnimator.ofFloat(ll_item, "alpha", 1f, 0f, 0f);
            ObjectAnimator item_translationX = ObjectAnimator.ofFloat(ll_item, "translationX", 0f, 300f);
            AnimatorSet set = new AnimatorSet();
            set.play(item_alpha).with(item_translationX);
            set.setDuration(500);
            set.start();

            //rl_item.setVisibility(View.GONE);
            fab_ckeched = false;*/
            ButtonGroupCloseAnimation();
        }
    }

    //点击歌曲信息框之后进行按钮组的显示
    private void ButtonGroupShowAnimation(){
        showFabItems();
        fab_ckeched = true;
        //设置动画  item显示
        if(!MyApplication.STOPING){
            ObjectAnimator item_alpha = ObjectAnimator.ofFloat(ll_item,"alpha",0f,0f,1f);
            ObjectAnimator item_translationX = ObjectAnimator.ofFloat(ll_item,"translationX",300f,0f);
            AnimatorSet set = new AnimatorSet();
            set.play(item_alpha).with(item_translationX);
            set.setDuration(500);
            set.start();
            ll_item.setVisibility(View.VISIBLE);
        }
    }

    //点击歌曲信息框之后进行按钮组的隐藏
    private void ButtonGroupCloseAnimation(){
        closeFabItems();
        //设置动画  item退出
        ObjectAnimator item_alpha = ObjectAnimator.ofFloat(ll_item, "alpha", 1f, 0f, 0f);
        ObjectAnimator item_translationX = ObjectAnimator.ofFloat(ll_item, "translationX", 0f, 300f);
        AnimatorSet set = new AnimatorSet();
        set.play(item_alpha).with(item_translationX);
        set.setDuration(500);
        set.start();

        //rl_item.setVisibility(View.GONE);
        fab_ckeched = false;
    }

    //上一曲
    private void prev(){
        playerControls.prev();
    }

    //暂停||播放
    private void playOrPause(){
        boolean result = playerControls.playOrPause();
        if(result == true){
            //--->2play
            playOrPause.setImageResource(R.drawable.playing);
        }
        else{
            //--->2pause
            playOrPause.setImageResource(R.drawable.pausing);
        }
    }

    //下一曲
    private void next(){
        playerControls.next();
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

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //侧边栏开启
                //侧边栏展开动作捕捉之后执行的动画
                ObjectAnimator iv_userPic_scaleX = ObjectAnimator.ofFloat(iv_header_userPic,"scaleX",1f,1.5f,0.8f,1f);
                ObjectAnimator iv_userPic_scaleY = ObjectAnimator.ofFloat(iv_header_userPic,"scaleY",1f,0.5f,1.2f,1f);
                ObjectAnimator iv_userPic_alpha = ObjectAnimator.ofFloat(iv_header_userPic,"alpha",0f,1f);
                ObjectAnimator tv_username_scaleX = ObjectAnimator.ofFloat(tv_header_username,"scaleX",0.7f,1f);
                ObjectAnimator tv_username_alpha = ObjectAnimator.ofFloat(tv_header_username,"alpha",0f,1f);

                AnimatorSet set = new AnimatorSet();
                set.setDuration(500);
                set.play(iv_userPic_scaleX).with(iv_userPic_scaleY).with(tv_username_scaleX)
                .with(iv_userPic_alpha).with(tv_username_alpha);
                set.start();
                iv_header_userPic.setVisibility(View.VISIBLE);
                tv_header_username.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //侧边栏关闭
                iv_header_userPic.setVisibility(View.INVISIBLE);
                tv_header_username.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
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
            Log.d(TAG, "onReceive: yxc666-->receive OK");
            //成功接受到广播---开始进行判断
            if(intent.getStringExtra("WHAT").equals("changePlayOrPause")){
                Log.d(TAG, "onReceive: yxc666-->111");
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

                if(MyApplication.isLocal){
                    if(MyApplication.isPlaying_local){
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
                }else{
                    if(MyApplication.isPlaying_url){
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
                }

                if(MyApplication.isLocal){
                    //更新歌曲信息到item中
                    Mp3Info mp3Info = MyApplication.nowMp3Info;
                    tv_item_title.setText(mp3Info.getTitle());
                    tv_item_singerAlum.setText(mp3Info.getSinger()+"-"+mp3Info.getAlbum());
                }
                else {
                    switch (MyApplication.isWho){
                        case "kw":
                            KuWoInfo.abslist kuwoinfo = MyApplication.nowUrlMp3Info_kw;
                            tv_item_title.setText(kuwoinfo.getSONGNAME());
                            tv_item_singerAlum.setText(kuwoinfo.getARTIST()+"-"+kuwoinfo.getALBUM());
                            break;
                        case "kg":
                            KuGouInfo.data.lists kugouinfo = MyApplication.nowUrlMp3Info_kg;
                            tv_item_title.setText(kugouinfo.getSongName());
                            tv_item_singerAlum.setText(kugouinfo.getSingerName()+"-"+kugouinfo.getAlbumName());
                            break;
                        default:
                            break;
                    }
                }
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
