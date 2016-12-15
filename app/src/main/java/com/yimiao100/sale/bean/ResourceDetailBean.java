package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/8/19.
 */
public class ResourceDetailBean {
    private ResourceListBean resourceInfo;


    private String status;

    public ResourceListBean getResourceResult() {
        return resourceInfo;
    }

    public void setResourceInfo(ResourceListBean resourceInfo) {
        this.resourceInfo = resourceInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
