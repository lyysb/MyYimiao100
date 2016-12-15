package com.yimiao100.sale.bean;

/**
 * 积分明细列表Bean
 * Created by 亿苗通 on 2016/10/29.
 */
public class IntegralList {
    private int id;
    private int userId;
    private int objectId;
    private String objectType;
    private String integralType;
    private int integralValue;
    private String objectTitle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getIntegralType() {
        return integralType;
    }

    public void setIntegralType(String integralType) {
        this.integralType = integralType;
    }

    public int getIntegralValue() {
        return integralValue;
    }

    public void setIntegralValue(int integralValue) {
        this.integralValue = integralValue;
    }

    public String getObjectTitle() {
        return objectTitle;
    }

    public void setObjectTitle(String objectTitle) {
        this.objectTitle = objectTitle;
    }
}
