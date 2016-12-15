package com.yimiao100.sale.bean;

import java.util.List;

/**
 * 资讯列表
 * Created by 亿苗通 on 2016/8/10.
 */
public class PagedListBean {
    private int id;
    private String title;
    private String newsContent;
    private String newsAbstract;
    private String newsSource;
    private int layoutType;
    private int score;
    private int commentNumber;
    private int isTop;
    private long publishAt;

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    private boolean readed;

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public long getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(long publishAt) {
        this.publishAt = publishAt;
    }

    private long createdAt;
    private long updatedAt;
    private int userScoreStatus;
    /**
     * id : 6
     * imagePath : 2016/08/10/e9996dc07b0f4069880d792c32fdb036.jpg
     * imageUrl : http://ob8083hy1.bkt.clouddn.com/2016/08/10/e9996dc07b0f4069880d792c32fdb036.jpg
     * createdAt : null
     * updatedAt : null
     */

    private List<ImageListBean> imageList;
    private List<TagListBean> tagList;

    /**
     * @return 资讯对象id
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return 资讯标题
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return 资讯内容
     */
    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    /**
     * @return 资讯摘要
     */
    public String getNewsAbstract() {
        return newsAbstract;
    }

    public void setNewsAbstract(String newsAbstract) {
        this.newsAbstract = newsAbstract;
    }

    /**
     * @return 资讯来源
     */
    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    /**
     * @return 列表页布局类型
     */
    public int getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    /**
     * @return 评分数
     */
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return 评论数
     */
    public int getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    /**
     * @return 创建时间
     */
    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return 更新时间
     */
    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return 用户评分状态
     */
    public int getUserScoreStatus() {
        return userScoreStatus;
    }

    public void setUserScoreStatus(int userScoreStatus) {
        this.userScoreStatus = userScoreStatus;
    }

    /**
     * @return 图片列表
     */
    public List<ImageListBean> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageListBean> imageList) {
        this.imageList = imageList;
    }

    public List<TagListBean> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagListBean> tagList) {
        this.tagList = tagList;
    }

    @Override
    public String toString() {
        return "PagedListBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", newsContent='" + newsContent + '\'' +
                ", newsAbstract='" + newsAbstract + '\'' +
                ", newsSource='" + newsSource + '\'' +
                ", layoutType=" + layoutType +
                ", score=" + score +
                ", commentNumber=" + commentNumber +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userScoreStatus=" + userScoreStatus +
                '}';
    }
}
