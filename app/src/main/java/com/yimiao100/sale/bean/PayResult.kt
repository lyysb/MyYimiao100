package com.yimiao100.sale.bean

/**
 * 支付结果返回
 * Created by Michel on 2018/3/20.
 */
data class PayResult(
        var status: String,
        var reason: Int,
        var payRequest: PayRequest
)