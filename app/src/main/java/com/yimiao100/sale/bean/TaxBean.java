package com.yimiao100.sale.bean;

/**
 * Created by Michel on 2017/3/4.
 */

public class TaxBean {
    private String status;
    private int reason;
    private Tax tax;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }
}
