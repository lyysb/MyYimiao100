package com.yimiao100.sale.bean

/**
 * 批量竞标
 * Created by Michel on 2018/3/20.
 */
data class BizData(
        var userAccountType: String,
        var bidList: ArrayList<Bid>
)