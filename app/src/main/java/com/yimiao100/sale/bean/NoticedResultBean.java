package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * Created by 亿苗通 on 2016/8/16.
 */
public class NoticedResultBean {
    private int totalPage;
    private int pageSize;
    private int page;
    /**
     * noticeId : 13
     * noticeTitle : 44
     * noticeContent : 56
     * noticeLevel : low
     * senderName : 疫苗圈
     * readStatus : 0
     * createdAt : 1470986437000
     * readAt : null
     */

    private ArrayList<NoticedListBean> pagedList;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<NoticedListBean> getPagedList() {
        return pagedList;
    }

    public void setPagedList(ArrayList<NoticedListBean> pagedList) {
        this.pagedList = pagedList;
    }
}
