package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * 账单详情Bean
 * Created by 亿苗通 on 2016/9/1.
 */
public class ReconciliationDetailBean {
    private String status;
    private int reason;
    private ArrayList<ReconciliationDetail> orderItemList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public ArrayList<ReconciliationDetail> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(ArrayList<ReconciliationDetail> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
