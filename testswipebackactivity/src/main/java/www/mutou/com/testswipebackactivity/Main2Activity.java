package www.mutou.com.testswipebackactivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class Main2Activity extends SwipeBackActivity {

    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setSwipeBackEnable(true);
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }
}
