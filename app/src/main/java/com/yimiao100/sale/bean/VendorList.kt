package com.yimiao100.sale.bean

import com.bigkoo.pickerview.model.IPickerViewData

/**
 * Created by Michel on 2017/6/9.
 */
data class VendorList (
        var vendorId: Int,
        var categoryList: ArrayList<CategoryList>?,
        var vendorName: String

): IPickerViewData{
    override fun getPickerViewText(): String = vendorName
}