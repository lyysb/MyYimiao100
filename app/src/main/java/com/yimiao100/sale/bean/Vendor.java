package com.yimiao100.sale.bean;

/**
 * 厂家bean
 * Created by 亿苗通 on 2016/10/30.
 */
public class Vendor {
    private int id;
    private String vendorName;
    private String logoImageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getLogoImageUrl() {
        return logoImageUrl;
    }

    public void setLogoImageUrl(String logoImageUrl) {
        this.logoImageUrl = logoImageUrl;
    }
}
