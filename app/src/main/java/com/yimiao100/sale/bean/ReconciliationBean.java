package com.yimiao100.sale.bean;

/**
 * 对账列表
 * Created by 亿苗通 on 2016/9/1.
 */
public class ReconciliationBean {
    private String status;
    private int reason;
    private ReconciliationResult pagedResult;

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

    public ReconciliationResult getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(ReconciliationResult pagedResult) {
        this.pagedResult = pagedResult;
    }
}
