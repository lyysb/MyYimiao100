package com.yimiao100.sale.bean

/**
 * Created by Michel on 2017/6/8.
 */
data class OrderOnlineBean(
        var status: String,
        var reason: Int,
        var consigneeList: ArrayList<ConsigneeList>,
        var bizSelect: ArrayList<CategoryList>,
        var orderOnline: OrderOnline?
)