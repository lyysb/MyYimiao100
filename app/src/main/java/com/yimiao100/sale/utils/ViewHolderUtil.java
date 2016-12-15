package com.yimiao100.sale.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by 亿苗通 on 2016/8/11.
 */
public class ViewHolderUtil {
    private ViewHolderUtil(){
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

}
