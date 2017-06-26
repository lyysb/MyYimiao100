package com.yimiao100.sale.adapter.peger

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.yimiao100.sale.base.BaseFragment

/**
 * Created by Michel on 2017/6/12.
 */
class MainPagerAdapter(fm: FragmentManager, var fragments: ArrayList<BaseFragment>): FragmentPagerAdapter(fm) {

    override fun getCount(): Int = fragments.size

    override fun getItem(position: Int): Fragment = fragments[position]
}