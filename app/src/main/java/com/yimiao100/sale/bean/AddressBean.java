package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * 常用地址列表Bean
 * Created by 亿苗通 on 2016/10/27.
 */

public class AddressBean {
    private String status;
    private ArrayList<Address> addresslist;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Address> getAddresslist() {
        return addresslist;
    }

    public void setAddresslist(ArrayList<Address> addresslist) {
        this.addresslist = addresslist;
    }
}
