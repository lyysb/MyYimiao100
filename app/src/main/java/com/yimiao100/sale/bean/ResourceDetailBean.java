package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/8/19.
 */
public class ResourceDetailBean {
    private ResourceListBean resourceInfo;

    private int reason;

    private String status;

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public ResourceListBean getResourceInfo() {
        return resourceInfo;
    }

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
