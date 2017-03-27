package com.yimiao100.sale.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 常用地址
 * Created by 亿苗通 on 2016/10/27.
 */

public class Address implements Parcelable {
    private int id;
    private int userId;
    private int provinceId;
    private int cityId;
    private int areaId;
    private int isDefault;
    private String cnName;
    private String phoneNumber;
    private String zipCode;
    private String fullAddress;
    private String provinceName;
    private String cityName;
    private String areaName;
    private long createdAt;
    private long updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.userId);
        dest.writeInt(this.provinceId);
        dest.writeInt(this.cityId);
        dest.writeInt(this.areaId);
        dest.writeInt(this.isDefault);
        dest.writeString(this.cnName);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.zipCode);
        dest.writeString(this.fullAddress);
        dest.writeString(this.provinceName);
        dest.writeString(this.cityName);
        dest.writeString(this.areaName);
        dest.writeLong(this.createdAt);
        dest.writeLong(this.updatedAt);
    }

    public Address() {
    }

    protected Address(Parcel in) {
        this.id = in.readInt();
        this.userId = in.readInt();
        this.provinceId = in.readInt();
        this.cityId = in.readInt();
        this.areaId = in.readInt();
        this.isDefault = in.readInt();
        this.cnName = in.readString();
        this.phoneNumber = in.readString();
        this.zipCode = in.readString();
        this.fullAddress = in.readString();
        this.provinceName = in.readString();
        this.cityName = in.readString();
        this.areaName = in.readString();
        this.createdAt = in.readLong();
        this.updatedAt = in.readLong();
    }

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}
