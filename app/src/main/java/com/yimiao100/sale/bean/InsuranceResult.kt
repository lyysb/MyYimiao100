package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/8/22.
 */
data class InsuranceResult (
        var page: Int,
        var totalPage: Int,
        var pageSize: Int,
        var pagedList: ArrayList<InsuranceDetail>
)