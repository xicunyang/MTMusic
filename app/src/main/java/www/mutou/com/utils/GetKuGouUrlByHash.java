package www.mutou.com.utils;
import com.alibaba.fastjson.JSON;
import www.mutou.com.model.KuGouInfo;
import www.mutou.com.service.HtmlService;

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

        //使用阿里的fastJson方法
        KuGouInfo kuGouInfo = JSON.parseObject(jsonString, KuGouInfo.class);
        return kuGouInfo.getData()[0].getPlay_url();
    }
}
