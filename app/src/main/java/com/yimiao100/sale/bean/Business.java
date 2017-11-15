package com.yimiao100.sale.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by michel on 2017/8/30.
 */
public class Business implements Parcelable {
    private int id;
    private int resourceId;
    private int userId;
    private int userAccountId;
    private int companyId;
    private int categoryId;
    private double bidDeposit;
    private double saleDeposit;
    private int quota;
    private double baseAmount;
    private int increment;
    private String serialNo;
    private int bidQty;
    private String userAccountType;
    private String orderStatus;
    private String orderStatusName;
    private String invalidReason;
    private long startAt;
    private long endAt;
    private long bidExpiredAt;
    private long bidExpiredTipAt;
    private long defaultExpiredAt;
    private long createdAt;
    private long updatedAt;
    private String companyName;
    private String companyImageUrl;
    private String categoryName;
    private String productName;
    private String provinceName;
    private String cityName;
    private String areaName;
    private String customerName;
    private String ownerName;
    private double saleTotalAmount;

    @Override
    public String toString() {
        return "Business{" +
                "id=" + id +
                ", resourceId=" + resourceId +
                ", userId=" + userId +
                ", userAccountId=" + userAccountId +
                ", companyId=" + companyId +
                ", categoryId=" + categoryId +
                ", bidDeposit=" + bidDeposit +
                ", saleDeposit=" + saleDeposit +
                ", quota=" + quota +
                ", baseAmount=" + baseAmount +
                ", increment=" + increment +
                ", serialNo='" + serialNo + '\'' +
                ", bidQty=" + bidQty +
                ", userAccountType='" + userAccountType + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderStatusName='" + orderStatusName + '\'' +
                ", invalidReason='" + invalidReason + '\'' +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                ", bidExpiredAt=" + bidExpiredAt +
                ", bidExpiredTipAt=" + bidExpiredTipAt +
                ", defaultExpiredAt=" + defaultExpiredAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", companyName='" + companyName + '\'' +
                ", companyImageUrl='" + companyImageUrl + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", productName='" + productName + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", areaName='" + areaName + '\'' +
                ", customerName='" + customerName + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", saleTotalAmount=" + saleTotalAmount +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(int userAccountId) {
        this.userAccountId = userAccountId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getBidDeposit() {
        return bidDeposit;
    }

    public void setBidDeposit(double bidDeposit) {
        this.bidDeposit = bidDeposit;
    }

    public double getSaleDeposit() {
        return saleDeposit;
    }

    public void setSaleDeposit(double saleDeposit) {
        this.saleDeposit = saleDeposit;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public int getBidQty() {
        return bidQty;
    }

    public void setBidQty(int bidQty) {
        this.bidQty = bidQty;
    }

    public String getUserAccountType() {
        return userAccountType;
    }

    public void setUserAccountType(String userAccountType) {
        this.userAccountType = userAccountType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatusName() {
        return orderStatusName;
    }

    public void setOrderStatusName(String orderStatusName) {
        this.orderStatusName = orderStatusName;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }

    public long getStartAt() {
        return startAt;
    }

    public void setStartAt(long startAt) {
        this.startAt = startAt;
    }

    public long getEndAt() {
        return endAt;
    }

    public void setEndAt(long endAt) {
        this.endAt = endAt;
    }

    public long getBidExpiredAt() {
        return bidExpiredAt;
    }

    public void setBidExpiredAt(long bidExpiredAt) {
        this.bidExpiredAt = bidExpiredAt;
    }

    public long getBidExpiredTipAt() {
        return bidExpiredTipAt;
    }

    public void setBidExpiredTipAt(long bidExpiredTipAt) {
        this.bidExpiredTipAt = bidExpiredTipAt;
    }

    public long getDefaultExpiredAt() {
        return defaultExpiredAt;
    }

    public void setDefaultExpiredAt(long defaultExpiredAt) {
        this.defaultExpiredAt = defaultExpiredAt;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyImageUrl() {
        return companyImageUrl;
    }

    public void setCompanyImageUrl(String companyImageUrl) {
        this.companyImageUrl = companyImageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getSaleTotalAmount() {
        return saleTotalAmount;
    }

    public void setSaleTotalAmount(double saleTotalAmount) {
        this.saleTotalAmount = saleTotalAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.resourceId);
        dest.writeInt(this.userId);
        dest.writeInt(this.userAccountId);
        dest.writeInt(this.companyId);
        dest.writeInt(this.categoryId);
        dest.writeDouble(this.bidDeposit);
        dest.writeDouble(this.saleDeposit);
        dest.writeInt(this.quota);
        dest.writeDouble(this.baseAmount);
        dest.writeInt(this.increment);
        dest.writeString(this.serialNo);
        dest.writeInt(this.bidQty);
        dest.writeString(this.userAccountType);
        dest.writeString(this.orderStatus);
        dest.writeString(this.orderStatusName);
        dest.writeString(this.invalidReason);
        dest.writeLong(this.startAt);
        dest.writeLong(this.endAt);
        dest.writeLong(this.bidExpiredAt);
        dest.writeLong(this.bidExpiredTipAt);
        dest.writeLong(this.defaultExpiredAt);
        dest.writeLong(this.createdAt);
        dest.writeLong(this.updatedAt);
        dest.writeString(this.companyName);
        dest.writeString(this.companyImageUrl);
        dest.writeString(this.categoryName);
        dest.writeString(this.productName);
        dest.writeString(this.provinceName);
        dest.writeString(this.cityName);
        dest.writeString(this.areaName);
        dest.writeString(this.customerName);
        dest.writeString(this.ownerName);
        dest.writeDouble(this.saleTotalAmount);
    }

    public Business() {
    }

    protected Business(Parcel in) {
        this.id = in.readInt();
        this.resourceId = in.readInt();
        this.userId = in.readInt();
        this.userAccountId = in.readInt();
        this.companyId = in.readInt();
        this.categoryId = in.readInt();
        this.bidDeposit = in.readDouble();
        this.saleDeposit = in.readDouble();
        this.quota = in.readInt();
        this.baseAmount = in.readDouble();
        this.increment = in.readInt();
        this.serialNo = in.readString();
        this.bidQty = in.readInt();
        this.userAccountType = in.readString();
        this.orderStatus = in.readString();
        this.orderStatusName = in.readString();
        this.invalidReason = in.readString();
        this.startAt = in.readLong();
        this.endAt = in.readLong();
        this.bidExpiredAt = in.readLong();
        this.bidExpiredTipAt = in.readLong();
        this.defaultExpiredAt = in.readLong();
        this.createdAt = in.readLong();
        this.updatedAt = in.readLong();
        this.companyName = in.readString();
        this.companyImageUrl = in.readString();
        this.categoryName = in.readString();
        this.productName = in.readString();
        this.provinceName = in.readString();
        this.cityName = in.readString();
        this.areaName = in.readString();
        this.customerName = in.readString();
        this.ownerName = in.readString();
        this.saleTotalAmount = in.readDouble();
    }

    public static final Parcelable.Creator<Business> CREATOR = new Parcelable.Creator<Business>() {
        @Override
        public Business createFromParcel(Parcel source) {
            return new Business(source);
        }

        @Override
        public Business[] newArray(int size) {
            return new Business[size];
        }
    };
}
