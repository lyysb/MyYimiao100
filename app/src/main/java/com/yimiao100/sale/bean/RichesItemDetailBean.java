package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/11/9.
 */

public class RichesItemDetailBean {
    private String status;
    private int reason;
    private AccountDetail accountDetail;

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

    public AccountDetail getAccountDetail() {
        return accountDetail;
    }

    public void setAccountDetail(AccountDetail accountDetail) {
        this.accountDetail = accountDetail;
    }
}
