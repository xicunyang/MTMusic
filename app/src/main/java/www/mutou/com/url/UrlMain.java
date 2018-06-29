package www.mutou.com.url;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import www.mutou.com.adapter.AdapterUrlListView_Kugou;
import www.mutou.com.adapter.AdapterUrlListView_Kuwo;
import www.mutou.com.application.MyApplication;
import www.mutou.com.local.LocalMain;
import www.mutou.com.model.KuGouInfo;
import www.mutou.com.model.KuWoInfo;
import www.mutou.com.model.Mp3Info;
import www.mutou.com.mtmusic.MainActivity;
import www.mutou.com.mtmusic.R;
import www.mutou.com.service.HtmlService;
import www.mutou.com.service.PlayerService;
import www.mutou.com.service.testUrlPlayer;
import www.mutou.com.utils.DensityUtil;
import www.mutou.com.utils.GetKugouSongs;
import www.mutou.com.utils.GetKuwoSongs;
import www.mutou.com.utils.PlayerControls;
import www.mutou.com.utils.UrlItemClick;

public class UrlMain extends SwipeBackActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,View.OnClickListener{
    private SwipeBackLayout mSwipeBackLayout;
    private CircleImageView search_big;
    private CircleImageView search_big_empty;
    private TextView tv_1;
    private TextView tv_2;
    private TextView tv_3;
    private EditText url_et;
    private ImageView url_iv;
    private LinearLayout kuwo_item;
    private LinearLayout kugou_item;
    private ImageView kuwoDot;
    private ImageView kugouDot;
    private static final String TAG = "UrlMain";
    private List<KuWoInfo> kuwo_list;
    private final int KUWO = 1;
    private final int KUGOU = 2;
    private ListView url_listview;
    private ProgressBar url_progressbar;
    private List<KuGouInfo> kugou_list;
    private Dialog dialog;

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

        setContentView(R.layout.activity_url_main);

        setToolBar();

        //找到控件
        initViews();

