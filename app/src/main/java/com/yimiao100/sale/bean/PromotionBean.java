package com.yimiao100.sale.bean;

/**
 * 保证金可提现Bean
 * Created by 亿苗通 on 2016/9/13.
 */
public class PromotionBean {
    private String status;
    private int reason;
    private PromotionResult pagedResult;
    private Stat stat;

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

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

    public PromotionResult getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(PromotionResult pagedResult) {
        this.pagedResult = pagedResult;
    }
}
