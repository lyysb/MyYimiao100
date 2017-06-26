package com.yimiao100.sale.bean

import com.bigkoo.pickerview.model.IPickerViewData

/**
 * Created by Michel on 2017/6/8.
 */
data class CategoryList(
        var categoryId: Int,
        var categoryName: String,
        var productList: ArrayList<ProductList>?,
        var createdAt: Long,
        var updatedAt: Long,
        var selected: Long
): IPickerViewData{
    override fun getPickerViewText(): String = categoryName
}