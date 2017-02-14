package com.yimiao100.sale.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Michel on 2017/2/13.
 * ListView in ScrollView
 */

public class ScrollListView extends ListView {
    public ScrollListView(Context context) {
        this(context, null);
    }

    public ScrollListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec
                .AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