        //如果正在播放网络文件
        if(!MyApplication.Url_main_Animate_showOrNot){
            //去掉那些动画---直接显示内容
            url_et.setVisibility(View.VISIBLE);
            url_iv.setVisibility(View.VISIBLE);
            kuwo_item.setVisibility(View.VISIBLE);
            kugou_item.setVisibility(View.VISIBLE);
            search_big_empty.setVisibility(View.GONE);

            switch (MyApplication.isWho){
                case "kw":
                    showDot("kw");
                    url_et.setText(MyApplication.nowSearching);
                    AdapterUrlListView_Kuwo adapterUrlListView_kuwo = new AdapterUrlListView_Kuwo(this,MyApplication.allUrlmp3list_kw);
                    url_listview.setAdapter(adapterUrlListView_kuwo);
                    if(MyApplication.nowUrlPosition>3){
                        url_listview.setSelection(MyApplication.nowUrlPosition-3);
                    }
                    break;
                case "kg":
                    showDot("kg");
                    url_et.setText(MyApplication.nowSearching);
                    AdapterUrlListView_Kugou adapterUrlListView_kugou = new AdapterUrlListView_Kugou(this,MyApplication.allUrlmp3list_kg);
                    url_listview.setAdapter(adapterUrlListView_kugou);
                    if(MyApplication.nowUrlPosition>3){
                        url_listview.setSelection(MyApplication.nowUrlPosition-3);
                    }
                    break;
                default:
                    break;
            }
        }else{
            //进场动画
            inAnimation();
        }
    }
    //找到控件
    private void initViews() {
        search_big = (CircleImageView) findViewById(R.id.url_search_big);
        search_big_empty = (CircleImageView) findViewById(R.id.url_search_big_empty);
        tv_1 = (TextView) findViewById(R.id.urlMain_tv_1);
        tv_2 = (TextView) findViewById(R.id.urlMain_tv_2);
        tv_3 = (TextView) findViewById(R.id.urlMain_tv_3);
        url_et = (EditText) findViewById(R.id.url_search_et);
        url_iv = (ImageView) findViewById(R.id.url_search_iv);

        kuwo_item = (LinearLayout) findViewById(R.id.kuwo_item);
        kugou_item = (LinearLayout) findViewById(R.id.kugou_item);

        //两个dot
        kuwoDot = (ImageView) findViewById(R.id.iv_kuwo_dot);
        kugouDot = (ImageView) findViewById(R.id.iv_kugou_dot);

        //找到listview控件
        url_listview = (ListView) findViewById(R.id.urlMain_listview);
        url_listview.setOnItemClickListener(this);
        url_listview.setOnItemLongClickListener(this);
        //进度条
        url_progressbar = (ProgressBar) findViewById(R.id.url_progressbar);

        //设置大搜索按钮点击事件
        search_big_empty.setOnClickListener(this);
        //设置小搜索按钮的点击事件
        url_iv.setOnClickListener(this);
        kuwo_item.setOnClickListener(this);
        kugou_item.setOnClickListener(this);



    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.url_search_big_empty:
                //圆圈上移+放大镜上移+搜索框淡入+搜索按钮淡入
                zcAnimation();
                break;
            case R.id.url_search_iv:
                //显示酷我酷狗搜索按钮
                ObjectAnimator kuwoItemAlpha = ObjectAnimator.ofFloat(kuwo_item,"alpha",0f,1f);
                ObjectAnimator kugouItemAlpha = ObjectAnimator.ofFloat(kugou_item,"alpha",0f,1f);

                AnimatorSet set = new AnimatorSet();
                set.play(kuwoItemAlpha).with(kugouItemAlpha);
                set.setDuration(1000);
                set.start();
                kuwo_item.setVisibility(View.VISIBLE);
                kugou_item.setVisibility(View.VISIBLE);
                //设置位置标示为0
                MyApplication.nowUrlPosition = -2;
                //设置UrlMain动画是否显示？
                MyApplication.Url_main_Animate_showOrNot = false;

                //将第一个的dot进行拉伸---形成下划线
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showDot("kw");
                        url_progressbar.setVisibility(View.VISIBLE);
                        //进行歌曲列表集合的获取
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getSongsList(KUWO);
                            }
                        }).start();
                    }
                },500);
                break;
            case R.id.kuwo_item:
                showDot("kw");
                url_progressbar.setVisibility(View.VISIBLE);
                //设置位置标示为0
                MyApplication.nowUrlPosition = -2;
                //进行歌曲列表集合的获取
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getSongsList(KUWO);
                    }
                }).start();
                break;
            case R.id.kugou_item:
                showDot("kg");
                MyApplication.nowUrlPosition = -2;
                url_progressbar.setVisibility(View.VISIBLE);
                //设置位置标示为0
                MyApplication.nowUrlPosition = -2;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getSongsList(KUGOU);
                    }
                }).start();
                break;
            case R.id.urlMain_bottom_download:
                Toast.makeText(this, "进入下载页面", Toast.LENGTH_SHORT).show();
                dialog.hide();
                break;
            case R.id.urlMain_bottom_like:
                Toast.makeText(this, "您已添加收藏，感谢您的支持", Toast.LENGTH_SHORT).show();
                dialog.hide();
                break;
            case R.id.urlMain_bottom_playmv:
                //先进行正在播放的音乐的一个暂停状态
                PlayerControls playerControls = new PlayerControls(this);
                playerControls.playOrPause();

                dialog.hide();
                Intent intent = new Intent();
                intent.setClass(UrlMain.this,UrlMV.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    private void getSongsList(int flag){
        String searchMessage = "";
        if(TextUtils.isEmpty(url_et.getText())){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UrlMain.this, "请输入内容后再进行查询~", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        else{
            searchMessage = url_et.getText().toString();
        }
        MyApplication.nowSearching = searchMessage;

        Message message = Message.obtain();
        switch (flag){
            case KUWO:
                kuwo_list = new GetKuwoSongs().getSongs(searchMessage);
                message.arg1 = KUWO;
                break;
            case KUGOU:
                kugou_list = new GetKugouSongs().getSongs(searchMessage);
                message.arg1 = KUGOU;
                break;
            default:
                break;
        }

        handler.sendMessage(message);
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1){
                case KUWO:
                    //修改list的值，插入数据---使用不同的Adapter
                    MyApplication.allUrlmp3list_kw = kuwo_list;
                    AdapterUrlListView_Kuwo adapterUrlListView_kuwo = new AdapterUrlListView_Kuwo(UrlMain.this,kuwo_list);
                    url_listview.setAdapter(adapterUrlListView_kuwo);
                    break;
                case KUGOU:
                    //修改list的值，插入数据---使用不同的Adapter
                    MyApplication.allUrlmp3list_kg = kugou_list;
                    Log.d(TAG, "handleMessage: --->yxchhh");
                    //添加Adapter
                    AdapterUrlListView_Kugou adapterUrlListView_kugou= new AdapterUrlListView_Kugou(UrlMain.this,kugou_list);
                    url_listview.setAdapter(adapterUrlListView_kugou);
                    break;

                default:
                    break;
            }
            url_progressbar.setVisibility(View.GONE);

        }
    };

    private void showDot(String flag){
        //显示之前先将原来的去掉
        ObjectAnimator CkuwoDotScaleX = ObjectAnimator.ofFloat(kuwoDot,"scaleX",50f,0f);
        ObjectAnimator CkugouDotScaleX = ObjectAnimator.ofFloat(kugouDot,"scaleX",50f,0f);
        AnimatorSet set = new AnimatorSet();
        set.play(CkugouDotScaleX).with(CkuwoDotScaleX);
        set.setDuration(450);
        set.start();

        ObjectAnimator DotScaleX = null;
        switch (flag){
            case "kw":
                DotScaleX= ObjectAnimator.ofFloat(kuwoDot,"scaleX",0f,50f);
                DotScaleX.setDuration(500);
                DotScaleX.start();
                kuwoDot.setVisibility(View.VISIBLE);
                break;
            case "kg":
                DotScaleX= ObjectAnimator.ofFloat(kugouDot,"scaleX",0f,50f);
                DotScaleX.setDuration(500);
                DotScaleX.start();
                kugouDot.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }

    //圆圈上移+放大镜上移+搜索框淡入+搜索按钮淡入
    private void zcAnimation(){
        //圆圈上移淡出
        float pianyi = DensityUtil.dip2px(UrlMain.this,110);
        ObjectAnimator searchBigEmpty_scaleX = ObjectAnimator.ofFloat(search_big_empty,"scaleX",1f,2.3f);
        ObjectAnimator searchBigEmpty_scaleY = ObjectAnimator.ofFloat(search_big_empty,"scaleY",1f,0.3f);
        ObjectAnimator searchBigEmpty_alpha = ObjectAnimator.ofFloat(search_big_empty,"alpha",1f,0.2f,0f);
        ObjectAnimator searchBigEmpty_translationY = ObjectAnimator.ofFloat(search_big_empty,"translationY",-pianyi,-pianyi-400f);
        ObjectAnimator searchBigEmpty_translationX = ObjectAnimator.ofFloat(search_big_empty,"translationX",0,-100f);
        AnimatorSet set = new AnimatorSet();
        set.play(searchBigEmpty_scaleX).with(searchBigEmpty_scaleY).with(searchBigEmpty_alpha)
                .with(searchBigEmpty_translationY).with(searchBigEmpty_translationX);
        set.setDuration(1000);
        set.start();

        //放大镜上移淡出
        ObjectAnimator searchBig_scaleX = ObjectAnimator.ofFloat(search_big,"scaleX",1f,0.5f);
        ObjectAnimator searchBig_scaleY = ObjectAnimator.ofFloat(search_big,"scaleY",1f,0.5f);
        ObjectAnimator searchBig_alpha = ObjectAnimator.ofFloat(search_big,"alpha",1f,0.2f,0f);
        ObjectAnimator searchBig_translationY = ObjectAnimator.ofFloat(search_big,"translationY",-pianyi,-pianyi-400f);
        ObjectAnimator searchBig_translationX = ObjectAnimator.ofFloat(search_big,"translationX",0,560f);
        AnimatorSet set2 = new AnimatorSet();
        set2.play(searchBig_scaleX).with(searchBig_scaleY).with(searchBig_alpha)
                .with(searchBig_translationY).with(searchBig_translationX);
        set2.setDuration(1000);
        set2.start();

        //将搜索框和放大镜淡入
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //先将上面两个给gong掉---防止误触
                search_big_empty.setVisibility(View.INVISIBLE);
                search_big.setVisibility(View.INVISIBLE);

                ObjectAnimator url_et_alpha = ObjectAnimator.ofFloat(url_et,"alpha",0f,1f);
                ObjectAnimator url_iv_alpha = ObjectAnimator.ofFloat(url_iv,"alpha",0f,1f);
                AnimatorSet set = new AnimatorSet();
                set.setDuration(1000);
                set.play(url_et_alpha).with(url_iv_alpha);
                set.start();
                url_et.setVisibility(View.VISIBLE);
                url_iv.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ObjectAnimator tv1_alpha = ObjectAnimator.ofFloat(tv_1,"alpha",1f,0f);
                        ObjectAnimator tv2_alpha = ObjectAnimator.ofFloat(tv_2,"alpha",1f,0f);
                        ObjectAnimator tv3_alpha = ObjectAnimator.ofFloat(tv_3,"alpha",1f,0f);

                        ObjectAnimator tv1_translationY = ObjectAnimator.ofFloat(tv_1,"translationY",0f,50f);
                        ObjectAnimator tv2_translationY = ObjectAnimator.ofFloat(tv_2,"translationY",0f,100f);
                        ObjectAnimator tv3_translationY = ObjectAnimator.ofFloat(tv_3,"translationY",0f,150f);

                        AnimatorSet set = new AnimatorSet();
                        set.setDuration(600);
                        set.play(tv1_alpha).with(tv1_translationY)
                                .with(tv2_alpha).with(tv2_translationY)
                                .with(tv3_alpha).with(tv3_translationY);
                        set.start();
                    }
                },1000);
            }
        },700);
    }

    //进场动画   1
    private void inAnimation() {
        //大圆圈
        float pianyi = DensityUtil.dip2px(this,110);
        ObjectAnimator searchBigEmpty_scaleX = ObjectAnimator.ofFloat(search_big_empty,"scaleX",1f,0.5f,0.6f,0.7f,0.8f,0.9f,1f);
        ObjectAnimator searchBigEmpty_scaleY = ObjectAnimator.ofFloat(search_big_empty,"scaleY",1f,0.5f,0.6f,0.7f,0.8f,0.9f,1f);
        ObjectAnimator searchBigEmpty_alpha = ObjectAnimator.ofFloat(search_big_empty,"alpha",1f,0.3f,0.5f,0.7f,0.9f,1f);
        ObjectAnimator searchBigEmpty_translationY = ObjectAnimator.ofFloat(search_big_empty,"translationY",pianyi,-pianyi);
        AnimatorSet set = new AnimatorSet();
        set.play(searchBigEmpty_translationY).with(searchBigEmpty_scaleX).with(searchBigEmpty_scaleY).with(searchBigEmpty_alpha);
        set.setDuration(1000);
        set.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //大搜索按钮
                float pianyi = DensityUtil.dip2px(UrlMain.this,110);
                ObjectAnimator searchBig_alpha = ObjectAnimator.ofFloat(search_big,"alpha",0f,1f);
                ObjectAnimator searchBig_scaleX = ObjectAnimator.ofFloat(search_big,"scaleX",1f,1f);
                ObjectAnimator searchBig_scaleY = ObjectAnimator.ofFloat(search_big,"scaleY",1f,1f);
                ObjectAnimator searchBigEmpty_translationY = ObjectAnimator.ofFloat(search_big,"translationY",-pianyi,-pianyi);
                AnimatorSet set = new AnimatorSet();
                set.play(searchBig_alpha).with(searchBig_scaleX).with(searchBig_scaleY).with(searchBigEmpty_translationY);
                set.setDuration(700);
                set.start();
                search_big.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //三个字
                        float pianyi = DensityUtil.dip2px(UrlMain.this,50);
                        ObjectAnimator tv1_translationX = ObjectAnimator.ofFloat(tv_1,"translationX",-pianyi,0f);
                        ObjectAnimator tv2_translationX = ObjectAnimator.ofFloat(tv_2,"translationX",pianyi,0f);
                        ObjectAnimator tv3_translationY = ObjectAnimator.ofFloat(tv_3,"translationY",50f,0f);
                        ObjectAnimator tv1_alpha = ObjectAnimator.ofFloat(tv_1,"alpha",0f,1f);
                        ObjectAnimator tv2_alpha = ObjectAnimator.ofFloat(tv_2,"alpha",0f,1f);
                        ObjectAnimator tv3_alpha = ObjectAnimator.ofFloat(tv_3,"alpha",0f,1f);
                        AnimatorSet set = new AnimatorSet();
                        set.setDuration(700);
                        set.play(tv1_translationX).with(tv2_translationX).with(tv3_translationY)
                        .with(tv1_alpha).with(tv2_alpha).with(tv3_alpha);
                        set.start();
                        tv_1.setVisibility(View.VISIBLE);
                        tv_2.setVisibility(View.VISIBLE);
                        tv_3.setVisibility(View.VISIBLE);
                    }
                },600);
            }
        },1000);
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

    //在item点击的时候
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UrlItemClick urlItemClick = new UrlItemClick(kuwo_list,kugou_list,url_listview,position,this);
        urlItemClick.urlItemClick();
