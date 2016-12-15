package com.yimiao100.sale.bean;

import android.text.TextUtils;

import com.bigkoo.pickerview.model.IPickerViewData;

/**
 * Created by 亿苗通 on 2016/8/3.
 */
public class Area implements IPickerViewData {
    private int id;
    private String code;
    private String name;
    private String cityCode;

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

    @Override
    public String getPickerViewText() {
        if (TextUtils.isEmpty(name)){
            return "";
        }
        return name;
    }
}
