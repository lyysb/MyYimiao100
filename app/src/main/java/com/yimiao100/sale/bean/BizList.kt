package com.yimiao100.sale.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by michel on 2017/8/28.
 */
data class BizList(
        var objectId: Int,
        var tipStatus: Int,
        var bizType: String,
        var userAccountType: String,
        var objectName: String,
        var imageUrl: String
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(objectId)
        writeInt(tipStatus)
        writeString(bizType)
        writeString(userAccountType)
        writeString(objectName)
        writeString(imageUrl)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<BizList> = object : Parcelable.Creator<BizList> {
            override fun createFromParcel(source: Parcel): BizList = BizList(source)
            override fun newArray(size: Int): Array<BizList?> = arrayOfNulls(size)
        }
    }
}
