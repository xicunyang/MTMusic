package www.mutou.com.testswipebackactivity;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by 在旭 on 2018/3/4.
 */
public class HtmlService {
    private static final String TAG="HtmlService";
    public static String getHtml(String path){
        //将path转换为URL格式
        URL url = null;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getHtml: yxc1");
        //打开Http链接
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getHtml: yxc2");
        //设置请求方式为GET
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getHtml: yxc3");
        //设置连接超时时间
        conn.setConnectTimeout(5 * 1000);
        Log.d(TAG, "getHtml: yxc4");

        //通过conn获取输入流(字节流)---inStream
        InputStream inStream = null;
        try {
            Log.d(TAG, "getHtml: yxc42");
            inStream = conn.getInputStream();
        } catch (IOException e) {

            Log.d(TAG, "getHtml: yxc444444");
        }
        Log.d(TAG, "getHtml: yxc5");
        //将readInputStream返回的字节数组存入data中
        byte[] data = new byte[0];
        try {
            data = readInputStream(inStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getHtml: yxc6");
        //通过new String的方法。并设置utf-8的编码方式，转换为String类型
        String html = null;
        try {
            html = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getHtml: yxc7");
        //将获取到的String类型的html返回
        Log.d("", "getHtml: yxc_8");
        return html;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception{
        //转换成字节数组
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //新建缓冲字节数组
        byte[] buffer = new byte[1024];
        int len = 0;
        /*

        JDK解释：read(Byte []b)一个参数
        从输入流中读取一定量的字节，将其存入缓冲区数组buffer中
        以整数形式返回其实际读取的字节数。
        如果b的长度为0，或者不读取任何字节，则返回为0；
        否则，尝试读取至少一个字节。
        如果，流位于末尾而没有可用字节，则返回为-1；
        否则，至少读取一个字节并将其存储在 b 中。
        将读取的第一个字节存入buffer[0]中，
        下一个存入buffer[1]中，以此类推。
        读取的字节数最多等于buffer的长度。
         */
        while( (len=inStream.read(buffer)) != -1 ){
            //每次读取8个字节作为一次循环
            //将这8个字节存入的数组buffer作为参数
            //写入outStream中
            //将指定的byte数组从偏移量off开始的len个字节写入此输出流
            outStream.write(buffer, 0, len);
        }
        //字节流操作之后，将资源关闭
        inStream.close();
        //将获得的outStream转换为字节数组，并返回
        return outStream.toByteArray();
    }
}
