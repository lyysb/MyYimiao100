package com.yimiao100.sale.bean;

/**
 * 回款Bean
 * Created by 亿苗通 on 2016/9/12.
 */
public class PaymentBean {
    private String status;
    private int reason;
    private PaymentResult pagedResult;

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

    public PaymentResult getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(PaymentResult pagedResult) {
        this.pagedResult = pagedResult;
    }
}
