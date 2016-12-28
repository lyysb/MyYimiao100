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
 * 公开课Adapter
 * Created by 亿苗通 on 2016/10/21.
 */

public class PublicClassAdapter extends BaseAdapter {

    private final ArrayList<OpenClass> mList;

    public PublicClassAdapter(ArrayList<OpenClass> openClasses) {
        mList =  openClasses;
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
        final OpenClass openClass = getItem(position);
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_public_class, null);
        }

        ImageView frame = ViewHolderUtil.get(convertView, R.id.open_class_sample);
        TextView className = ViewHolderUtil.get(convertView, R.id.open_class_name);
        TextView classStatus = ViewHolderUtil.get(convertView, R.id.open_class_status);
        TextView classDuration = ViewHolderUtil.get(convertView, R.id.open_class_duration);
        TextView vendorName = ViewHolderUtil.get(convertView, R.id.open_class_vendor_name);
        TextView playCount = ViewHolderUtil.get(convertView, R.id.public_play_count);
        TextView publishTime = ViewHolderUtil.get(convertView, R.id.public_publish_time);
        TextView integralType = ViewHolderUtil.get(convertView, R.id.public_integral_type);

        //视频缩略图
        if (openClass.getImageUrl() != null && !openClass.getImageUrl().isEmpty()) {
            Picasso.with(parent.getContext()).load(openClass.getImageUrl() +
                    "?imageMogr2/thumbnail/240x215").placeholder(R.mipmap
                    .ico_default_short_picture).into(frame);
        }
        //课程名字
        className.setText(openClass.getCourseName());
        //视频时长
        classDuration.setText(TimeUtil.timeStamp2Date(openClass.getVideoDuration() * 1000 +"", "mm:ss"));

        //视频来源
        vendorName.setText("来源：" + openClass.getVendorName());
        //播放次数
        playCount.setText(openClass.getPlayCount() + "次");
        //发布时间
        publishTime.setText(TimeUtil.timeStamp2Date(openClass.getPublishAt() + "", "yyyy-MM-dd"));
        //积分类型
        switch (openClass.getIntegralType()) {
            case "increase":
                integralType.setText("+" + openClass.getIntegralValue() + "积分");
                break;
            case "decrease":
                integralType.setText("-" + openClass.getIntegralValue() + "积分");
                break;
            case "free":
                integralType.setText("免费");
                break;
        }
        //是否已看过:参与过积分运算为已看过；没参与过为没看过
        classStatus.setVisibility(openClass.getIntegralStatus() == 1 ? View.VISIBLE : View.INVISIBLE);
        return convertView;
    }
}
