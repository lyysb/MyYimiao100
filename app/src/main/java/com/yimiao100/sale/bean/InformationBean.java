package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/8/10.
 */
public class InformationBean {


    private PagedResultBean pagedResult;

    private String status;
    /**
     * @return 分页对象
     */
    public PagedResultBean getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(PagedResultBean pagedResult) {
        this.pagedResult = pagedResult;
    }
    /**
     * @return 标记调用是否成功
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "InformationBean{" +
                "pagedResult=" + pagedResult +
                ", status='" + status + '\'' +
                '}';
    }
}
