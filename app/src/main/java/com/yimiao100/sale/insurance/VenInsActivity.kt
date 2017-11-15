package com.yimiao100.sale.insurance

import com.yimiao100.sale.base.BaseActivityWithTab
import com.yimiao100.sale.base.BaseFragmentSingleList
import com.yimiao100.sale.fragment.VendorPersonalFragment
import java.util.ArrayList

class VenInsActivity : BaseActivityWithTab() {

    override fun title(): String = "厂家列表"

    override fun setFragments(): ArrayList<BaseFragmentSingleList> {
        val lists = ArrayList<BaseFragmentSingleList>()
        lists.add(VenInsCorFragment())
        lists.add(VenInsPerFragment())
        return lists
    }

}
