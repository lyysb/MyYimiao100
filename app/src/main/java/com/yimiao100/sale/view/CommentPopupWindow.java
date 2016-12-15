package com.yimiao100.sale.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.yimiao100.sale.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 发表评论的popupWindow
 * Created by 亿苗通 on 2016/8/15.
 */
public class CommentPopupWindow extends PopupWindow {
    //代表这整个PopupWindow视图
    private View mPopupView;
    private Activity context;//传入一个Activity的context;
    //发表
    private ImageView submit_comment;
    //输入框
    private EditText et_comment_content;

    private OnPopupWindowClickListener mOnPopupWindowClickListener;

    public CommentPopupWindow(final Activity context) {
        super(context);
        this.context = context;
        //取得xml里面定义的view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopupView = inflater.inflate(R.layout.dialog_comment_input, null);

        et_comment_content = (EditText) mPopupView.findViewById(R.id.et_comment_content);

        submit_comment = (ImageView) mPopupView.findViewById(R.id.submit_comment);


        submit_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPopupWindowClickListener != null) {
                    mOnPopupWindowClickListener.OnPopupWindowClick(et_comment_content);
                    dismiss();
                }
            }
        });


        //初始化PopupWindow时改变背景灰度
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 0.5f;
        context.getWindow().setAttributes(lp);//改变背景颜色

        /**
         * ********************PopupWindow相关属性*********************
         */
        //设置PopupWindow主视图
        this.setContentView(mPopupView);
        //设置PopouWindow弹出窗体的宽度,有三个属性（FILL_PARENT/WRAP_CONTENT/MATCH_PARENT）
        this.setWidth(ActionBar.LayoutParams.FILL_PARENT);
        //设置PopupWindow弹出窗体的高度,属性同上
        this.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
        //设置PopupWindow弹出窗体是否可点击
        this.setFocusable(true);
        //设置PopupWindow弹出动画效果
//        this.setAnimationStyle(R.style.SharePopupAniamtion);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置PopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //设置PopuoWindow弹出窗体需要软键盘
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        //设置弹出的软键盘大少和弹出框一样，覆盖尺寸，不会着PopupWindow
        //属性有[SOFT_INPUT_ADJUST_PAN]:覆盖在PopuWindow上、  [SOFT_INPUT_ADJUST_RESIZE]:总在PopuWindow下，不会挡着。若不需要键盘显示则不用设置
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        et_comment_content.setFocusableInTouchMode(true);//输入框允许点击
        et_comment_content.setFocusable(true);//输入框获取焦点
        et_comment_content.requestFocus();//启动输入框焦点

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
                           public void run() {
                               showKeyBoard();
                           }
                       },
                60);
    }

    public interface OnPopupWindowClickListener {
        void OnPopupWindowClick(EditText comment_content);
    }

    public void setOnPopupWindowClickListener(OnPopupWindowClickListener listener) {
        mOnPopupWindowClickListener = listener;
    }

    //隐藏虚拟键
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) et_comment_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_comment_content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //显示虚拟键盘
    public void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) et_comment_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_comment_content, InputMethodManager.SHOW_IMPLICIT);
    }

    //PopupWindow窗体的销毁
    @Override
    public void dismiss() {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 1f;
        context.getWindow().setAttributes(lp);//改变背景颜色
        hideKeyBoard();
        super.dismiss();
    }
}
