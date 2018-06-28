package www.mutou.com.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.List;

import www.mutou.com.application.MyApplication;
import www.mutou.com.model.KuWoInfo;
import www.mutou.com.mtmusic.R;

import static android.content.ContentValues.TAG;

/**
 * Created by 木头 on 2018/6/25.
 */

public class AdapterUrlListView_Kuwo extends BaseAdapter{
    Context mContext;
    List<KuWoInfo> kuWoInfos;
    public AdapterUrlListView_Kuwo(Context mContext, List<KuWoInfo> kuWoInfos){
        this.mContext = mContext;
        this.kuWoInfos = kuWoInfos;
    }
    @Override
    public int getCount() {
        if(kuWoInfos!=null){
            return kuWoInfos.get(0).getAbslist().length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
/*
            使用ViewHolder的好处就是要想使用 ListView 就需要编写一个 Adapter 将数据适配到 ListView上，
            而为了节省资源提高运行效率，一般自定义类 ViewHolder
            来减少 findViewById() 的使用以及避免过多地 inflate view，从而实现目标。
         */
        ViewHolder viewHolder = null;
        //如果此条目是空的---就新建
        if(convertView==null){
            //新建条目
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_url_main_item,null);
            //新建一个Holder存条目
            viewHolder = new ViewHolder();
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.url_detail_title);
            viewHolder.iv_detail_mv = (ImageView) convertView.findViewById(R.id.url_detail_mv);
            viewHolder.iv_hq = (ImageView) convertView.findViewById(R.id.url_detail_hq);
            viewHolder.tv_singerAlum = (TextView) convertView.findViewById(R.id.url_detail_singerAlum);
            viewHolder.iv_playing_flag = (ImageView) convertView.findViewById(R.id.url_detail_playing_flag);
            viewHolder.tv_id = (TextView) convertView.findViewById(R.id.url_detail_id);
            viewHolder.tv_url = (TextView) convertView.findViewById(R.id.url_detail_url);
            viewHolder.tv_who = (TextView) convertView.findViewById(R.id.url_detail_who);
            viewHolder.iv_mvUrl = (TextView) convertView.findViewById(R.id.url_detail_mvUrl);
            //将viewHolder存入convertView中
            convertView.setTag(viewHolder);
        }
        //此条目被新建过---划下去又划上来了---此时就不用重新建条目了
        else{
            //直接获取到条目
            viewHolder = (ViewHolder) convertView.getTag();
        }


        //以上解决之后---开始设置值
        viewHolder.tv_title.setText(kuWoInfos.get(0).getAbslist()[position].getSONGNAME());

        viewHolder.tv_singerAlum.setText(kuWoInfos.get(0).getAbslist()[position].getARTIST()+"-"
                +kuWoInfos.get(0).getAbslist()[position].getALBUM());

        viewHolder.iv_hq.setVisibility(View.VISIBLE);



        //这个position不是我想的position---而是当前视图内的position
        if(MyApplication.nowUrlPosition == position){
            viewHolder.iv_playing_flag.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.iv_playing_flag.setVisibility(View.GONE);
        }

        viewHolder.tv_url.setText(kuWoInfos.get(0).getAbslist()[position].getMP3RID());
        viewHolder.tv_who.setText("kw");
        viewHolder.iv_mvUrl.setText(kuWoInfos.get(0).getAbslist()[position].getMp4sig1());
        Log.e(TAG, "getView: "+kuWoInfos.get(0).getAbslist()[position].getSONGNAME()
        +"   "+kuWoInfos.get(0).getAbslist()[position].getMp4sig1());
        if(!kuWoInfos.get(0).getAbslist()[position].getMp4sig1().equals("0")){
            viewHolder.iv_detail_mv.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.iv_detail_mv.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder{
        TextView tv_title;
        ImageView iv_detail_mv;
        ImageView iv_hq;
        TextView tv_singerAlum;
        ImageView iv_playing_flag;
        TextView tv_id;
        TextView tv_url;
        TextView tv_who;
        TextView iv_mvUrl;
    }
}




























