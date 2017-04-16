package com.yimiao100.sale.bean

/**
 * Created by 亿苗通 on 2016/8/9.
 */
data class TokenInfoBean (
    var accessTokenExpiredAt: Long,
    var accessToken: String,
    var accessTokenExpireInterval: String,
    var refreshToken: String
)
