package com.yimiao100.sale.bean;

/**
 * 发货Bean
 * Created by 亿苗通 on 2016/9/12.
 */
public class ShipBean {
    private String status;
    private int reason;
    private ShipResult pagedResult;

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

    public ShipResult getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(ShipResult pagedResult) {
        this.pagedResult = pagedResult;
    }
}
