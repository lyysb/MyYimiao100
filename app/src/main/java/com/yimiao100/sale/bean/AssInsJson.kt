package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/9/1.
 */
data class AssInsJson(
        var status: String,
        var reason: Int,
        var pagedResult: AssInsResult,
        var stat: Stat
)
