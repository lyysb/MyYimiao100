package com.yimiao100.sale.bean

import com.bigkoo.pickerview.model.IPickerViewData

/**
 * Created by Michel on 2017/6/8.
 */
data class Customer(
        var customerId: Int,
        var provinceId: Int,
        var cityId: Int,
        var areaId: Int,
        var customerName: String
): IPickerViewData{
    override fun getPickerViewText(): String = customerName
}