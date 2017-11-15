package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/9/28.
 */
data class ConsigneeList(
        var consigneeName: String,
        var consigneePhoneNumber: String,
        var provinceId: Int,
        var cityId: Int,
        var areaId: Int,
        var consigneeAddress: String
)