package com.yimiao100.sale.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 亿苗通 on 2016/9/1.
 */
public class ReconciliationList implements Parcelable {
    private int id;
    private int resourceId;
    private int userId;
    private int vendorId;
    private int productId;
    private int provinceId;
    private int cityId;
    private int areaId;
    private double bidDeposit;
    private double saleDeposit;
    private int quota;
    private int increment;
    private int evaluationMonth;
    private int paymentPeriod;
    private double withholdRatio;
    private double unitPrice;
    private double deliveryPrice;
    private double paymentPrice;
    private String resourceProtocolUrl;
    private String orderProtocolUrl;
    private String serialNo;
    private int bidQty;
    private String userAccountType;
    private String orderStatus;
    private String orderStatusName;
    private String invalidReason;
    private int totalQty;
    private double totalAmount;
    private double orderBidDeposit;
    private double orderRemainingDeposit;
    private long startAt;
    private long endAt;
    private long bidExpiredAt;
    private long defaultExpiredAt;
    private long createdAt;
    private long updatedAt;
    private String vendorName;
    private String vendorLogoImageUrl;
    private String spec;
    private String dosageForm;
    private String provinceName;
    private String cityName;
    private String areaName;
    private String customerName;
    private String categoryName;
    private String productName;

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

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public int getEvaluationMonth() {
        return evaluationMonth;
    }

    public void setEvaluationMonth(int evaluationMonth) {
        this.evaluationMonth = evaluationMonth;
    }

    public int getPaymentPeriod() {
        return paymentPeriod;
    }

    public void setPaymentPeriod(int paymentPeriod) {
        this.paymentPeriod = paymentPeriod;
    }

    public double getWithholdRatio() {
        return withholdRatio;
    }

    public void setWithholdRatio(double withholdRatio) {
        this.withholdRatio = withholdRatio;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public double getPaymentPrice() {
        return paymentPrice;
    }

    public void setPaymentPrice(double paymentPrice) {
        this.paymentPrice = paymentPrice;
    }

    public String getResourceProtocolUrl() {
        return resourceProtocolUrl;
    }

    public void setResourceProtocolUrl(String resourceProtocolUrl) {
        this.resourceProtocolUrl = resourceProtocolUrl;
    }

    public String getOrderProtocolUrl() {
        return orderProtocolUrl;
    }

    public void setOrderProtocolUrl(String orderProtocolUrl) {
        this.orderProtocolUrl = orderProtocolUrl;
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

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getOrderBidDeposit() {
        return orderBidDeposit;
    }

    public void setOrderBidDeposit(double orderBidDeposit) {
        this.orderBidDeposit = orderBidDeposit;
    }

    public double getOrderRemainingDeposit() {
        return orderRemainingDeposit;
    }

    public void setOrderRemainingDeposit(double orderRemainingDeposit) {
        this.orderRemainingDeposit = orderRemainingDeposit;
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

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorLogoImageUrl() {
        return vendorLogoImageUrl;
    }

    public void setVendorLogoImageUrl(String vendorLogoImageUrl) {
        this.vendorLogoImageUrl = vendorLogoImageUrl;
    }


    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.resourceId);
        dest.writeInt(this.userId);
        dest.writeInt(this.vendorId);
        dest.writeInt(this.productId);
        dest.writeInt(this.provinceId);
        dest.writeInt(this.cityId);
        dest.writeInt(this.areaId);
        dest.writeDouble(this.bidDeposit);
        dest.writeDouble(this.saleDeposit);
        dest.writeInt(this.quota);
        dest.writeInt(this.increment);
        dest.writeInt(this.evaluationMonth);
        dest.writeInt(this.paymentPeriod);
        dest.writeDouble(this.withholdRatio);
        dest.writeDouble(this.unitPrice);
        dest.writeDouble(this.deliveryPrice);
        dest.writeDouble(this.paymentPrice);
        dest.writeString(this.resourceProtocolUrl);
        dest.writeString(this.orderProtocolUrl);
        dest.writeString(this.serialNo);
        dest.writeInt(this.bidQty);
        dest.writeString(this.userAccountType);
        dest.writeString(this.orderStatus);
        dest.writeString(this.orderStatusName);
        dest.writeString(this.invalidReason);
        dest.writeInt(this.totalQty);
        dest.writeDouble(this.totalAmount);
        dest.writeDouble(this.orderBidDeposit);
        dest.writeDouble(this.orderRemainingDeposit);
        dest.writeLong(this.startAt);
        dest.writeLong(this.endAt);
        dest.writeLong(this.bidExpiredAt);
        dest.writeLong(this.defaultExpiredAt);
        dest.writeLong(this.createdAt);
        dest.writeLong(this.updatedAt);
        dest.writeString(this.vendorName);
        dest.writeString(this.vendorLogoImageUrl);
        dest.writeString(this.spec);
        dest.writeString(this.dosageForm);
        dest.writeString(this.provinceName);
        dest.writeString(this.cityName);
        dest.writeString(this.areaName);
        dest.writeString(this.customerName);
        dest.writeString(this.categoryName);
        dest.writeString(this.productName);
    }

    public ReconciliationList() {
    }

    protected ReconciliationList(Parcel in) {
        this.id = in.readInt();
        this.resourceId = in.readInt();
        this.userId = in.readInt();
        this.vendorId = in.readInt();
        this.productId = in.readInt();
        this.provinceId = in.readInt();
        this.cityId = in.readInt();
        this.areaId = in.readInt();
        this.bidDeposit = in.readDouble();
        this.saleDeposit = in.readDouble();
        this.quota = in.readInt();
        this.increment = in.readInt();
        this.evaluationMonth = in.readInt();
        this.paymentPeriod = in.readInt();
        this.withholdRatio = in.readDouble();
        this.unitPrice = in.readDouble();
        this.deliveryPrice = in.readDouble();
        this.paymentPrice = in.readDouble();
        this.resourceProtocolUrl = in.readString();
        this.orderProtocolUrl = in.readString();
        this.serialNo = in.readString();
        this.bidQty = in.readInt();
        this.userAccountType = in.readString();
        this.orderStatus = in.readString();
        this.orderStatusName = in.readString();
        this.invalidReason = in.readString();
        this.totalQty = in.readInt();
        this.totalAmount = in.readDouble();
        this.orderBidDeposit = in.readDouble();
        this.orderRemainingDeposit = in.readDouble();
        this.startAt = in.readLong();
        this.endAt = in.readLong();
        this.bidExpiredAt = in.readLong();
        this.defaultExpiredAt = in.readLong();
        this.createdAt = in.readLong();
        this.updatedAt = in.readLong();
        this.vendorName = in.readString();
        this.vendorLogoImageUrl = in.readString();
        this.spec = in.readString();
        this.dosageForm = in.readString();
        this.provinceName = in.readString();
        this.cityName = in.readString();
        this.areaName = in.readString();
        this.customerName = in.readString();
        this.categoryName = in.readString();
        this.productName = in.readString();
    }

    public static final Parcelable.Creator<ReconciliationList> CREATOR = new Parcelable.Creator<ReconciliationList>() {
        public ReconciliationList createFromParcel(Parcel source) {
            return new ReconciliationList(source);
        }

        public ReconciliationList[] newArray(int size) {
            return new ReconciliationList[size];
        }
    };
}
