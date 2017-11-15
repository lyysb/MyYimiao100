package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/8/22.
 */
data class InsuranceDetail(
        var id: Int,
        var companyId: Int,
        var categoryId: Int,
        var productId: Int,
        var provinceId: Int,
        var cityId: Int,
        var areaId: Int,
        var customerId: Int?,
        var bidDeposit: Double,
        var saleDeposit: Double,
        var quota: Double,
        var increment: Double,
        var resourceStatus: String,
        var resourceStatusName: String,
        var startAt: Long,
        var endAt: Long,
        var bidExpiredAt: Long,
        var bidExpiredTipAt: Long,
        var defaultExpiredAt: Long,
        var createdAt: Long,
        var updatedAt: Long,
        var companyName: String,
        var companyImageUrl: String,
        var categoryName: String,
        var productName: String,
        var productImageUrl: String,
        var provinceName: String,
        var cityName: String,
        var areaName: String,
        var customerName: String?
)