package com.yimiao100.sale.bean;

/**
 * 财富——bean
 * Created by 亿苗通 on 2016/9/13.
 */
public class RichesBean {
    private String status;
    private int reason;
    private UserFund userFund;

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

    public UserFund getUserFund() {
        return userFund;
    }

    public void setUserFund(UserFund userFund) {
        this.userFund = userFund;
    }
}
