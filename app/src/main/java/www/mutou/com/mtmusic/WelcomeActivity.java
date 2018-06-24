package www.mutou.com.mtmusic;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import www.mutou.com.utils.DensityUtil;

public class WelcomeActivity extends Activity {

    private CircleImageView welcome_image;
    public static WelcomeActivity instance = null;
    private ImageView imageView_m1;
    private ImageView imageView_m2;
    private ImageView imageView_t;
    private ImageView imageView_usic;
    private LinearLayout linearLayout_mtm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_welcome);

        welcome_image = (CircleImageView) findViewById(R.id.welcome_image);
        imageView_m1 = (ImageView) findViewById(R.id.welcome_M1);
        imageView_m2 = (ImageView) findViewById(R.id.welcome_M2);
        imageView_t = (ImageView) findViewById(R.id.welcome_T);
        imageView_usic = (ImageView) findViewById(R.id.welcome_usic);
        linearLayout_mtm = (LinearLayout) findViewById(R.id.linear_MTM);


        //开始开场动画
        startAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomAnimationMTM();
            }
        },700);

        welcome_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始退场动画
                exitAnimation();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.setClass(WelcomeActivity.this,MainActivity.class);
                        startActivityForResult(intent, 0,ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this).toBundle());
                    }
                }, 500);

            }
        });
    }

    /**
     * 底部动画MTM
     */
    private void bottomAnimationMTM(){
        //M1
        ObjectAnimator m1_scalex = ObjectAnimator.ofFloat(imageView_m1,"scaleX",0.3f,1.2f,1f);
        ObjectAnimator m1_scaleY = ObjectAnimator.ofFloat(imageView_m1,"scaleY",0.3f,1.2f,1f);
        ObjectAnimator m1_alpha = ObjectAnimator.ofFloat(imageView_m1, "alpha", 0f,1f);

        AnimatorSet setm1 = new AnimatorSet();
        setm1.play(m1_scalex).with(m1_scaleY).with(m1_alpha);
        setm1.setDuration(1000);
        setm1.start();
        imageView_m1.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //t
                ObjectAnimator t_scalex = ObjectAnimator.ofFloat(imageView_t,"scaleX",0.3f,1.2f,1f);
                ObjectAnimator t_scaleY = ObjectAnimator.ofFloat(imageView_t,"scaleY",0.3f,1.2f,1f);
                ObjectAnimator t_alpha = ObjectAnimator.ofFloat(imageView_t, "alpha", 0f,1f);
                AnimatorSet sett = new AnimatorSet();
                sett.play(t_scalex).with(t_scaleY).with(t_alpha);
                sett.setDuration(1000);
                sett.start();
                imageView_t.setVisibility(View.VISIBLE);
            }
        },300);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //M2
                ObjectAnimator m2_scalex = ObjectAnimator.ofFloat(imageView_m2,"scaleX",0.3f,1.2f,1f);
                ObjectAnimator m2_scaleY = ObjectAnimator.ofFloat(imageView_m2,"scaleY",0.3f,1.2f,1f);
                ObjectAnimator m2_alpha = ObjectAnimator.ofFloat(imageView_m2, "alpha", 0f,1f);
                float pianyi = DensityUtil.dip2px(WelcomeActivity.this,20);
                ObjectAnimator mtm_translation = ObjectAnimator.ofFloat(linearLayout_mtm,"translationX",0f,-pianyi,-pianyi,-pianyi);
                AnimatorSet setm2 = new AnimatorSet();
                setm2.play(m2_scalex).with(m2_scaleY).with(m2_alpha).before(mtm_translation);
                setm2.setDuration(1000);
                setm2.start();
                imageView_m2.setVisibility(View.VISIBLE);
            }
        },600);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator usic_alpha = ObjectAnimator.ofFloat(imageView_usic, "alpha", 0f,1f);
                AnimatorSet set = new AnimatorSet();
                set.play(usic_alpha);
                set.setDuration(1000);
                set.start();
                imageView_usic.setVisibility(View.VISIBLE);
            }
        },1600);
    }
    
    /**
     * 开场动画
     */
    private void startAnimation(){
        //旋转动画
        ObjectAnimator animator_rotation = ObjectAnimator.ofFloat(welcome_image,"rotation",150f,360f);
        //横向拉伸
        ObjectAnimator animator_scalex = ObjectAnimator.ofFloat(welcome_image,"scaleX",0.3f,1f);
        //纵向拉伸
        ObjectAnimator animator_scaleY = ObjectAnimator.ofFloat(welcome_image,"scaleY",0.3f,1f);
        //纵向移动
        ObjectAnimator animator_translation = ObjectAnimator.ofFloat(welcome_image,"translationY",20f,-400f);
        //透明度
        ObjectAnimator animator_alpha = ObjectAnimator.ofFloat(welcome_image, "alpha", 0f,1f);
        //设置动画集合
        AnimatorSet set = new AnimatorSet();
        //设置动画顺序
        set.play(animator_rotation).
                with(animator_translation).
                with(animator_alpha).
                with(animator_scalex).
                with(animator_scaleY);
        //设置动画持续时间
        set.setDuration(1000);
        //开始动画
        set.start();
    }

    /**
     * 退场动画
     */
    private void exitAnimation(){
        //横向拉伸
        ObjectAnimator animator_scalex = ObjectAnimator.ofFloat(welcome_image,"scaleX",1f,0.7f,3f);
        //纵向拉伸
        ObjectAnimator animator_scaleY = ObjectAnimator.ofFloat(welcome_image,"scaleY",1f,0.7f,3f);
        //纵向移动
        ObjectAnimator animator_translation = ObjectAnimator.ofFloat(welcome_image,"translationY",-400f,0f);
        //透明度
        ObjectAnimator animator_alpha = ObjectAnimator.ofFloat(welcome_image, "alpha", 1f,0.1f,0f);

        //纵向移动
        ObjectAnimator mtm_translation = ObjectAnimator.ofFloat(linearLayout_mtm,"translationY",0f,200f,200f);
        //纵向移动
        ObjectAnimator usic_translation = ObjectAnimator.ofFloat(imageView_usic,"translationY",0f,200f,200f);
        //透明度
        ObjectAnimator mtm_alpha = ObjectAnimator.ofFloat(linearLayout_mtm, "alpha", 1f,0f,0f,0f,0f);
        //透明度
        ObjectAnimator usic_alpha = ObjectAnimator.ofFloat(imageView_usic, "alpha", 1f,0f,0f,0f,0f);


        //设置动画集合
        AnimatorSet set = new AnimatorSet();
        //设置动画顺序
        set.play(animator_translation).
                with(animator_alpha).
                with(animator_scaleY).
                with(animator_scalex).
                with(mtm_translation).
                with(usic_translation).with(mtm_alpha).with(usic_alpha);
        //设置动画持续时间
        set.setDuration(500);
        //开始动画
        set.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //在MainActivity回来的时候，会关掉这个页面---迂回战术---效果也可以
        finish();
    }
}


























