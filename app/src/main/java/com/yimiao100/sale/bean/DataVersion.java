package com.yimiao100.sale.bean;

/**
 * Created by Michel on 2017/1/6.
 */

public class DataVersion {
    private int id;
    private String versionKey;
    private String versionCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersionKey() {
        return versionKey;
    }

    public void setVersionKey(String versionKey) {
        this.versionKey = versionKey;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}
