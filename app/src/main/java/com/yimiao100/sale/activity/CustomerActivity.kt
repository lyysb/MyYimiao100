package com.yimiao100.sale.activity

import com.yimiao100.sale.base.BaseActivityWithTab
import com.yimiao100.sale.base.BaseFragmentSingleList
import com.yimiao100.sale.fragment.CustomerAll
import com.yimiao100.sale.fragment.CustomerPersonal

/**
 * 客户界面
 */
class CustomerActivity : BaseActivityWithTab() {
    override fun title(): String? {
        return "客户"
    }

    override fun setFragments(): ArrayList<BaseFragmentSingleList>? {
        var list = ArrayList<BaseFragmentSingleList>()
        list.add(CustomerAll())
        list.add(CustomerPersonal())
        return list
    }
}
