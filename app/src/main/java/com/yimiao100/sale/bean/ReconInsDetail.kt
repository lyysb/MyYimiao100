package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/8/30.
 */
data class ReconInsDetail (
        var id: Int,
        var orderId: Int,
        var policyId: Int,
        var companyId: Int,
        var confirmStatus: String,
        var confirmStatusName: String,
        var customerName: String,
        var itemAmount: Double,     // 奖励金额(推广费)
        var unitPrice: Double,      // 保险单价
        var policyAmount: Double,   // 保费合计
        var packageName: String,    // 套餐名称
        var policyNo: String,       // 流水单号
        var insurePeriod: Int,      // 投保年限
        var policyCreatedAt: String,  // 保单生成日期
        var policyStartAt: String,    // 时间周期起
        var policyEndAt: String       // 时间周期止
)