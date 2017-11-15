package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/9/14.
 */
data class PromoIns (
        var provinceName: String,
        var cityName: String,
        var areaName: String,
        var companyName: String,
        var productName: String,
        var serialNo: String,
        var saleWithdrawTotalAmount: Double,
        var isChecked: Boolean,
        var id: Int
)