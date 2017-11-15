package com.yimiao100.sale.adapter.fragment

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.yimiao100.sale.base.BaseFragment

/**
 * 逾期垫款提现Fragment
 * Created by michel on 2017/10/31.
 */
class OverduePagerAdapter(
        fm: FragmentManager?,
        private val fragments: ArrayList<BaseFragment>
) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "公司推广"
            else -> return "个人推广"
        }
    }
}