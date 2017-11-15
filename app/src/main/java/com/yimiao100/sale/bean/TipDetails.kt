package com.yimiao100.sale.bean

/**
 * Created by michel on 2017/9/4.
 */
data class TipDetails(
        var insurance_order_balance: HashMap<String, ArrayList<String>>,
        var vaccine_order_balance: HashMap<String, ArrayList<String>>
)