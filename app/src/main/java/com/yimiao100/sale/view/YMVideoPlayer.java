package com.yimiao100.sale.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

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
    public void onCompletion() {
        super.onCompletion();
        if (mListener != null) {
            mListener.onComplete();
        }
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
