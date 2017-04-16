package com.yimiao100.sale.bean

/**
 * Created by Michel on 2016/8/9.
 */
data class SignUpBean (
    var userInfo: UserInfoBean,
    var tokenInfo: TokenInfoBean,
    var reason: Int,
    var status: String
)
