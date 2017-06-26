package com.yimiao100.sale.bean

/**
 * Created by Michel on 2017/6/9.
 */
data class UploadBean(
        var status: String,
        var reason: Int,
        var bizSelect: ArrayList<VendorList>,
        var adverseApply: AdverseApply?,
        var authzApply: AuthorizationApply?

)