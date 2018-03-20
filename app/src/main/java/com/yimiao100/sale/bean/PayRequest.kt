package com.yimiao100.sale.bean

import com.google.gson.annotations.SerializedName

/**
 * 微信支付返回结果
 * Created by Michel on 2018/3/20.
 */
data class PayRequest (
        @SerializedName("package")
        var packageValue: String,   // 因为json的key为关键字，所以使用SerializedName注解
        var appid: String,
        var sign: String,
        var partnerid: String,
        var prepayid: String,
        var noncestr: String,
        var timestamp: String
)