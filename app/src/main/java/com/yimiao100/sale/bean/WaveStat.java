package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/9/13.
 */
public class WaveStat {
    private int deliveryQty;
    private int deliveryTotalQty;
    private double paymentAmount;
    private double paymentTotalAmount;

    public int getDeliveryQty() {
        return deliveryQty;
    }

    public void setDeliveryQty(int deliveryQty) {
        this.deliveryQty = deliveryQty;
    }

    public int getDeliveryTotalQty() {
        return deliveryTotalQty;
    }

    public void setDeliveryTotalQty(int deliveryTotalQty) {
        this.deliveryTotalQty = deliveryTotalQty;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public double getPaymentTotalAmount() {
        return paymentTotalAmount;
    }

    public void setPaymentTotalAmount(double paymentTotalAmount) {
        this.paymentTotalAmount = paymentTotalAmount;
    }
}
