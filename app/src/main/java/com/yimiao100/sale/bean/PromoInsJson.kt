package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/9/14.
 */
data class PromoInsJson(
        var reason: Int,
        var status: String,
        var pagedResult: PromoInsResult,
        var stat: Stat
)