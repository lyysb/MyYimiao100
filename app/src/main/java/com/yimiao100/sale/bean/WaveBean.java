package com.yimiao100.sale.bean;

/**
 * CRM_水波纹统计
 * Created by 亿苗通 on 2016/9/13.
 */
public class WaveBean {
    private String status;
    private int reason;
    private WaveStat statResult;

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

    public WaveStat getStatResult() {
        return statResult;
    }

    public void setStatResult(WaveStat statResult) {
        this.statResult = statResult;
    }
}
