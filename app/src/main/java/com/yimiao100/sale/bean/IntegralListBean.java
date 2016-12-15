package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/10/29.
 */

public class IntegralListBean {
    private String status;
    private int reason;
    private IntegralListResult pagedResult;

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

    public IntegralListResult getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(IntegralListResult pagedResult) {
        this.pagedResult = pagedResult;
    }
}
