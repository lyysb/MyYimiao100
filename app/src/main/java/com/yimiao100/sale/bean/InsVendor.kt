package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/9/11.
 */
data class InsVendor (
        var id: Int,
        var resourceId: Int,
        var companyId: Int,
        var companyName: String,
        var provinceName: String,
        var cityName: String,
        var areaName: String,
        var tipStatus: Int,
        var policyCounter: Int,         // 保单数量
        var orderSaleDeposit: Double,   // 保费总金额
        var saleTotalAmount: Double,   // 总金额奖励
        var policyTotalAmount: Double,  // 进度条：policyTotalAmount/saleQuota
        var saleQuota: Double,
        var serialNo: String
)