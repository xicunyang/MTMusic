package www.mutou.com.mtmusic;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.constraint.solver.widgets.Animator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import www.mutou.com.application.MyApplication;
import www.mutou.com.service.PlayerService;
import www.mutou.com.service.testDetailService;

public class DetailActivity extends Activity implements View.OnClickListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initViews();
        initEvents();
        initService();
        changPianRotate_Start();

    }

    private void initViews() {
        seekBar = (SeekBar) findViewById(R.id.detail_seekbar);
        btn_startService = (Button) findViewById(R.id.openService);
        iv_playOrPause = (ImageView) findViewById(R.id.detail_playOrPause);
        iv_yaobi = (ImageView) findViewById(R.id.detail_yaobi);
        changpian = (CircleImageView) findViewById(R.id.detail_centerImage);
    }

    private void initEvents() {
        iv_playOrPause.setOnClickListener(this);
    }

    //唱片旋转--开始
    private void changPianRotate_Start(){
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
        changpian_rotate.start();
    }
    //唱片旋转--暂停
    private void changPianRotate_Pause(){
        changpian_rotate.start();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detail_playOrPause:
                Animation yaobi_down = AnimationUtils.loadAnimation(this,R.anim.detail_yaobi_down);
                iv_yaobi.startAnimation(yaobi_down);
                break;
        }
    }

    //连接Service
    private void initService() {
        Intent intent = new Intent();
        intent.setClass(DetailActivity.this, PlayerService.class);
        intent.putExtra("WHO","DETAIL");
        startService(intent);

        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    myBind = (PlayerService.MyBind) service;
                    //设置进度条的最大长度
                    int max = myBind.getDuration();
                    seekBar.setMax(max);
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
                            seekBar.setProgress(myBind.getCurrentPosition());
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //资源的关闭
        if(serviceConnection!=null){
            unbindService(serviceConnection);
        }
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


}






















