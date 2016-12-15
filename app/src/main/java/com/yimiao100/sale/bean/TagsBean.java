package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * Created by 亿苗通 on 2016/9/19.
 */
public class TagsBean {
    private String status;
    private int reason;
    private ArrayList<TagListBean> tagList;

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

    public ArrayList<TagListBean> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<TagListBean> tagList) {
        this.tagList = tagList;
    }
}
