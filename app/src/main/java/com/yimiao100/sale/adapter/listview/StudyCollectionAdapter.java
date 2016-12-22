package com.yimiao100.sale.adapter.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.OpenClass;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;


/**
 * 学习-我的收藏
 * Created by 亿苗通 on 2016/10/21.
 */

public class StudyCollectionAdapter extends BaseAdapter {

    private final ArrayList<OpenClass> mList;

    public StudyCollectionAdapter(ArrayList<OpenClass> collectClasses) {
        mList = collectClasses;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public OpenClass getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OpenClass collectClass = getItem(position);
        //缩略图，时长，是否已经看过，视频标题，视频来源，免费或者送积分，看过多少次，发布时间，是否已考过
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_study_collection, null);
        }
        ImageView frame = ViewHolderUtil.get(convertView, R.id.course_collect_logo);
        TextView name = ViewHolderUtil.get(convertView, R.id.course_collect_name);
        TextView vendorName = ViewHolderUtil.get(convertView, R.id.course_collect_vendor_name);
        TextView integralType = ViewHolderUtil.get(convertView, R.id.course_collect_integral_type);
        TextView count = ViewHolderUtil.get(convertView, R.id.course_collect_count);
        TextView publishAt = ViewHolderUtil.get(convertView, R.id.course_collect_publish_time);
        ImageView isExamed = ViewHolderUtil.get(convertView, R.id.course_collect_ex);
        TextView duration = ViewHolderUtil.get(convertView, R.id.course_collect_duration);

        //视频缩略图
        if (collectClass.getImageUrl() != null && !collectClass.getImageUrl().isEmpty()) {
            Picasso.with(parent.getContext()).load(collectClass.getImageUrl()
                    + "?imageMogr2/thumbnail/96x86").placeholder(R.mipmap.ico_default_short_picture)
                    .into(frame);
        }
        //课程名称
        name.setText(collectClass.getCourseName());
        //课程时长
        duration.setText(TimeUtil.timeStamp2Date(collectClass.getVideoDuration() * 1000 + "", "mm:ss"));
        //课程来源
        vendorName.setText("来源：" + collectClass.getVendorName());
        //播放次数
        count.setText(collectClass.getPlayCount() + "次");
        //发布时间
        publishAt.setText(TimeUtil.timeStamp2Date(collectClass.getPublishAt() + "", "yyyy-MM-dd"));
        //积分类型
        switch (collectClass.getIntegralType()) {
            case "increase":
                integralType.setText("+" + collectClass.getIntegralValue() + "积分");
                break;
            case "decrease":
                integralType.setText("-" + collectClass.getIntegralValue() + "积分");
                break;
            case "free":
                integralType.setText("免费");
                break;
        }
        //是否已经参与过考试
        isExamed.setVisibility(collectClass.getExamStatus() == 1 ? View.VISIBLE : View.INVISIBLE);

        return convertView;
    }
}
