package com.yimiao100.sale.bean;

/**
 * 获取当前用户通知列表
 * Created by 亿苗通 on 2016/8/16.
 */
public class NoticeBean {

    /**
     * pagedList : [{"noticeId":13,"noticeTitle":"44","noticeContent":"56","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986437000,"readAt":null},{"noticeId":12,"noticeTitle":"444","noticeContent":"55","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986429000,"readAt":null},{"noticeId":11,"noticeTitle":"555","noticeContent":"55","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986419000,"readAt":null},{"noticeId":10,"noticeTitle":"99","noticeContent":"99","noticeLevel":"middle","senderName":"疫苗圈","readStatus":0,"createdAt":1470986411000,"readAt":null},{"noticeId":9,"noticeTitle":"88","noticeContent":"88","noticeLevel":"high","senderName":"疫苗圈","readStatus":0,"createdAt":1470986405000,"readAt":null},{"noticeId":8,"noticeTitle":"667","noticeContent":"777","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986398000,"readAt":null},{"noticeId":7,"noticeTitle":"66","noticeContent":"666","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986388000,"readAt":null},{"noticeId":6,"noticeTitle":"3333","noticeContent":"33f'f'd'v ","noticeLevel":"high","senderName":"疫苗圈","readStatus":0,"createdAt":1470986380000,"readAt":null},{"noticeId":5,"noticeTitle":"测试4","noticeContent":"4444","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986299000,"readAt":null},{"noticeId":4,"noticeTitle":"111","noticeContent":"111","noticeLevel":"high","senderName":"测试厂家-1","readStatus":0,"createdAt":1470986281000,"readAt":null}]
     * totalPage : 2
     * pageSize : 10
     * page : 0
     */

    private NoticedResultBean pagedResult;
    /**
     * pagedResult : {"pagedList":[{"noticeId":13,"noticeTitle":"44","noticeContent":"56","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986437000,"readAt":null},{"noticeId":12,"noticeTitle":"444","noticeContent":"55","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986429000,"readAt":null},{"noticeId":11,"noticeTitle":"555","noticeContent":"55","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986419000,"readAt":null},{"noticeId":10,"noticeTitle":"99","noticeContent":"99","noticeLevel":"middle","senderName":"疫苗圈","readStatus":0,"createdAt":1470986411000,"readAt":null},{"noticeId":9,"noticeTitle":"88","noticeContent":"88","noticeLevel":"high","senderName":"疫苗圈","readStatus":0,"createdAt":1470986405000,"readAt":null},{"noticeId":8,"noticeTitle":"667","noticeContent":"777","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986398000,"readAt":null},{"noticeId":7,"noticeTitle":"66","noticeContent":"666","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986388000,"readAt":null},{"noticeId":6,"noticeTitle":"3333","noticeContent":"33f'f'd'v ","noticeLevel":"high","senderName":"疫苗圈","readStatus":0,"createdAt":1470986380000,"readAt":null},{"noticeId":5,"noticeTitle":"测试4","noticeContent":"4444","noticeLevel":"low","senderName":"疫苗圈","readStatus":0,"createdAt":1470986299000,"readAt":null},{"noticeId":4,"noticeTitle":"111","noticeContent":"111","noticeLevel":"high","senderName":"测试厂家-1","readStatus":0,"createdAt":1470986281000,"readAt":null}],"totalPage":2,"pageSize":10,"page":0}
     * status : success
     */

    private String status;

    public NoticedResultBean getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(NoticedResultBean pagedResult) {
        this.pagedResult = pagedResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
