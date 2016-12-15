package com.yimiao100.sale.bean;

import java.util.List;

/**
 * Created by 亿苗通 on 2016/8/15.
 */
public class ErrorReasonBean {

    /**
     * errorReasonList : [{"errorReason":"广告","id":1},{"errorReason":"过时","id":2},{"errorReason":"重复","id":3},{"errorReason":"错别字","id":4},{"errorReason":"色情低俗","id":5},{"errorReason":"标题夸张","id":6},{"errorReason":"观点错误","id":7},{"errorReason":"与事实不符","id":8},{"errorReason":"内容格式错误","id":9}]
     * status : success
     */

    private String status;
    /**
     * errorReason : 广告
     * id : 1
     */

    private List<ErrorReasonListBean> errorReasonList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ErrorReasonListBean> getErrorReasonList() {
        return errorReasonList;
    }

    public void setErrorReasonList(List<ErrorReasonListBean> errorReasonList) {
        this.errorReasonList = errorReasonList;
    }

}
