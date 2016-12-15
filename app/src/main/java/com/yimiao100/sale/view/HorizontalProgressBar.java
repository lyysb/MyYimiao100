package com.yimiao100.sale.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.yimiao100.sale.R;


/**
 * 自定义横向进度条
 */

public class HorizontalProgressBar extends ProgressBar {

    //设置默认属性值
    private static final int DEFAULT_REACH_COLOR = 0xFF009688;
    private static final int DEFAULT_REACH_HEIGHT = 10;
    private static final int DEFAULT_UN_REACH_COLOR = 0xFFDBDBDB;
    private static final int DEFAULT_UN_REACH_HEIGHT = 10;
    private static final int DEFAULT_TEXT_COLOR = 0xFF757575;
    private static final int DEFAULT_TEXT_SIZE = 12;
    private static final int DEFAULT_TEXT_OFFSET = 10;

    //声明成员变量
    private int mReachColor = DEFAULT_REACH_COLOR;
    private int mReachHeight = dp2px(DEFAULT_REACH_HEIGHT);
    private int mUnReachColor = DEFAULT_UN_REACH_COLOR;
    private int mUnReachHeight = dp2px(DEFAULT_UN_REACH_HEIGHT);
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    private int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);
    private String mText;

    private Paint mPaint = new Paint();
    private int mRealWidth;

    public HorizontalProgressBar(Context context) {
        this(context, null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义属性
        obtainProgressAttr(attrs);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获得宽度大小值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        //获得高度
        int height = measureHeight(heightMeasureSpec);
        //设置宽高
        setMeasuredDimension(width, height);
        //获得实际宽度
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();

        canvas.translate(getPaddingLeft(), getHeight()/2);

        boolean noNeedUnReach = false;

        //draw reachBar
        //测量文字宽度
        int textWidth = (int) mPaint.measureText(mText);
        //计算当前百分比
        float ratio = getProgress() * 1.0f / getMax();
        //计算进度
        float progressX = ratio * mRealWidth;
        //判断是否需要不在需要绘制UnReachBar
        if (progressX + textWidth > mRealWidth) {
            progressX = mRealWidth - textWidth;
            noNeedUnReach = true;
        }
        //计算最终需要绘制到的进度条位置
        float endX = progressX - mTextOffset / 2;
        if (endX > 0) {
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        //draw text
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);
        canvas.drawText(mText, progressX, y, mPaint);

        //draw unReachBar
        if (!noNeedUnReach) {
            //计算起始位置
            float startX = progressX + textWidth + mTextOffset / 2;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(startX, 0 , mRealWidth, 0, mPaint);
        }

        canvas.restore();
    }

    /**
     * 获取自定义属性值
     * @param attrs
     */
    private void obtainProgressAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar);

        mReachColor = typedArray.getColor(R.styleable.HorizontalProgressBar_progressReachColor,
                mReachColor);
        mReachHeight = (int) typedArray.getDimension(R.styleable
                .HorizontalProgressBar_progressReachHeight, mReachHeight);

        mUnReachColor = typedArray.getColor(R.styleable
                .HorizontalProgressBar_progressUnReachColor, mUnReachColor);
        mUnReachHeight = (int) typedArray.getDimension(R.styleable
                .HorizontalProgressBar_progressUnReachHeight, mUnReachHeight);

        mTextColor = typedArray.getColor(R.styleable.HorizontalProgressBar_progressTextColor,
                mTextColor);
        mTextSize = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_progressTextSize,
                mTextSize);
        mTextOffset = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_progressTextOffset,
                mTextOffset);

        mText = typedArray.getString(R.styleable.HorizontalProgressBar_progressText);

        mPaint.setTextSize(mTextSize);
        typedArray.recycle();
    }

    /**
     * 设置文本
     * @param text
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * 获得控件高度
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            //如果是指定的精确值match_parent/??dp
            result = size;
        } else {
            //计算文字高度
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingBottom() + getPaddingTop() + Math.max(Math.max(mReachHeight,
                    mUnReachHeight), Math.abs(textHeight));
            if (mode == MeasureSpec.AT_MOST) {
                //如果是warp_content
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * dp转px
     * @param dpVal
     * @return
     */
    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources()
                .getDisplayMetrics());
    }

    private int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources()
                .getDisplayMetrics());
    }
}
