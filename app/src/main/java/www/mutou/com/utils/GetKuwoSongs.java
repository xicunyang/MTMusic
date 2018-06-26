package www.mutou.com.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.List;

import www.mutou.com.model.KuWoInfo;
import www.mutou.com.service.HtmlService;

/**
 * Created by 木头 on 2018/6/26.
 */

public class GetKuwoSongs {
    public List<KuWoInfo> getSongs(String searchMsg){
        List<KuWoInfo> list;
        String jsonString = null;
        //成功获取网页内容
        try {
            String url = "http://search.kuwo.cn/r.s?all="+searchMsg+"&ft=music&itemset=web_2013&client=kt&pn=0&rn=120&rformat=json&encoding=utf8";
            jsonString = HtmlService.getHtml(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //接下来进行数据解析
        jsonString = jsonString.replace("\\&", "&");
        jsonString = jsonString.replace("&nbsp;", " ");
        jsonString = "[" + jsonString + "]";


        //使用阿里的fastJson方法
        list = JSON.parseArray(jsonString,KuWoInfo.class);
        return list;
    }
}
