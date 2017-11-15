package com.yimiao100.sale.bean

import java.util.ArrayList

/**
 * Created by michel on 2017/9/1.
 */
data class AssInsResult(
        var totalPage: Int,
        var pageSize: Int,
        var page: Int,
        var pagedList: ArrayList<AssInsList>
)