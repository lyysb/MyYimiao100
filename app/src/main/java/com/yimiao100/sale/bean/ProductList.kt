package com.yimiao100.sale.bean

import com.bigkoo.pickerview.model.IPickerViewData

/**
 * Created by Michel on 2017/6/8.
 */
data class ProductList(
        var productId: Int,
        var vendorId: Int,
        var categoryId: Int,
        var productName: String,
        var productDesc: String,
        var imagePath: String,
        var imageUrl: String,
        var createdAt: Long,
        var updatedAt: Long,
        var selected: Long,
        var vendorName: String,
        var vendorLogoUrl: String,
        var categoryName: String,
        var specId: Int,
        var dosageFormId: Int,
        var spec: String,
        var dosageForm: String,
        var provinceList: ArrayList<Province>,
        var unitsList: ArrayList<String>
): IPickerViewData {
    override fun getPickerViewText(): String = "$productName\t$spec\t$dosageForm"
}