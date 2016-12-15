package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/11/11.
 */
public class GoodsBean {
    private String status;
    private int reason;
    private GoodsResult pagedResult;

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

    public GoodsResult getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(GoodsResult pagedResult) {
        this.pagedResult = pagedResult;
    }
}
