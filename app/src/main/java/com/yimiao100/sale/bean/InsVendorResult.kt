package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/9/11.
 */
data class InsVendorResult(
        var totalPage: Int,
        var pageSize: Int,
        var page: Int,
        var pagedList: ArrayList<InsVendor>
)