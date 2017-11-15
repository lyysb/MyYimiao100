package com.yimiao100.sale.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by michel on 2017/9/12.
 */
data class ReconCusJson (
        var reason: Int,
        var status: String,

        @SerializedName("pagedResult")
        var customerResult: ReconCusResult
)