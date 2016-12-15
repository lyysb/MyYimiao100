package com.yimiao100.sale.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 客户列表条目
 * Created by 亿苗通 on 2016/8/31.
 */
public class CDCListBean implements Parcelable {
    private int id;
    private int provinceId;
    private int cityId;
    private int areaId;
    private String cdcName;
    private String clinicName;
    private String address;
    private String phoneNumber;
    private int cardAmount;
    private int useAmount;
    private String provinceName;
    private String cityName;
    private String areaName;
    private int userAddStatus;
    private long createdAt;
    private long updatedAt;

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public int getUserAddStatus() {
        return userAddStatus;
    }

    public void setUserAddStatus(int userAddStatus) {
        this.userAddStatus = userAddStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCdcName() {
        return cdcName;
    }

    public void setCdcName(String cdcName) {
        this.cdcName = cdcName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getCardAmount() {
        return cardAmount;
    }

    public void setCardAmount(int cardAmount) {
        this.cardAmount = cardAmount;
    }

    public int getUseAmount() {
        return useAmount;
    }

    public void setUseAmount(int useAmount) {
        this.useAmount = useAmount;
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
        dest.writeInt(this.provinceId);
        dest.writeInt(this.cityId);
        dest.writeInt(this.areaId);
        dest.writeString(this.cdcName);
        dest.writeString(this.clinicName);
        dest.writeString(this.address);
        dest.writeString(this.phoneNumber);
        dest.writeInt(this.cardAmount);
        dest.writeInt(this.useAmount);
        dest.writeString(this.provinceName);
        dest.writeString(this.cityName);
        dest.writeString(this.areaName);
        dest.writeInt(this.userAddStatus);
        dest.writeLong(this.createdAt);
        dest.writeLong(this.updatedAt);
    }

    public CDCListBean() {
    }

    protected CDCListBean(Parcel in) {
        this.id = in.readInt();
        this.provinceId = in.readInt();
        this.cityId = in.readInt();
        this.areaId = in.readInt();
        this.cdcName = in.readString();
        this.clinicName = in.readString();
        this.address = in.readString();
        this.phoneNumber = in.readString();
        this.cardAmount = in.readInt();
        this.useAmount = in.readInt();
        this.provinceName = in.readString();
        this.cityName = in.readString();
        this.areaName = in.readString();
        this.userAddStatus = in.readInt();
        this.createdAt = in.readLong();
        this.updatedAt = in.readLong();
    }

    public static final Parcelable.Creator<CDCListBean> CREATOR = new Parcelable.Creator<CDCListBean>() {
        public CDCListBean createFromParcel(Parcel source) {
            return new CDCListBean(source);
        }

        public CDCListBean[] newArray(int size) {
            return new CDCListBean[size];
        }
    };
}
