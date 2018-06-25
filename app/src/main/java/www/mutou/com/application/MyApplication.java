package www.mutou.com.application;

import android.animation.ObjectAnimator;
import android.app.Application;

import java.util.List;

import www.mutou.com.model.KuWoInfo;
import www.mutou.com.model.Mp3Info;

/**
 * Created by 木头 on 2018/6/23.
 */

public class MyApplication extends Application{
    public static boolean STOPING = true;
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


    public static KuWoInfo.abslist nowUrlMp3Info_kw;
    //当前列表的所有歌曲
    public static List<KuWoInfo> allUrlmp3list_kw = null;
    public static List<Mp3Info> allUrlmp3list_kg = null;


    //设置两个变量存正在使用什么播放
    public static boolean isLocal;
    //当前正在播放的position
    public static int nowUrlPosition = -1;
    //当前正在播放的position的上一个position
    public static int oldUrlPosition = -1;
    //当前正在播放的是谁家的音频
    public static String isWho;

    //正在播放---两个---local---url
    //正在播放？
    public static boolean isPlaying_local = false;
    public static boolean isPlaying_url = false;

    //刚打开--是否是停止？
    public static boolean isStoping_local = true;
    public static boolean isStoping_url = true;

    //isUrl暂时不需要
    public static boolean isUrl;
}
























