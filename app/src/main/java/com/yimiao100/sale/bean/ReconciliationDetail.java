package com.yimiao100.sale.bean;

/**
 *
 * Created by 亿苗通 on 2016/9/1.
 */
public class ReconciliationDetail {
    private int id;
    private int orderId;
    private int resourceId;
    private int userId;
    private int vendorId;
    private int productId;
    private int provinceId;
    private int cityId;
    private int areaId;
    private String serialNo;
    private String invoiceNo;
    private String productBatchNo;
    private long expiredAt;
    private double unitPrice;
    private double deliveryPrice;
    private double paymentPrice;
    private int qty;
    private double withholdAmount;
    private String withholdRemark;
    private String deliveryConfirmStatus;
    private String deliveryConfirmStatusName;
    private String paymentConfirmStatus;
    private String paymentConfirmStatusName;
    private String deliveryFundStatus;
    private String paymentFundStatus;
    private long deliveryAt;
    private long paymentAt;
    private long createdAt;
    private long updatedAt;
    private double deliveryTotalAmount;
    private double paymentTotalAmount;
    private String units;

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getProductBatchNo() {
        return productBatchNo;
    }

    public void setProductBatchNo(String productBatchNo) {
        this.productBatchNo = productBatchNo;
    }

    public long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(long expiredAt) {
        this.expiredAt = expiredAt;
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

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getWithholdAmount() {
        return withholdAmount;
    }

    public void setWithholdAmount(double withholdAmount) {
        this.withholdAmount = withholdAmount;
    }

    public String getWithholdRemark() {
        return withholdRemark;
    }

    public void setWithholdRemark(String withholdRemark) {
        this.withholdRemark = withholdRemark;
    }

    public String getDeliveryConfirmStatus() {
        return deliveryConfirmStatus;
    }

    public void setDeliveryConfirmStatus(String deliveryConfirmStatus) {
        this.deliveryConfirmStatus = deliveryConfirmStatus;
    }

    public String getDeliveryConfirmStatusName() {
        return deliveryConfirmStatusName;
    }

    public void setDeliveryConfirmStatusName(String deliveryConfirmStatusName) {
        this.deliveryConfirmStatusName = deliveryConfirmStatusName;
    }

    public String getPaymentConfirmStatus() {
        return paymentConfirmStatus;
    }

    public void setPaymentConfirmStatus(String paymentConfirmStatus) {
        this.paymentConfirmStatus = paymentConfirmStatus;
    }

    public String getPaymentConfirmStatusName() {
        return paymentConfirmStatusName;
    }

    public void setPaymentConfirmStatusName(String paymentConfirmStatusName) {
        this.paymentConfirmStatusName = paymentConfirmStatusName;
    }

    public String getDeliveryFundStatus() {
        return deliveryFundStatus;
    }

    public void setDeliveryFundStatus(String deliveryFundStatus) {
        this.deliveryFundStatus = deliveryFundStatus;
    }

    public String getPaymentFundStatus() {
        return paymentFundStatus;
    }

    public void setPaymentFundStatus(String paymentFundStatus) {
        this.paymentFundStatus = paymentFundStatus;
    }

    public long getDeliveryAt() {
        return deliveryAt;
    }

    public void setDeliveryAt(long deliveryAt) {
        this.deliveryAt = deliveryAt;
    }

    public long getPaymentAt() {
        return paymentAt;
    }

    public void setPaymentAt(long paymentAt) {
        this.paymentAt = paymentAt;
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

    public double getDeliveryTotalAmount() {
        return deliveryTotalAmount;
    }

    public void setDeliveryTotalAmount(double deliveryTotalAmount) {
        this.deliveryTotalAmount = deliveryTotalAmount;
    }

    public double getPaymentTotalAmount() {
        return paymentTotalAmount;
    }

    public void setPaymentTotalAmount(double paymentTotalAmount) {
        this.paymentTotalAmount = paymentTotalAmount;
    }
}
