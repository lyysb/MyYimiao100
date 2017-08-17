package com.yimiao100.sale.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ScreenUtil;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * 自定义视频播放器，可以实现监听视频播放完成
 * Created by Michel on 2016/10/18.
 */
public class YMVideoPlayer extends JCVideoPlayerStandard {

    private OnCompleteListener mListener;
    private OnPlayListener mPlayListener;

    public YMVideoPlayer(Context context) {
        super(context);
    }

    public YMVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        mListener = listener;
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        if (mListener != null) {
            LogUtil.d("视频播放完成");
            mListener.onComplete();
        }
    }

    /**
     * 播放视频
     */
    public void playVideo() {
        startVideo();
    }

    /**
     * 设置播放链接
     * @param url
     * @param screen
     * @param objects
     */
    public void setURL(String url, int screen, Object... objects) {
        setUp(url != null ? url : Constant.DEFAULT_VIDEO, screen, objects);
    }

    /**
     * 设置封面图
     * @param context
     * @param imageUrl
     */
    public void setScreen(Context context, String imageUrl) {
        Picasso.with(context).load(imageUrl + "?imageMogr2/thumbnail/480x240")
                .resize(ScreenUtil.getScreenWidth(context), DensityUtil.dp2px(context, 200))
                .into(this.thumbImageView);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (currentState == JCVideoPlayerStandard.CURRENT_STATE_NORMAL || currentState ==
                JCVideoPlayerStandard.CURRENT_STATE_PREPARING) {
            if (mPlayListener != null) {
                mPlayListener.onPlay();
            }
        }
    }
    public interface OnPlayListener {
        void onPlay();
    }
    public void setOnPlayListener(OnPlayListener listener) {
        mPlayListener = listener;
    }
}
