package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/8/19.
 */
public class UserAccountBean {

    private CorporateBean corporate;


    private PersonalBean personal;

    public CorporateBean getCorporate() {
        return corporate;
    }

    public void setCorporate(CorporateBean corporate) {
        this.corporate = corporate;
    }

    public PersonalBean getPersonal() {
        return personal;
    }

    public void setPersonal(PersonalBean personal) {
        this.personal = personal;
    }
}
