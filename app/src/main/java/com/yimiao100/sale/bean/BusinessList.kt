package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/8/28.
 */
data class BusinessList(
        var pagedList: ArrayList<Business>,
        var totalPage: Int,
        var pageSize: Int,
        var page: Int
)