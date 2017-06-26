package com.yimiao100.sale.bean;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.ArrayList;

/**
 * Created by 亿苗通 on 2016/8/3.
 */
public class Area implements IPickerViewData {
    private int id;
    private int areaId;
    private String code;
    private String name;
    private String areaName;
    private String cityCode;

    private ArrayList<Customer> customerList;

    public ArrayList<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(ArrayList<Customer> customerList) {
        this.customerList = customerList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public String getPickerViewText() {
        return name != null ? name : areaName;
    }
}
