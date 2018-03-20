package com.yimiao100.sale.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yimiao100.sale.R;

/**
 * 资源列表底部多选
 * Created by Michel on 2018/3/9.
 */

public class SelectAllView extends RelativeLayout {

    private TextView selectCount;
    private CheckBox selectAll;
    private OnSelectAllClickListener onSelectAllClickListener;
    private OnConfirmClickListener onConfirmClickListener;

    public SelectAllView(Context context) {
        this(context, null);
    }

    public SelectAllView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SelectAllView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.select_all, this);
        // todo: 自定义属性的设置
        selectAll = (CheckBox) findViewById(R.id.select_all);
        selectAll.setOnClickListener(v -> {
            // 接口暴露到外部的全选点击事件
            if (onSelectAllClickListener != null) {
                // 是否被选中，交由外部处理,so 给我反选回去！
                selectAll.setChecked(!selectAll.isChecked());
                onSelectAllClickListener.onSelectAllClick();
            }
        });
        selectCount = (TextView) findViewById(R.id.select_all_count);
        Button confirm = (Button) findViewById(R.id.select_all_confirm);
        confirm.setOnClickListener(v -> {
            // 接口暴露到外部的确定点击事件
            if (onConfirmClickListener != null) {
                onConfirmClickListener.onConfirmClick();
            }
        });
    }

    /**
     * @return 全选的选中状态
     */
    public boolean isSelectAll() {
        return selectAll.isChecked();
    }

    /**
     * 全选
     */
    public void selectAll() {
        selectAll.setChecked(true);
    }

    /**
     * 取消全选
     */
    public void deselectAll() {
        selectAll.setChecked(false);
    }

    /**
     * 更新统计文本
     * @param msg
     */
    public void updateSelectCount(String msg) {
        selectCount.setText(msg);
    }

    public void setOnSelectAllClickListener(OnSelectAllClickListener onSelectAllClickListener) {
        this.onSelectAllClickListener = onSelectAllClickListener;
    }

    public interface OnSelectAllClickListener{
        void onSelectAllClick();
    }

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }

    public interface OnConfirmClickListener{
        void onConfirmClick();
    }
}
