package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/8/30.
 */
data class ReconDetailJson(
        var reason: Int,
        var status: String,
        var pagedResult: ReconDetailResult,
        var stat: InsuranceStat
)