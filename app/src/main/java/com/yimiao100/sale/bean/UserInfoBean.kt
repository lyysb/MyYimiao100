package com.yimiao100.sale.bean

/**
 * Created by 亿苗通 on 2016/8/9.
 */
data class UserInfoBean (
    var id: Int,
    var accountNumber: String,
    var cnName: String,
    var sex: String,
    var age: Int,
    var phoneNumber: String,
    var email: String,
    var provinceId: Int,
    var cityId: Int,
    var areaId: Int,
    var idNumber: String,
    var profileImagePath: String,
    var profileImageUrl: String,
    var signupSource: String,
    var createdAt: Long,
    var updatedAt: Long,
    var provinceName: String,
    var cityName: String,
    var areaName: String
)