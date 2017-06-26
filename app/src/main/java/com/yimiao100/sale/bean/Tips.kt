package com.yimiao100.sale.bean

/**
 * Created by Michel on 2017/6/5.
 */
data class Tips(
        var order_balance: Int,
        var notice: Int,
        var details: HashMap<String, ArrayList<String>>
)