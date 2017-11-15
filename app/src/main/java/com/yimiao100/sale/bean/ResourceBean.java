package com.yimiao100.sale.bean;

/**
 * 资源列表
 * Created by 亿苗通 on 2016/8/19.
 */
public class ResourceBean {

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    private int reason;

    private ResourceResultBean pagedResult;

    private String status;

    public ResourceResultBean getResourceResult() {
        return pagedResult;
    }

    public void setPagedResult(ResourceResultBean pagedResult) {
        this.pagedResult = pagedResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
