package com.yimiao100.sale.bean;

import android.text.TextUtils;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.List;

/**
 * Created by 亿苗通 on 2016/8/3.
 */
public class City implements IPickerViewData {
    private int id;
    private String code;
    private String name;
    private String provinceCode;
    /**
     * id : 1
     * code : 110101
     * name : 东城区
     * cityCode : 110100
     */

    private List<Area> areaList;

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

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public List<Area> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<Area> areaList) {
        this.areaList = areaList;
    }

    @Override
    public String getPickerViewText() {
        if (TextUtils.isEmpty(name)){
            return "";
        }
        return name;
    }
}
