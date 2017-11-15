package com.yimiao100.sale.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by michel on 2017/9/12.
 */
data class ReconCusResult(
        var totalPage: Int,
        var pageSize: Int,
        var page: Int,
        @SerializedName("pagedList")
        var customerList: ArrayList<ReconCus>
)