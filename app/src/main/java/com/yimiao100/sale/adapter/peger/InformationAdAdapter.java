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
 * 资讯-广告轮播图
 * Created by 亿苗通 on 2016/9/18.
 */
public class InformationAdAdapter extends PagerAdapter{

    private final ArrayList<Carousel> mList;
    private OnAdClickListener mListener;

    public InformationAdAdapter(ArrayList<Carousel> carouselList) {
        mList = carouselList;
    }


    @Override
    public int getCount() {
        return mList.size() * 10000 * 100;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position = position % mList.size();
        ImageView imageView = new ImageView(container.getContext());
        String imageUrl = mList.get(position).getMediaUrl();
        Picasso.with(container.getContext()).load(imageUrl).placeholder(R.mipmap.ico_default_bannner)
                    .resize(ScreenUtil.getScreenWidth(container.getContext()), DensityUtil.dp2px(container.getContext(), 190))
                    .into(imageView);
        final int finalPosition = position;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAdClick(finalPosition);
                }
            }
        });
        container.addView(imageView);
        return imageView;
    }

    public void setOnAdClickListener(OnAdClickListener listener) {
        mListener = listener;
    }
    public interface OnAdClickListener{
        void onAdClick(int position);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
