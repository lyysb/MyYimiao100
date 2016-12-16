package com.yimiao100.sale.bean;

/**
 * 错误信息码
 * Created by 亿苗通 on 2016/8/9.
 */
public class ErrorBean {

    private int reason = 0;
    private String status;

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
