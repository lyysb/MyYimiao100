package com.yimiao100.sale.bean

/**
 * Created by Michel on 2017/6/8.
 */
data class OrderOnline(
        var id: Int,
        var userId: Int,
        var vendorId: Int,
        var categoryId: Int,
        var productId: Int,
        var specId: Int,
        var dosageFormId: Int,
        var provinceId: Int,
        var cityId: Int,
        var areaId: Int,
        var customerId: Int,
        var applyDeliveryAt: Long,
        var deliveryQty: String,
        var deliveryUnits: String,
        var protocolFilePath: String,
        var protocolFileUrl: String,
        var protocolFileCount: String,
        var remark: String,
        var consigneeName: String,
        var consigneePhoneNumber: String,
        var consigneeAddress: String,
        var bizName: String,
        var bizPhoneNumber: String,
        var vendorName: String,
        var vendorLogoUrl: String,
        var categoryName: String,
        var productName: String,
        var spec: String,
        var dosageForm: String,
        var provinceName: String,
        var cityName: String,
        var areaName: String,
        var customerName: String,
        var createdAt: Long,
        var updatedAt: Long
)