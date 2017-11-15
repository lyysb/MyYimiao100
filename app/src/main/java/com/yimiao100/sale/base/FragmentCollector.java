package com.yimiao100.sale.base;

import android.support.v4.app.Fragment;

/**
 * Created by michel on 2017/10/16.
 */
public class FragmentCollector {
    private static BaseFragment topFragment;

    public static BaseFragment getTopFragment() {
        return topFragment;
    }

    public static void setTopFragment(BaseFragment fragment) {
        topFragment = fragment;
    }
}
