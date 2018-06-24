package www.mutou.com.application;

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
    public static int nowPosition = 5;
}
