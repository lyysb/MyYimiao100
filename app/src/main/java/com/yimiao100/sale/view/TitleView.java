package com.yimiao100.sale.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yimiao100.sale.R;

/**
 * 自定义复用Title
 * Created by 亿苗通 on 2016/8/2.
 */
public class TitleView extends RelativeLayout {
    private final Context mContext;
    ImageView mTitleReturn;                 //左侧按钮
    TextView mTitleTitleText;               //标题
    TextView mTitleRightText;               //右侧文字
    RelativeLayout mTitleBar;               //标题栏

    private Drawable mTitle_background;     //标题栏背景
    private boolean mLeft_isShow;           //左侧控件是否显示
    private String mTitle_text;             //标题文字内容
    private boolean mRight_isShow;          //右侧文字是否显示
    private String mTitle_right;            //右侧文字内容

    //生命左右被点击监听
    private TitleBarOnClickListener mListener;




    /**
     * 创建一个监听左右侧被点击的接口
     */
    public interface TitleBarOnClickListener {
        void leftOnClick();  //左侧点击事件

        void rightOnClick(); //右侧点击事件
    }

    public void setOnTitleBarClick(TitleBarOnClickListener listener) {
        this.mListener = listener;
    }

    public void setTitle(String Title){
        mTitleTitleText.setText(Title);
    }
    public void setRight(String text){
        mTitleRightText.setText(text);
    }


    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        //加载布局
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initView(Context context, AttributeSet attrs) {
        //向ViewGroup中填充View
        View.inflate(context, R.layout.title, this);
        mTitleBar = (RelativeLayout) findViewById(R.id.title_bar);
        mTitleReturn = (ImageView) findViewById(R.id.title_return);
        mTitleTitleText = (TextView) findViewById(R.id.title_titleText);
        mTitleRightText = (TextView) findViewById(R.id.title_rightText);

        //获取自定义的属性的值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView);

        //获取的属性的值
        //标题栏背景
        mTitle_background = typedArray.getDrawable(R.styleable.TitleView_title_background);
        //左侧图标是否显示
        mLeft_isShow = typedArray.getBoolean(R.styleable.TitleView_left_isShow, false);
        //标题文字内容
        mTitle_text = typedArray.getString(R.styleable.TitleView_title_text);
        //右侧文字内容
        mTitle_right = typedArray.getString(R.styleable.TitleView_title_right);
        //右侧文字是否显示
        mRight_isShow = typedArray.getBoolean(R.styleable.TitleView_right_isShow, false);
        // recycle()可以重用 效率会高
        typedArray.recycle();

        //设置标题栏背景
        mTitleBar.setBackground(mTitle_background);
        //设置左侧图标可见性
        mTitleReturn.setVisibility(mLeft_isShow ? View.VISIBLE : View.INVISIBLE);
        //设置标题文字
        mTitleTitleText.setText(mTitle_text);
        //设置右侧文字
        mTitleRightText.setText(mTitle_right);
        //设置右侧文字可见性
        mTitleRightText.setVisibility(mRight_isShow ? View.VISIBLE : View.INVISIBLE);

        //回调左侧按钮的监听事件
        mTitleReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.leftOnClick();
                }
            }
        });

        //回调右侧文字的监听事件
        mTitleRightText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.rightOnClick();
                }
            }
        });
    }


    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

}