//        //设置isLocal为false---证明现在就是isUrl，要清空其变量的值吗？我觉得可以不清空
//        MyApplication.isLocal = false;
//        MyApplication.nowPosition = -1;
//        MyApplication.STOPING = false;
//        MyApplication.isPlaying_local = false;
//
//        //1.获取到position---使用position进行获取到此条目
//
//        View v = url_listview.getChildAt(position-url_listview.getFirstVisiblePosition());
//        TextView tv_who = (TextView) v.findViewById(R.id.url_detail_who);
//        switch (tv_who.getText().toString()){
//            case "kw":
//                MyApplication.isWho = "kw";
//                if(kuwo_list==null){
//                    kuwo_list = MyApplication.allUrlmp3list_kw;
//                }
//                MyApplication.nowUrlMp3Info_kw = kuwo_list.get(0).getAbslist()[position];
//                break;
//            case "kg":
//                MyApplication.isWho = "kg";
//                if(kugou_list == null){
//                    kugou_list = MyApplication.allUrlmp3list_kg;
//                }
//                MyApplication.nowUrlMp3Info_kg = kugou_list.get(0).getData()[0].getLists()[position];
//                break;
//            default:
//                break;
//        }
//
//        //先更新位置
//        MyApplication.oldUrlPosition = MyApplication.nowUrlPosition;
//        MyApplication.nowUrlPosition = position;
//
//        linkToService(position);
    }

