package com.yimiao100.sale.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by michel on 2017/8/23.
 */
data class InsuranceInfo(
        var id: Int,
        var companyId: Int,
        var categoryId: Int,
        var productId: Int,
        var provinceId: Int,
        var cityId: Int,
        var areaId: Int,
        var customerId: Int?,
        var bidDeposit: Double,
        var saleDeposit: Double,
        var saleDepositPercent: Double,
        var quota: Double,
        var increment: Double,
        var baseAmount: Double,
        var salePercent: Double,
        var policyContent: String,
        var resourceStatus: String,
        var resourceStatusName: String,
        var companyName: String,
        var companyImageUrl: String,
        var categoryName: String,
        var productName: String,
        var productImageUrl: String,
        var productVideoUrl: String?,
        var provinceName: String,
        var cityName: String,
        var areaName: String,
        var customerName: String?,
        var startAt: Long,
        var endAt: Long,
        var bidExpiredAt: Long,
        var bidExpiredTipAt: Long,
        var defaultExpiredAt: Long,
        var createdAt: Long,
        var updatedAt: Long
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readInt(),
            source.readInt(),
            source.readInt(),
            source.readInt(),
            source.readInt(),
            source.readInt(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readDouble(),
            source.readDouble(),
            source.readDouble(),
            source.readDouble(),
            source.readDouble(),
            source.readDouble(),
            source.readDouble(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readLong(),
            source.readLong(),
            source.readLong(),
            source.readLong(),
            source.readLong(),
            source.readLong(),
            source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeInt(companyId)
        writeInt(categoryId)
        writeInt(productId)
        writeInt(provinceId)
        writeInt(cityId)
        writeInt(areaId)
        writeValue(customerId)
        writeDouble(bidDeposit)
        writeDouble(saleDeposit)
        writeDouble(saleDepositPercent)
        writeDouble(quota)
        writeDouble(increment)
        writeDouble(baseAmount)
        writeDouble(salePercent)
        writeString(policyContent)
        writeString(resourceStatus)
        writeString(resourceStatusName)
        writeString(companyName)
        writeString(companyImageUrl)
        writeString(categoryName)
        writeString(productName)
        writeString(productImageUrl)
        writeString(productVideoUrl)
        writeString(provinceName)
        writeString(cityName)
        writeString(areaName)
        writeString(customerName)
        writeLong(startAt)
        writeLong(endAt)
        writeLong(bidExpiredAt)
        writeLong(bidExpiredTipAt)
        writeLong(defaultExpiredAt)
        writeLong(createdAt)
        writeLong(updatedAt)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<InsuranceInfo> = object : Parcelable.Creator<InsuranceInfo> {
            override fun createFromParcel(source: Parcel): InsuranceInfo = InsuranceInfo(source)
            override fun newArray(size: Int): Array<InsuranceInfo?> = arrayOfNulls(size)
        }
    }
}