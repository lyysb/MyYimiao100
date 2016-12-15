package com.yimiao100.sale.bean;

/**
 * CDC 列表数据
 * Created by 亿苗通 on 2016/8/31.
 */
public class CDCBean {
    private String status;
    private int reason;
    private CDCResult pagedResult;

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

    public CDCResult getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(CDCResult pagedResult) {
        this.pagedResult = pagedResult;
    }
}
