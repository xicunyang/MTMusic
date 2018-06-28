package www.mutou.com.local;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import www.mutou.com.adapter.AdapterLocalFragment;
import www.mutou.com.adapter.AdapterLocalListView;
import www.mutou.com.application.MyApplication;
import www.mutou.com.model.Mp3Info;
import www.mutou.com.mtmusic.R;
import www.mutou.com.service.PlayerService;
import www.mutou.com.utils.AudioUtils;

public class LocalMain extends SwipeBackActivity{

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

        setContentView(R.layout.activity_local_main);


        //设置FragementList
        List<Fragment> list = new ArrayList<>();
        list.add(new Local());
        list.add(new Cache());

        ViewPager viewPager = (ViewPager) findViewById(R.id.localMain_Viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.localMain_TabLayout);
        viewPager.setAdapter(new AdapterLocalFragment(getSupportFragmentManager(),list));
        //设置预加载的页面数
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("我的本地");
        tabLayout.getTabAt(1).setText("我的缓存");

        //显示返回按钮
        setToolBar();
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
