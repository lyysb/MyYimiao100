package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/8/30.
 */
data class Reconciliation(
        var areaId: Int,
        var areaName: String,
        var baseAmount: Double,
        var bidDeposit: Double,
        var bidExpiredAt: Long,
        var bidExpiredTipAt: Long,
        var categoryId: Int,
        var categoryName: String,
        var cityId: Int,
        var cityName: String,
        var companyId: Int,
        var companyName: String,
        var companyImageUrl: String,
        var createdAt: Long,
        var customerId: Int,
        var customerName: String?,
        var defaultExpiredAt: Long,
        var endAt: Long,
        var id: Int,
        var increment: Int,
        var orderBidDeposit: Double,
        var orderSaleDeposit: Double,
        var orderStatus: String,
        var orderStatusName: String,
        var ownerName: String,
        var productId: Int,
        var productName: String,
        var provinceId: Int,
        var provinceName: String,
        var resourceId: Int,
        var saleDeposit: Double,
        var saleTotalAmount: Double,
        var serialNo: String,
        var startAt: Long,
        var updatedAt: Long,
        var userAccountId: Int,
        var userAccountType: String,
        var userAccountTypeName: String,
        var userId: Int,
        var tipStatus: Int

)
