package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/11/1.
 */

public class OpenClassBean {
    private String status;
    private int reason;
    private OpenClassResult pagedResult;

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

    public OpenClassResult getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(OpenClassResult pagedResult) {
        this.pagedResult = pagedResult;
    }
}
