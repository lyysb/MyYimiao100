package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * 厂家列表Bean
 * Created by 亿苗通 on 2016/10/30.
 */
public class VendorListBean {
    private String status;
    private int reason;
    private ArrayList<Vendor> vendorList;

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

    public ArrayList<Vendor> getVendorList() {
        return vendorList;
    }

    public void setVendorList(ArrayList<Vendor> vendorList) {
        this.vendorList = vendorList;
    }
}
