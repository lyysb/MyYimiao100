package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * Created by 亿苗通 on 2016/10/29.
 */
public class IntegralListResult {
    private int totalPage;
    private int pageSize;
    private int page;
    private ArrayList<IntegralList> pagedList;

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

    public ArrayList<IntegralList> getPagedList() {
        return pagedList;
    }

    public void setPagedList(ArrayList<IntegralList> pagedList) {
        this.pagedList = pagedList;
    }
}
