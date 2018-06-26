package www.mutou.com.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.List;

import www.mutou.com.model.KuGouInfo;
import www.mutou.com.model.KuWoInfo;
import www.mutou.com.service.HtmlService;

/**
 * Created by 木头 on 2018/6/26.
 */

public class GetKugouSongs {
    public List<KuGouInfo> getSongs(String searchMsg){
        List<KuGouInfo> list = null;
        String jsonString = null;
        //成功获取网页内容
        try {
            String url = "http://songsearch.kugou.com/song_search_v2?keyword="+searchMsg+"&page=1&pagesize=150&userid=-1&clientver=&platform=WebFilter&tag=em&filter=2&iscorrection=1&privilege_filter=0";
            jsonString = HtmlService.getHtml(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("", "getSongs: yxc----"+jsonString);
        //接下来进行数据解析
        jsonString = jsonString.replace("\\&", "&");
        jsonString = jsonString.replace("&nbsp;", " ");
        jsonString = jsonString.replace("<em>","");
        jsonString = jsonString.replace("<\\/em>","");
        jsonString = "[" + jsonString + "]";
        Log.d("", "getSongs: yxc----"+jsonString);

        //使用阿里的fastJson方法
        list = JSON.parseArray(jsonString,KuGouInfo.class);
        return list;
    }
}
