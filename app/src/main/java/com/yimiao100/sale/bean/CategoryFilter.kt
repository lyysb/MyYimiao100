package com.yimiao100.sale.bean

import com.bigkoo.pickerview.model.IPickerViewData

/**
 * 过滤条件-Category
 * Created by Michel on 2018/3/15.
 */
data class CategoryFilter (
        var id: Int,
        var categoryName: String
): IPickerViewData {
    override fun getPickerViewText(): String = categoryName
}