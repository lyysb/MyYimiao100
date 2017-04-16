package com.yimiao100.sale.bean

/**
 * Created by 亿苗通 on 2016/11/9.
 */
data class AccountDetail(
    var id: Int,
    var userId: Int,
    var orderId: Int,
    var amount: Double,
    var accountDetailType: String,
    var accountType: String,
    var accountTypeName: String,
    var transactionType: String,
    var transactionTypeName: String,
    var remark: String,
    var createdAt: Long,
    var updatedAt: Long,
    var cnName: String,
    var serialNo: String,
    var categoryName: String,
    var productName: String,
    var spec: String,
    var dosageForm: String,
    var provinceName: String,
    var cityName: String,
    var areaName: String,
    var customerName: String,
    var vendorName: String
)
