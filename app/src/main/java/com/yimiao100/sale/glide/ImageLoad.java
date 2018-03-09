package com.yimiao100.sale.glide;

import android.content.Context;
import android.widget.ImageView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.ScreenUtil;

/**
 * 简单封装图片加载
 * Created by Michel on 2018/3/9.
 */

public class ImageLoad {

    public static void loadAd(Context context, String url, int height, ImageView imageView) {
        GlideApp.with(context)
                .load(url)
                .placeholder(R.mipmap.ico_default_bannner)
                .override(ScreenUtil.getScreenWidth(context), DensityUtil.dp2px(context, height))
                .into(imageView);
    }
}
