package com.yimiao100.sale.adapter.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 学习任务Adapter
 * Created by 亿苗通 on 2016/10/24.
 */

public class StudyTaskAdapter extends FragmentPagerAdapter {
    private final ArrayList<Fragment> mFragments;
    private String[] mTitles = {
            "已完成",
            "未完成"
    };

    public StudyTaskAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
