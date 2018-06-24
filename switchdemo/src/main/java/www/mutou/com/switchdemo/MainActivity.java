package www.mutou.com.switchdemo;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.tandong.swichlayout.BaseEffects;
import com.tandong.swichlayout.SwitchLayout;

/**
 * SwitchLayout
 * 如果想自定义特效动画时长的话，请在此四个变量对应设置 SwitchLayout.animDuration = 1000;
 * SwitchLayout.longAnimDuration = 2000; BaseAnimViewS.animDuration = 1000;
 * BaseAnimViewS.longAnimDuration = 2000;即可。单位毫秒。
 */
public class MainActivity extends Activity {
    private Button btn_ok, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7,
            btn_8, btn_9, btn_10, btn_11, btn_12, btn_13, btn_14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        // 设置进入Activity的Activity特效动画，同理可拓展为布局动画
        SwitchLayout.getSlideFromBottom(this, false,
                BaseEffects.getQuickToSlowEffect());
        // 三个参数分别为（Activity/View，是否关闭Activity，特效（可为空））
        initListener();

    }

    private void initListener() {
        btn_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 0);
                MainActivity.this.startActivity(in);
            }
        });

        btn_1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 1);
                MainActivity.this.startActivity(in);
            }
        });
        btn_2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 2);
                MainActivity.this.startActivity(in);
            }
        });
        btn_3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 3);
                MainActivity.this.startActivity(in);
            }
        });
        btn_4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 4);
                MainActivity.this.startActivity(in);
            }
        });
        btn_5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 5);
                MainActivity.this.startActivity(in);
            }
        });
        btn_6.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 6);
                MainActivity.this.startActivity(in);
            }
        });

        btn_7.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 7);
                MainActivity.this.startActivity(in);
            }
        });
        btn_8.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 8);
                MainActivity.this.startActivity(in,
                        ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
        btn_9.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 9);
                MainActivity.this.startActivity(in);
            }
        });
        btn_10.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 10);
                MainActivity.this.startActivity(in);
            }
        });
        btn_11.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 11);
                MainActivity.this.startActivity(in,
                        ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
        btn_12.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 12);
                MainActivity.this.startActivity(in);
            }
        });
        btn_13.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 13);
                MainActivity.this.startActivity(in);
            }
        });
        btn_14.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent in = new Intent(MainActivity.this, AnotherActivity.class);
                in.putExtra("key", 14);
                MainActivity.this.startActivity(in);
            }
        });
    }

    private void initView() {
        btn_ok = (Button) this.findViewById(R.id.btn_ok);
        btn_1 = (Button) this.findViewById(R.id.btn_1);
        btn_2 = (Button) this.findViewById(R.id.btn_2);
        btn_3 = (Button) this.findViewById(R.id.btn_3);
        btn_4 = (Button) this.findViewById(R.id.btn_4);
        btn_5 = (Button) this.findViewById(R.id.btn_5);
        btn_6 = (Button) this.findViewById(R.id.btn_6);
        btn_7 = (Button) this.findViewById(R.id.btn_7);
        btn_8 = (Button) this.findViewById(R.id.btn_8);
        btn_9 = (Button) this.findViewById(R.id.btn_9);
        btn_10 = (Button) this.findViewById(R.id.btn_10);
        btn_11 = (Button) this.findViewById(R.id.btn_11);
        btn_12 = (Button) this.findViewById(R.id.btn_12);
        btn_13 = (Button) this.findViewById(R.id.btn_13);
        btn_14 = (Button) this.findViewById(R.id.btn_14);

    }
}
