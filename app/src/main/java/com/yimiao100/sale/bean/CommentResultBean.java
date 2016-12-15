package com.yimiao100.sale.bean;

import java.util.List;

/**
 * 分页评论
 * Created by 亿苗通 on 2016/8/15.
 */
public class CommentResultBean {
    private int totalPage;
    private int pageSize;
    private int page;
    /**
     * id : 16
     * objectId : 8
     * userId : 1
     * commentContent : Errors
     * score : 1
     * createdAt : 1471231658000
     * updatedAt : 1471231658000
     * userName : dkal
     * profileImageUrl : http://ob2mg4u3u.bkt.clouddn.com/2016/08/15/19561dbeba794844a9e3cd1cfb7b212c.png
     * userScoreStatus : 0
     */

    private List<CommentListBean> pagedList;

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

    public List<CommentListBean> getCommentList() {
        return pagedList;
    }

    public void setPagedList(List<CommentListBean> pagedList) {
        this.pagedList = pagedList;
    }

    @Override
    public String toString() {
        return "CommentResultBean{" +
                "totalPage=" + totalPage +
                ", pageSize=" + pageSize +
                ", page=" + page +
                ", pagedList=" + pagedList +
                '}';
    }
}
