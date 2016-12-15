package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * Created by 亿苗通 on 2016/10/27.
 */

public class RegionListBean {
    private ArrayList<Province> provinceList;
    private String status;

    public ArrayList<Province> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(ArrayList<Province> provinceList) {
        this.provinceList = provinceList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
