package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/8/30.
 */
data class ReconDetailResult(
        var page: Int,
        var pageSize: Int,
        var totalPage: Int,
        var pagedList: ArrayList<ReconInsDetail>
)