package com.yimiao100.sale.vaccine

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.yimiao100.sale.R
import com.yimiao100.sale.adapter.fragment.OverduePagerAdapter
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.base.BaseFragment
import com.yimiao100.sale.fragment.OverdueCorFragment
import com.yimiao100.sale.fragment.OverduePerFragment
import com.yimiao100.sale.view.TitleView
import org.jetbrains.anko.find

class OverdueActivity : BaseActivity(), TitleView.TitleBarOnClickListener {

    lateinit var title: TitleView
    lateinit var viewPager: ViewPager
    lateinit var tabs: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overdue)

        initView()
    }

    private fun initView() {
        initTitle()

        initViewPager()

        initTabs()
    }

    private fun initTitle() {
        title = find(R.id.overdue_tab_title)
        title.setTitle("逾期垫款提现")
        title.setOnTitleBarClick(this)
    }

    private fun initViewPager() {
        viewPager = find(R.id.overdue_tab_view_pager)
        val fragments = ArrayList<BaseFragment>()
        fragments.add(OverdueCorFragment())
        fragments.add(OverduePerFragment())
        viewPager.adapter = OverduePagerAdapter(supportFragmentManager, fragments)
    }

    private fun initTabs() {
        tabs = find(R.id.overdue_tab_tabs)
        tabs.setupWithViewPager(viewPager)
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }

}
