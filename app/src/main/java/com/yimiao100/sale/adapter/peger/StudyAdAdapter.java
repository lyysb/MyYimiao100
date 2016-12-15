package com.yimiao100.sale.adapter.peger;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.ScreenUtil;

import java.util.ArrayList;

/**
 * 学习界面的视频轮播图
 * Created by 亿苗通 on 2016/10/21.
 */

public class StudyAdAdapter extends PagerAdapter {

    private final ArrayList<Carousel> mCarouselList;
    private OnClickListener mListener;

    public StudyAdAdapter(ArrayList<Carousel> carouselList) {
        mCarouselList = carouselList;
    }

    @Override
    public int getCount() {
        return mCarouselList.size() * 1000 * 100;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position = position % mCarouselList.size();
        ImageView imageView = new ImageView(container.getContext());
        String url = mCarouselList.get(position).getMediaUrl() + "?vframe/png/offset/10/w/480/h/240/";
        Picasso.with(container.getContext()).load(url).placeholder(R.mipmap.ico_default_bannner)
                .resize(ScreenUtil.getScreenWidth(container.getContext()),
                        DensityUtil.dp2px(container.getContext(), 190))
                .into(imageView);
        //对外暴露点击事件
        final int finalPosition = position;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(finalPosition);
            }
        });
        container.addView(imageView);
        return imageView;
    }

    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }

    public interface OnClickListener {
        void onClick(int position);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
