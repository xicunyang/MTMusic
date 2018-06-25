package www.mutou.com.application;

import android.animation.ObjectAnimator;
import android.app.Application;

/**
 * Created by 木头 on 2018/6/23.
 */

public class MyApplication extends Application{
    //正在播放？
    public static boolean isPlaying = false;
    //刚打开--是否是停止？
    public static boolean isStoping = true;
    //当前正在播放的position
    public static int nowPosition = -1;
    //当前正在播放的position的上一个position
    public static int oldPosition = -1;
    //MainFAB旋转对象
    public static ObjectAnimator MainFABRotation = null;
}
