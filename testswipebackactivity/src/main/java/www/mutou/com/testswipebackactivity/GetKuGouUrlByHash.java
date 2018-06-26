package www.mutou.com.testswipebackactivity;

import android.util.Log;

import com.alibaba.fastjson.JSON;

/**
 * Created by 木头 on 2018/6/26.
 */

public class GetKuGouUrlByHash {
    public String getUrl(String hash){
        String jsonString = null;
        //成功获取网页内容
        try {
            String url = "http://www.kugou.com/yy/index.php?r=play/getdata&hash="+hash;
            jsonString = HtmlService.getHtml(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //接下来进行数据解析

        //使用阿里的fastJson方法
        KuGouInfo kuGouInfo = JSON.parseObject(jsonString, KuGouInfo.class);
        Log.d("", "getUrl: ___>okokok    "+kuGouInfo.getData()[0].getPlay_url());

        return "";
    }
}
