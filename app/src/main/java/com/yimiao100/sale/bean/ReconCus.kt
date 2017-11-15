package com.yimiao100.sale.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by michel on 2017/9/12.
 */
data class ReconCus (
        @SerializedName("id")
        var customerId: Int,
        var customerName: String
)