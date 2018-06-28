package www.mutou.com.utils;

import www.mutou.com.service.HtmlService;

/**
 * Created by 木头 on 2018/6/27.
 */

public class GetKuwoMvUrl {
    public String getUrl(String mp3ID){
        String path = "http://www.kuwo.cn/yy/st/mvurl?rid=MUSIC_"+mp3ID;
        String result = "";
        try {
            result = HtmlService.getHtml(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
