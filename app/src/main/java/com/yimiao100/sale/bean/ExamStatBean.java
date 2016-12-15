package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/11/4.
 */
public class ExamStatBean {
    private String status;
    private int reason;
    private ExamStat stat;

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

    public ExamStat getStat() {
        return stat;
    }

    public void setStat(ExamStat stat) {
        this.stat = stat;
    }
}
