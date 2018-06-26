package www.mutou.com.utils;

import www.mutou.com.service.HtmlService;

/**
 * Created by 木头 on 2018/6/26.
 */

public class GetKuWoUrlByID {
    public String getKWMp3Url(String mp3id) {
        String id = mp3id.substring(4, mp3id.length());
        String result = "";
        String url = "http://antiserver.kuwo.cn/anti.s?type=convert_url&rid=MUSIC_" + id + "&format=mp3&response=url";
        try {
            result = HtmlService.getHtml(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
