package com.yimiao100.sale.bean;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.List;

/**
 * Created by 亿苗通 on 2016/8/3.
 */
public class Province implements IPickerViewData {

    /**
     * id : 1
     * code : 110000
     * name : 北京市
     * cityList : [{"id":1,"code":"110100","name":"市辖区","provinceCode":"110000","areaList":[{"id":1,"code":"110101","name":"东城区","cityCode":"110100"},{"id":2,"code":"110102","name":"西城区","cityCode":"110100"},{"id":3,"code":"110103","name":"崇文区","cityCode":"110100"},{"id":4,"code":"110104","name":"宣武区","cityCode":"110100"},{"id":5,"code":"110105","name":"朝阳区","cityCode":"110100"},{"id":6,"code":"110106","name":"丰台区","cityCode":"110100"},{"id":7,"code":"110107","name":"石景山区","cityCode":"110100"},{"id":8,"code":"110108","name":"海淀区","cityCode":"110100"},{"id":9,"code":"110109","name":"门头沟区","cityCode":"110100"},{"id":10,"code":"110111","name":"房山区","cityCode":"110100"},{"id":11,"code":"110112","name":"通州区","cityCode":"110100"},{"id":12,"code":"110113","name":"顺义区","cityCode":"110100"},{"id":13,"code":"110114","name":"昌平区","cityCode":"110100"},{"id":14,"code":"110115","name":"大兴区","cityCode":"110100"},{"id":15,"code":"110116","name":"怀柔区","cityCode":"110100"},{"id":16,"code":"110117","name":"平谷区","cityCode":"110100"}]},{"id":2,"code":"110200","name":"县","provinceCode":"110000","areaList":[{"id":17,"code":"110228","name":"密云县","cityCode":"110200"},{"id":18,"code":"110229","name":"延庆县","cityCode":"110200"}]}]
     */

    private int id;
    private String code;
    private String name;
    /**
     * id : 1
     * code : 110100
     * name : 市辖区
     * provinceCode : 110000
     * areaList : [{"id":1,"code":"110101","name":"东城区","cityCode":"110100"},{"id":2,"code":"110102","name":"西城区","cityCode":"110100"},{"id":3,"code":"110103","name":"崇文区","cityCode":"110100"},{"id":4,"code":"110104","name":"宣武区","cityCode":"110100"},{"id":5,"code":"110105","name":"朝阳区","cityCode":"110100"},{"id":6,"code":"110106","name":"丰台区","cityCode":"110100"},{"id":7,"code":"110107","name":"石景山区","cityCode":"110100"},{"id":8,"code":"110108","name":"海淀区","cityCode":"110100"},{"id":9,"code":"110109","name":"门头沟区","cityCode":"110100"},{"id":10,"code":"110111","name":"房山区","cityCode":"110100"},{"id":11,"code":"110112","name":"通州区","cityCode":"110100"},{"id":12,"code":"110113","name":"顺义区","cityCode":"110100"},{"id":13,"code":"110114","name":"昌平区","cityCode":"110100"},{"id":14,"code":"110115","name":"大兴区","cityCode":"110100"},{"id":15,"code":"110116","name":"怀柔区","cityCode":"110100"},{"id":16,"code":"110117","name":"平谷区","cityCode":"110100"}]
     */

    private List<City> cityList;

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

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

    @Override
    public String getPickerViewText() {
        return name;
    }

}
