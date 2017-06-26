package com.yimiao100.sale.bean

/**
 * Created by Michel on 2017/6/9.
 */
data class OrderNoteResult(
        var page: Int,
        var totalPage: Int,
        var pageSize: Int,
        var pagedList: ArrayList<OrderNote>
)