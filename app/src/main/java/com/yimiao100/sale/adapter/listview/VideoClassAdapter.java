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
 * 推广考试视频列表
 * Created by 亿苗通 on 2016/10/21.
 */

public class VideoClassAdapter extends BaseAdapter {

    private final ArrayList<OpenClass> mList;

    public VideoClassAdapter(ArrayList<OpenClass> examClasses) {
        mList = examClasses;
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
        OpenClass examClass = getItem(position);
        //缩略图，时长，是否已经看过，视频标题，视频来源，免费或者送积分，看过多少次，发布时间，是否已考过
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_video_class, null);
        }
        ImageView logo = ViewHolderUtil.get(convertView, R.id.exam_course_logo);
        TextView duration = ViewHolderUtil.get(convertView, R.id.exam_course_duration);
        TextView name = ViewHolderUtil.get(convertView, R.id.exam_course_name);
        TextView vendorName = ViewHolderUtil.get(convertView, R.id.exam_course_vendor_name);
        TextView integralType = ViewHolderUtil.get(convertView, R.id.exam_course_integral_type);
        TextView publishTime = ViewHolderUtil.get(convertView, R.id.exam_course_publish_time);
        TextView count = ViewHolderUtil.get(convertView, R.id.exam_course_count);
        ImageView isExamed = ViewHolderUtil.get(convertView, R.id.exam_course_ex);
        TextView classStatus = ViewHolderUtil.get(convertView, R.id.exam_course_status);

        //课程缩略图
        if (examClass.getImageUrl() != null && examClass.getImageUrl().length() != 0) {
            Picasso.with(parent.getContext()).load(examClass.getImageUrl() +
                    "?imageMogr2/thumbnail/96x86")
                    .placeholder(R.mipmap.ico_default_short_picture).into(logo);
        }
        //课程名称
        name.setText(examClass.getCourseName());
        //课程时长
        duration.setText(TimeUtil.timeStamp2Date(examClass.getVideoDuration() * 1000 + "", "mm:ss"));
        //厂家名字
        vendorName.setText(examClass.getVendorName());
        //积分类型
        switch (examClass.getIntegralType()) {
            case "increase":
                integralType.setText("+" + examClass.getIntegralValue() + "积分");
                break;
            case "decrease":
                integralType.setText("-" + examClass.getIntegralValue() + "积分");
                break;
            case "free":
                integralType.setText("免费");
                break;
        }
        //发布时间
        publishTime.setText(TimeUtil.timeStamp2Date(examClass.getPublishAt() + "", "yyyy-MM-dd"));
        //播放次数
        count.setText(examClass.getPlayCount() + "次");
        //是否已考
        isExamed.setVisibility(examClass.getExamStatus() == 1 ? View.VISIBLE : View.INVISIBLE);
        //是否已经看过
        classStatus.setVisibility(examClass.getExamStatus() == 1 ? View.VISIBLE : View.INVISIBLE);

        return convertView;
    }
}
