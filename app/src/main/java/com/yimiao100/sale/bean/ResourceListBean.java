package com.yimiao100.sale.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 亿苗通 on 2016/8/19.
 */
public class ResourceListBean implements Parcelable {
    private int resourceId;             //业务列表中资源id
    private int userId;                 //用户id
    private String orderNo;             //订单号
    private int bidQty;                 //竞标数量
    private String userAccountType;     //用户账户类型
    private String orderStatus;         //订单状态
    private String orderStatusName;     //订单状态名称
    private String invalidReason;       //无效原因
    private int deliveryTotalQty;       //发货总数量
    private double deliveryTotalAmount; //发货总金额
    private int paymentTotalQty;        //回款总数量
    private double paymentTotalAmount;  //回款总金额
    private double orderBidDeposit;     //订单竞标保证金
    private double orderRemainingDeposit;   //订单保证金余额
    private long bidExpiredAt;              //竞标有效时间
    private long defaultExpiredAt;          //违约有效时间
    private String resourceProtocolUrl;
    private String orderProtocolUrl;
    private String serialNo;                //流水号
    private boolean read;        //已读
    private long bidExpiredTipAt;
    private String units;

    private String totalQty;

    public String getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(String totalQty) {
        this.totalQty = totalQty;
    }

    public static Creator<ResourceListBean> getCREATOR() {
        return CREATOR;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public long getBidExpiredTipAt() {
        return bidExpiredTipAt;
    }

    public void setBidExpiredTipAt(long bidExpiredTipAt) {
        this.bidExpiredTipAt = bidExpiredTipAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
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

    public double getOrderBidDeposit() {
        return orderBidDeposit;
    }

    public void setOrderBidDeposit(double orderBidDeposit) {
        this.orderBidDeposit = orderBidDeposit;
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public int getDeliveryTotalQty() {
        return deliveryTotalQty;
    }

    public void setDeliveryTotalQty(int deliveryTotalQty) {
        this.deliveryTotalQty = deliveryTotalQty;
    }

    public double getDeliveryTotalAmount() {
        return deliveryTotalAmount;
    }

    public void setDeliveryTotalAmount(double deliveryTotalAmount) {
        this.deliveryTotalAmount = deliveryTotalAmount;
    }

    public int getPaymentTotalQty() {
        return paymentTotalQty;
    }

    public void setPaymentTotalQty(int paymentTotalQty) {
        this.paymentTotalQty = paymentTotalQty;
    }

    public double getPaymentTotalAmount() {
        return paymentTotalAmount;
    }

    public void setPaymentTotalAmount(double paymentTotalAmount) {
        this.paymentTotalAmount = paymentTotalAmount;
    }

    public double getOrderRemainingDeposit() {
        return orderRemainingDeposit;
    }

    public void setOrderRemainingDeposit(double orderRemainingDeposit) {
        this.orderRemainingDeposit = orderRemainingDeposit;
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

    private int id;         //资源列表中为资源id。业务列表中为业务id
    /**
     * 以下为资源列表和业务列表公用部分
     */
    private int vendorId;
    private int productId;
    private int provinceId;
    private int cityId;
    private int areaId;
    private long startAt;
    private long endAt;
    private double bidDeposit;
    private double saleDeposit;
    private int quota;
    private int unitPrice;
    private int deliveryPrice;
    private int paymentPrice;
    private int incompletePercent;
    private int reducePercent;
    private String policyContent;
    private String protocolFilePath;
    private String protocolFileUrl;
    private long expiredAt;
    private long createdAt;
    private long updatedAt;
    private String vendorName;
    private String vendorLogoImageUrl;
    private String productFormalName;
    private String productCommonName;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    private int increment;  //竞标增量

    private String productImageUrl;
    private String productVideoUrl;

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductVideoUrl() {
        return productVideoUrl;
    }

    public void setProductVideoUrl(String productVideoUrl) {
        this.productVideoUrl = productVideoUrl;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(int deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public int getPaymentPrice() {
        return paymentPrice;
    }

    public void setPaymentPrice(int paymentPrice) {
        this.paymentPrice = paymentPrice;
    }

    public int getIncompletePercent() {
        return incompletePercent;
    }

    public void setIncompletePercent(int incompletePercent) {
        this.incompletePercent = incompletePercent;
    }

    public int getReducePercent() {
        return reducePercent;
    }

    public void setReducePercent(int reducePercent) {
        this.reducePercent = reducePercent;
    }

    public String getPolicyContent() {
        return policyContent;
    }

    public void setPolicyContent(String policyContent) {
        this.policyContent = policyContent;
    }

    public String getProtocolFilePath() {
        return protocolFilePath;
    }

    public void setProtocolFilePath(String protocolFilePath) {
        this.protocolFilePath = protocolFilePath;
    }

    public String getProtocolFileUrl() {
        return protocolFileUrl;
    }

    public void setProtocolFileUrl(String protocolFileUrl) {
        this.protocolFileUrl = protocolFileUrl;
    }

    public long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(long expiredAt) {
        this.expiredAt = expiredAt;
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

    public String getProductFormalName() {
        return productFormalName;
    }

    public void setProductFormalName(String productFormalName) {
        this.productFormalName = productFormalName;
    }

    public String getProductCommonName() {
        return productCommonName;
    }

    public void setProductCommonName(String productCommonName) {
        this.productCommonName = productCommonName;
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


    public ResourceListBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.totalQty);
        dest.writeInt(this.resourceId);
        dest.writeInt(this.userId);
        dest.writeString(this.orderNo);
        dest.writeInt(this.bidQty);
        dest.writeString(this.userAccountType);
        dest.writeString(this.orderStatus);
        dest.writeString(this.orderStatusName);
        dest.writeString(this.invalidReason);
        dest.writeInt(this.deliveryTotalQty);
        dest.writeDouble(this.deliveryTotalAmount);
        dest.writeInt(this.paymentTotalQty);
        dest.writeDouble(this.paymentTotalAmount);
        dest.writeDouble(this.orderBidDeposit);
        dest.writeDouble(this.orderRemainingDeposit);
        dest.writeLong(this.bidExpiredAt);
        dest.writeLong(this.defaultExpiredAt);
        dest.writeString(this.resourceProtocolUrl);
        dest.writeString(this.orderProtocolUrl);
        dest.writeString(this.serialNo);
        dest.writeByte(read ? (byte) 1 : (byte) 0);
        dest.writeLong(this.bidExpiredTipAt);
        dest.writeString(this.units);
        dest.writeInt(this.id);
        dest.writeInt(this.vendorId);
        dest.writeInt(this.productId);
        dest.writeInt(this.provinceId);
        dest.writeInt(this.cityId);
        dest.writeInt(this.areaId);
        dest.writeLong(this.startAt);
        dest.writeLong(this.endAt);
        dest.writeDouble(this.bidDeposit);
        dest.writeDouble(this.saleDeposit);
        dest.writeInt(this.quota);
        dest.writeInt(this.unitPrice);
        dest.writeInt(this.deliveryPrice);
        dest.writeInt(this.paymentPrice);
        dest.writeInt(this.incompletePercent);
        dest.writeInt(this.reducePercent);
        dest.writeString(this.policyContent);
        dest.writeString(this.protocolFilePath);
        dest.writeString(this.protocolFileUrl);
        dest.writeLong(this.expiredAt);
        dest.writeLong(this.createdAt);
        dest.writeLong(this.updatedAt);
        dest.writeString(this.vendorName);
        dest.writeString(this.vendorLogoImageUrl);
        dest.writeString(this.productFormalName);
        dest.writeString(this.productCommonName);
        dest.writeString(this.spec);
        dest.writeString(this.dosageForm);
        dest.writeString(this.provinceName);
        dest.writeString(this.cityName);
        dest.writeString(this.areaName);
        dest.writeString(this.customerName);
        dest.writeString(this.categoryName);
        dest.writeString(this.productName);
        dest.writeInt(this.increment);
        dest.writeString(this.productImageUrl);
        dest.writeString(this.productVideoUrl);
    }

    protected ResourceListBean(Parcel in) {
        this.totalQty = in.readString();
        this.resourceId = in.readInt();
        this.userId = in.readInt();
        this.orderNo = in.readString();
        this.bidQty = in.readInt();
        this.userAccountType = in.readString();
        this.orderStatus = in.readString();
        this.orderStatusName = in.readString();
        this.invalidReason = in.readString();
        this.deliveryTotalQty = in.readInt();
        this.deliveryTotalAmount = in.readDouble();
        this.paymentTotalQty = in.readInt();
        this.paymentTotalAmount = in.readDouble();
        this.orderBidDeposit = in.readDouble();
        this.orderRemainingDeposit = in.readDouble();
        this.bidExpiredAt = in.readLong();
        this.defaultExpiredAt = in.readLong();
        this.resourceProtocolUrl = in.readString();
        this.orderProtocolUrl = in.readString();
        this.serialNo = in.readString();
        this.read = in.readByte() != 0;
        this.bidExpiredTipAt = in.readLong();
        this.units = in.readString();
        this.id = in.readInt();
        this.vendorId = in.readInt();
        this.productId = in.readInt();
        this.provinceId = in.readInt();
        this.cityId = in.readInt();
        this.areaId = in.readInt();
        this.startAt = in.readLong();
        this.endAt = in.readLong();
        this.bidDeposit = in.readDouble();
        this.saleDeposit = in.readDouble();
        this.quota = in.readInt();
        this.unitPrice = in.readInt();
        this.deliveryPrice = in.readInt();
        this.paymentPrice = in.readInt();
        this.incompletePercent = in.readInt();
        this.reducePercent = in.readInt();
        this.policyContent = in.readString();
        this.protocolFilePath = in.readString();
        this.protocolFileUrl = in.readString();
        this.expiredAt = in.readLong();
        this.createdAt = in.readLong();
        this.updatedAt = in.readLong();
        this.vendorName = in.readString();
        this.vendorLogoImageUrl = in.readString();
        this.productFormalName = in.readString();
        this.productCommonName = in.readString();
        this.spec = in.readString();
        this.dosageForm = in.readString();
        this.provinceName = in.readString();
        this.cityName = in.readString();
        this.areaName = in.readString();
        this.customerName = in.readString();
        this.categoryName = in.readString();
        this.productName = in.readString();
        this.increment = in.readInt();
        this.productImageUrl = in.readString();
        this.productVideoUrl = in.readString();
    }

    public static final Creator<ResourceListBean> CREATOR = new Creator<ResourceListBean>() {
        public ResourceListBean createFromParcel(Parcel source) {
            return new ResourceListBean(source);
        }

        public ResourceListBean[] newArray(int size) {
            return new ResourceListBean[size];
        }
    };
}
