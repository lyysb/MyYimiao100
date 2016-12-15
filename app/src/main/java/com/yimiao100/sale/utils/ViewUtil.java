package com.yimiao100.sale.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.yimiao100.sale.R;



/**
 * View的工具类
 * Created by 亿苗通 on 2016/9/28.
 */
public class ViewUtil {
    private ViewUtil() {
         /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 给ListView设置数据为空的界面
     * @param activity
     * @param listView
     */
    public static void  setListEmptyView(Activity activity, ListView listView){
        View empty = LayoutInflater.from(activity).inflate(R.layout.empty_view, null);
        empty.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        empty.setVisibility(View.GONE);
        ((ViewGroup)listView.getParent()).addView(empty);
        listView.setEmptyView(empty);
    }

}
