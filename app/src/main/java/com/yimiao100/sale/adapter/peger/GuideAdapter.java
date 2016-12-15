package com.yimiao100.sale.adapter.peger;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 引导页Adapter
 * Created by 亿苗通 on 2016/12/2.
 */

public class GuideAdapter extends PagerAdapter {

    private final int[] mGuideIds;
    private onPagerClickListener mListener;

    public GuideAdapter(int[] guideIds) {
        mGuideIds = guideIds;
    }

    @Override
    public int getCount() {
        return mGuideIds.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setBackgroundResource(mGuideIds[position]);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });
        container.addView(imageView);
        return imageView;
    }

    public void setOnPagerClickListener(onPagerClickListener listener) {
        mListener = listener;
    }

    public interface onPagerClickListener {
        void onClick();
    }

}
