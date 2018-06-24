package www.mutou.com.url;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import www.mutou.com.mtmusic.MainActivity;
import www.mutou.com.mtmusic.R;
import www.mutou.com.utils.DensityUtil;

public class UrlMain extends SwipeBackActivity {
    private SwipeBackLayout mSwipeBackLayout;
    private CircleImageView search_big;
    private CircleImageView search_big_empty;
    private TextView tv_1;
    private TextView tv_2;
    private TextView tv_3;
    private EditText url_et;
    private ImageView url_iv;

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

        //设置大搜索按钮点击事件
        search_big_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //理想中是3D旋转
//                Intent intent = new Intent();
//                intent.setClass(UrlMain.this,UrlDetails.class);
//                startActivityForResult(intent, 0, ActivityOptions.makeSceneTransitionAnimation(UrlMain.this).toBundle());
                //测试动画
                float pianyi = DensityUtil.dip2px(UrlMain.this,110);
                ObjectAnimator searchBigEmpty_scaleX = ObjectAnimator.ofFloat(search_big_empty,"scaleX",1f,2f);
                ObjectAnimator searchBigEmpty_scaleY = ObjectAnimator.ofFloat(search_big_empty,"scaleY",1f,0.3f);
                ObjectAnimator searchBigEmpty_alpha = ObjectAnimator.ofFloat(search_big_empty,"alpha",1f,0.2f,0f);
                ObjectAnimator searchBigEmpty_translationY = ObjectAnimator.ofFloat(search_big_empty,"translationY",-pianyi,-pianyi-350f);
                ObjectAnimator searchBigEmpty_translationX = ObjectAnimator.ofFloat(search_big_empty,"translationX",0,-100f);
                AnimatorSet set = new AnimatorSet();
                set.play(searchBigEmpty_scaleX).with(searchBigEmpty_scaleY).with(searchBigEmpty_alpha)
                        .with(searchBigEmpty_translationY).with(searchBigEmpty_translationX);
                set.setDuration(1000);
                set.start();

                ObjectAnimator searchBig_scaleX = ObjectAnimator.ofFloat(search_big,"scaleX",1f,0.7f);
                ObjectAnimator searchBig_scaleY = ObjectAnimator.ofFloat(search_big,"scaleY",1f,0.7f);
                ObjectAnimator searchBig_alpha = ObjectAnimator.ofFloat(search_big,"alpha",1f,0.2f,0f);
                ObjectAnimator searchBig_translationY = ObjectAnimator.ofFloat(search_big,"translationY",-pianyi,-pianyi-300f);
                ObjectAnimator searchBig_translationX = ObjectAnimator.ofFloat(search_big,"translationX",0,450f);
                AnimatorSet set2 = new AnimatorSet();
                set2.play(searchBig_scaleX).with(searchBig_scaleY).with(searchBig_alpha)
                        .with(searchBig_translationY).with(searchBig_translationX);
                set2.setDuration(1000);
                set2.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator url_et_alpha = ObjectAnimator.ofFloat(url_et,"alpha",0f,1f);
                        ObjectAnimator url_iv_alpha = ObjectAnimator.ofFloat(url_iv,"alpha",0f,1f);
                        AnimatorSet set = new AnimatorSet();
                        set.setDuration(1000);
                        set.play(url_et_alpha).with(url_iv_alpha);
                        set.start();
                        url_et.setVisibility(View.VISIBLE);
                        url_iv.setVisibility(View.VISIBLE);
                    }
                },700);
            }
        });
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
}






