//    private synchronized void linkToService(int position){
//        Intent intent = new Intent();
//        //先将停止的flag去掉---第一次播放STOP2PLAY
//        //这里的isstoping会有冲突---第一首歌播放完之后就将此改为false了
//        if(MyApplication.isStoping_url){
//            MyApplication.isStoping_url = false;
//            MyApplication.isStoping_local = true;
//            intent.putExtra("PLAYFLAG","STOP2PLAY");
//            intent.putExtra("WHO","URL");
//            intent.setClass(UrlMain.this, PlayerService.class);
//            startService(intent);
//        }
//        //如果两次点击的是同一条---就暂停PLAY2PAUSE
//        else if(position == MyApplication.oldUrlPosition){
//            if(MyApplication.isPlaying_url){
//                intent.putExtra("PLAYFLAG","PLAY2PAUSE");
//                intent.putExtra("WHO","URL");
//                intent.setClass(UrlMain.this, PlayerService.class);
//                startService(intent);
//            }
//            else{
//                intent.putExtra("PLAYFLAG","PAUSE2PLAY");
//                intent.putExtra("WHO","URL");
//                intent.setClass(UrlMain.this, PlayerService.class);
//                startService(intent);
//            }
//        }
//        else if(MyApplication.oldUrlPosition!=-1 && position != MyApplication.oldUrlPosition){
//            intent.putExtra("PLAYFLAG","PLAYNEW");
//            intent.putExtra("WHO","URL");
//            intent.setClass(UrlMain.this, PlayerService.class);
//            startService(intent);
//        }
//        //改变点的状态---切换位置
//        changePlayingFlag(position);
//    }
//    private void changePlayingFlag(int position) {
//        int start = url_listview.getFirstVisiblePosition();
//        int end = url_listview.getLastVisiblePosition();
//        int oldPosition = MyApplication.oldUrlPosition;
//        int newPosition = MyApplication.nowUrlPosition;
//
//        if(oldPosition!=-1){
//            //说明不是第一次点击items
//            //先清除原来的
//            //此时---oldPosition-start<count有两种情况
//            //情况1---正常
//            //情况2---多滑一点点，count会多1
//            if(oldPosition>=start && oldPosition<=end){
//                //说明在显示区域内---不在显示区域内的话就不管了
//                //先去掉原来的
//                //怎样获取到位置呢？
//                //说明是第一次点击items
//                //oldPosition-start来定位当前视图内的位置
//                setItemFlagChange(oldPosition-start,View.GONE);
//                //设置新的
//                //newPosition-start来定位新按下的在视图内的位置
//                setItemFlagChange(newPosition-start,View.VISIBLE);
//            }
//            else{
//                //设置新的
//                //newPosition-start来定位新按下的在视图内的位置
//                setItemFlagChange(newPosition-start,View.VISIBLE);
//            }
//
//        }
//        else{
//            //说明是第一次点击items
//            //position-start来定位当前摁下的在可见视图内的位置
//            setItemFlagChange(position-start,View.VISIBLE);
//        }
//    }
//
//    public synchronized void setItemFlagChange(int position,int visible){
//        View v = url_listview.getChildAt(position);
//        ImageView iv = (ImageView) v.findViewById(R.id.url_detail_playing_flag);
//        iv.setVisibility(visible);
//    }

    //长摁事件
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        View v = url_listview.getChildAt(position-url_listview.getFirstVisiblePosition());
        TextView tv_mvUrl = (TextView) v.findViewById(R.id.url_detail_mvUrl);
        TextView tv_mp3ID = (TextView) v.findViewById(R.id.url_detail_url);
        TextView tv_who = (TextView) v.findViewById(R.id.url_detail_who);
        if(tv_mvUrl.getText().equals("0")){
            //自定义底部弹出Dialog
            setDialog(false);
        }
        else{
            //自定义底部弹出Dialog
            setDialog(true);
            MyApplication.nowPlayingMvUrl = tv_mp3ID.getText().toString().substring(4,tv_mp3ID.getText().toString().length());
        }
        return true;
    }
    private void setDialog(boolean showMvOrNot){
        dialog = new Dialog(this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.url_main_bottom_dialog,null
        );
        //初始化视图
        Button btn_download = (Button) root.findViewById(R.id.urlMain_bottom_download);
        Button btn_like = (Button) root.findViewById(R.id.urlMain_bottom_like);
        Button btn_playMv = (Button) root.findViewById(R.id.urlMain_bottom_playmv);
        btn_download.setOnClickListener(this);
        btn_like.setOnClickListener(this);
        btn_playMv.setOnClickListener(this);

        if(!showMvOrNot){
            btn_playMv.setVisibility(View.GONE);
        }
        else{
            btn_playMv.setVisibility(View.VISIBLE);
        }

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






























