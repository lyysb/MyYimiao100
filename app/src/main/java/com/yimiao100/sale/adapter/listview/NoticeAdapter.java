package com.yimiao100.sale.adapter.listview;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.NoticedListBean;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.List;

/**
 *
 * 通知列表Adapter
 * Created by 亿苗通 on 2016/8/15.
 */
public class NoticeAdapter extends BaseAdapter {

    private Context mContext;
    private List<NoticedListBean> mNoticedList;

    private final int TYPE_LOW = 0;
    private final int TYPE_MIDDLE = 1;
    private final int TYPE_HIGH = 2;

    public NoticeAdapter(Context context, List<NoticedListBean> pagedList){
        mContext = context;
        mNoticedList = pagedList;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (mNoticedList != null){
            String noticeLevel = mNoticedList.get(position).getNoticeLevel();
            switch (noticeLevel){
                case "low":
                    return TYPE_LOW;
                case "middle":
                    return TYPE_MIDDLE;
                case "high":
                    return TYPE_HIGH;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return mNoticedList != null ? mNoticedList.size() : 0 ;
    }

    @Override
    public NoticedListBean getItem(int position) {
        return mNoticedList != null ? mNoticedList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NoticedListBean notice = getItem(position);
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.item_notice, null);
        }
        ImageView notice_level = ViewHolderUtil.get(convertView, R.id.notice_level);
        TextView notice_list_title = ViewHolderUtil.get(convertView, R.id.notice_list_title);
        TextView notice_created_at = ViewHolderUtil.get(convertView, R.id.notice_created_at);
        ImageView notice_status = ViewHolderUtil.get(convertView, R.id.notice_status);
        LinearLayout ll_notice_list = ViewHolderUtil.get(convertView, R.id.ll_notice_list);
        TextView tvAccountType = ViewHolderUtil.get(convertView, R.id.notice_account_type);

        //设置通知标题
        notice_list_title.setText(notice.getNoticeSource() + "：" + notice.getNoticeTitle());
        //设置通知时间
        notice_created_at.setText(TimeUtil.timeStamp2Date(notice.getCreatedAt() + "", "yyyy年MM月dd日 HH:mm:ss"));
        //设置通知级别
        String noticeLevel = notice.getNoticeLevel();
        switch (noticeLevel){
            case "low":
                notice_level.setImageResource(R.mipmap.ico_notice_blue);
                break;
            case "middle":
                notice_level.setImageResource(R.mipmap.ico_notice_orange);
                break;
            case "high":
                notice_level.setImageResource(R.mipmap.ico_notice_red);
                break;
        }
        if (notice.getAccountType() != null) {
            switch (notice.getAccountType()) {
                case "corporate":
                    tvAccountType.setVisibility(View.VISIBLE);
                    tvAccountType.setText("对公");
                    tvAccountType.setTextColor(mContext.getResources().getColor(R.color.colorOrigin));
                    tvAccountType.setBackgroundResource(R.mipmap.ico_notice_company);
                    break;
                case "personal":
                    tvAccountType.setText("个人");
                    tvAccountType.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                    tvAccountType.setBackgroundResource(R.mipmap.ico_notice_personal);
                    tvAccountType.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            tvAccountType.setVisibility(View.GONE);
        }
        //设置阅读状态
        int readStatus = notice.getReadStatus();
        if (readStatus == 1){
            //已阅读
            notice_list_title.setTextColor(mContext.getResources().getColor(R.color.color999));
            notice_created_at.setTextColor(mContext.getResources().getColor(R.color.color999));
            notice_level.setImageResource(R.mipmap.ico_notice_gray);
            notice_status.setImageResource(R.mipmap.ico_notice_triangle_default);
//            ll_notice_list.setBackgroundColor(mContext.getResources().getColor(R.color.colorMine));
            ll_notice_list.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }
        return convertView;
    }
}
