package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * Created by 亿苗通 on 2016/9/13.
 */
public class AssuranceResult {
    private int totalPage;
    private int pageSize;
    private int page;
    private ArrayList<AssuranceList> pagedList;

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

    public ArrayList<AssuranceList> getPagedList() {
        return pagedList;
    }

    public void setPagedList(ArrayList<AssuranceList> pagedList) {
        this.pagedList = pagedList;
    }
}
