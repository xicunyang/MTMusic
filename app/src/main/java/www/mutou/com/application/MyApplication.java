package www.mutou.com.application;

import android.animation.ObjectAnimator;
import android.app.Application;

import java.util.List;

import www.mutou.com.model.Mp3Info;

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
    //当前播放的歌曲实体
    public static Mp3Info nowMp3Info;
    //当前列表的所有歌曲
    public static List<Mp3Info> allLocalmp3list = null;


    public static Mp3Info nowUrlMp3Info;
    //当前列表的所有歌曲
//    public static List<Mp3Info> allUrlmp3list = null;


    //设置两个变量存正在使用什么播放
    public static boolean isLocal;
    public static boolean isUrl;
}
























