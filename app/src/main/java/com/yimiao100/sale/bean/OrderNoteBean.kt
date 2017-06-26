package com.yimiao100.sale.bean

/**
 * Created by Michel on 2017/6/9.
 */
data class OrderNoteBean(
        var status: String,
        var reason: Int,
        var pagedResult: OrderNoteResult
)