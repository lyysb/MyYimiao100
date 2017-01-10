package com.yimiao100.sale.bean;

/**
 * 版本更新
 * Created by Michel on 2017/1/6.
 */

public class DataVersionBean {
    private String status;
    private int reason;
    private DataVersion version;

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

    public DataVersion getVersion() {
        return version;
    }

    public void setVersion(DataVersion version) {
        this.version = version;
    }
}
