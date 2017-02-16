package com.yimiao100.sale.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * FragmentPagerAdapter基类
 * Created by Michel on 2017/2/15.
 */

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<BaseFragmentSingleList> mFragments;

    public BaseFragmentPagerAdapter(FragmentManager fm, ArrayList<BaseFragmentSingleList> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments != null ? mFragments.get(position) : null;
    }

    @Override
    public int getCount() {
        return mFragments != null ? mFragments.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragments != null ? mFragments.get(position).getPageTitle() : "unKnown";
    }
}
