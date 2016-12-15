package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/9/13.
 */
public class Payment {
    private int statYear;
    private int statMonth;
    private int paymentQuota;
    private int paymentAmount;

    public int getStatYear() {
        return statYear;
    }

    public void setStatYear(int statYear) {
        this.statYear = statYear;
    }

    public int getStatMonth() {
        return statMonth;
    }

    public void setStatMonth(int statMonth) {
        this.statMonth = statMonth;
    }

    public int getPaymentQuota() {
        return paymentQuota;
    }

    public void setPaymentQuota(int paymentQuota) {
        this.paymentQuota = paymentQuota;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(int paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
}
