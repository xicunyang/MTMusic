package www.mutou.com.url;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import www.mutou.com.adapter.AdapterUrlListView_Kuwo;
import www.mutou.com.model.KuWoInfo;
import www.mutou.com.mtmusic.MainActivity;
import www.mutou.com.mtmusic.R;
import www.mutou.com.service.HtmlService;
import www.mutou.com.service.testUrlPlayer;
import www.mutou.com.utils.DensityUtil;

public class UrlMain extends SwipeBackActivity implements AdapterView.OnItemClickListener {
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
        //进场动画
        inAnimation();
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
        //进度条
        url_progressbar = (ProgressBar) findViewById(R.id.url_progressbar);

        //设置大搜索按钮点击事件
        search_big_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //圆圈上移+放大镜上移+搜索框淡入+搜索按钮淡入
                zcAnimation();
            }
        });
        //设置小搜索按钮的点击事件
        url_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示酷我酷狗搜索按钮
                ObjectAnimator kuwoItemAlpha = ObjectAnimator.ofFloat(kuwo_item,"alpha",0f,1f);
                ObjectAnimator kugouItemAlpha = ObjectAnimator.ofFloat(kugou_item,"alpha",0f,1f);

                AnimatorSet set = new AnimatorSet();
                set.play(kuwoItemAlpha).with(kugouItemAlpha);
                set.setDuration(1000);
                set.start();
                kuwo_item.setVisibility(View.VISIBLE);
                kugou_item.setVisibility(View.VISIBLE);


                //将第一个的dot进行拉伸---形成下划线
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showDot("kuwo");
                        url_progressbar.setVisibility(View.VISIBLE);
                        //进行歌曲列表集合的获取
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getKuwoSongs();
                            }
                        }).start();
                    }
                },500);
            }
        });
        kuwo_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDot("kuwo");
                url_progressbar.setVisibility(View.VISIBLE);
                //进行歌曲列表集合的获取
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getKuwoSongs();
                    }
                }).start();
            }
        });
        kugou_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDot("kugou");
            }
        });


    }

    //酷我音乐获取音乐集合
    private void getKuwoSongs() {

        String searchMessage = "";
        if(TextUtils.isEmpty(url_et.getText())){
            //
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
        String jsonString = null;
        //成功获取网页内容
        try {
            String url = "http://search.kuwo.cn/r.s?all="+searchMessage+"&ft=music&itemset=web_2013&client=kt&pn=0&rn=120&rformat=json&encoding=utf8";
            Log.d(TAG, "getKuwoSongs: yxc---"+url);
            jsonString = HtmlService.getHtml(url);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //接下来进行数据解析
        jsonString = jsonString.replace("\\&", "&");
        jsonString = jsonString.replace("&nbsp;", " ");
        jsonString = "[" + jsonString + "]";


        //使用阿里的fastJson方法
        kuwo_list = JSON.parseArray(jsonString,KuWoInfo.class);
        Message message = Message.obtain();
        message.arg1 = KUWO;
        handler.sendMessage(message);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //修改list的值，插入数据---使用不同的Adapter
            Log.d(TAG, "getKuwoSongs: yxc---"+kuwo_list.get(0).getAbslist().length);
            AdapterUrlListView_Kuwo adapterUrlListView_kuwo = new AdapterUrlListView_Kuwo(UrlMain.this,kuwo_list);
            url_listview.setAdapter(adapterUrlListView_kuwo);
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
            case "kuwo":
                DotScaleX= ObjectAnimator.ofFloat(kuwoDot,"scaleX",0f,50f);
                DotScaleX.setDuration(500);
                DotScaleX.start();
                kuwoDot.setVisibility(View.VISIBLE);
                break;
            case "kugou":
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

    //进场动画
    private void inAnimation() {
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
        //1.获取到position---使用position进行获取到此条目

        View v = url_listview.getChildAt(position-url_listview.getFirstVisiblePosition());
        TextView tv = (TextView) v.findViewById(R.id.url_detail_url);
        Log.d(TAG, "onItemClick: yxc--->"+tv.getText());

        /*Intent intent = new Intent();
        intent.setClass(UrlMain.this,testUrlPlayer.class);
        startService(intent);*/
    }
}






























