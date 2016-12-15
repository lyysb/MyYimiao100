package com.yimiao100.sale.bean;

/**
 * 财富详情-列表
 * Created by 亿苗通 on 2016/9/21.
 */
public class RichesDetailBean {
    private String status;
    private int reason;
    private RichesDetailResult pagedResult;

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

    public RichesDetailResult getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(RichesDetailResult pagedResult) {
        this.pagedResult = pagedResult;
    }
}
