package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/9/14.
 */
data class PromoInsResult (
        var page: Int,
        var totalPage: Int,
        var pageSize: Int,
        var pagedList: ArrayList<PromoIns>
)