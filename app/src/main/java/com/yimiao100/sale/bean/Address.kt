package com.yimiao100.sale.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * 常用地址
 * Created by michel on 2016/10/27.
 */

data class Address(
        var id: Int,
        var userId: Int,
        var provinceId: Int,
        var cityId: Int,
        var areaId: Int,
        var isDefault: Int,
        var cnName: String,
        var phoneNumber: String,
        var zipCode: String,
        var fullAddress: String,
        var provinceName: String,
        var cityName: String,
        var areaName: String,
        var createdAt: Long,
        var updatedAt: Long
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Address> = object : Parcelable.Creator<Address> {
            override fun createFromParcel(source: Parcel): Address = Address(source)
            override fun newArray(size: Int): Array<Address?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readLong(), source.readLong())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(id)
        dest?.writeInt(userId)
        dest?.writeInt(provinceId)
        dest?.writeInt(cityId)
        dest?.writeInt(areaId)
        dest?.writeInt(isDefault)
        dest?.writeString(cnName)
        dest?.writeString(phoneNumber)
        dest?.writeString(zipCode)
        dest?.writeString(fullAddress)
        dest?.writeString(provinceName)
        dest?.writeString(cityName)
        dest?.writeString(areaName)
        dest?.writeLong(createdAt)
        dest?.writeLong(updatedAt)
    }
}