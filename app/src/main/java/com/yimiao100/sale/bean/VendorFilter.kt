package com.yimiao100.sale.bean

import com.bigkoo.pickerview.model.IPickerViewData

/**
 * 过滤条件-Vendor
 * Created by Michel on 2018/3/15.
 */
data class VendorFilter(
        var id: Int,
        var vendorName: String,
        var categoryList: ArrayList<CategoryFilter>
): IPickerViewData{
    override fun getPickerViewText(): String = vendorName
}