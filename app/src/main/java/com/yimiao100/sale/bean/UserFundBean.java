package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/10/29.
 */

public class UserFundBean {
    private String status;
    private int reason;
    private UserFundNew userFund;

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

    public UserFundNew getUserFund() {
        return userFund;
    }

    public void setUserFund(UserFundNew userFund) {
        this.userFund = userFund;
    }
}
