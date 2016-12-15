package com.yimiao100.sale.bean;

import java.util.List;

/**
 * 资讯详情
 * Created by 亿苗通 on 2016/8/12.
 */
public class NewsBean {
    private int id;
    private String title;
    private String newsContent;
    private String newsAbstract;
    private String newsSource;
    private int newsType;
    private String newsTypeName;
    private String integralType;
    private int integralValue;

    public String getNewsTypeName() {
        return newsTypeName;
    }

    public void setNewsTypeName(String newsTypeName) {
        this.newsTypeName = newsTypeName;
    }

    public String getIntegralType() {
        return integralType;
    }

    public void setIntegralType(String integralType) {
        this.integralType = integralType;
    }

    public int getIntegralValue() {
        return integralValue;
    }

    public void setIntegralValue(int integralValue) {
        this.integralValue = integralValue;
    }

    public int getNewsType() {
        return newsType;
    }

    public void setNewsType(int newsType) {
        this.newsType = newsType;
    }

    private int layoutType;
    private int score;
    private int commentNumber;
    private long createdAt;
    private long updatedAt;
    private long publishAt;

    public long getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(long publishAt) {
        this.publishAt = publishAt;
    }

    private int userScoreStatus;
    private int userCollectionStatus;
    private List<ImageListBean> imageList;
    /**
     * id : 2
     * tagName : 测试标签-2
     * createdAt : null
     * updatedAt : null
     */

    private List<TagListBean> tagList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public String getNewsAbstract() {
        return newsAbstract;
    }

    public void setNewsAbstract(String newsAbstract) {
        this.newsAbstract = newsAbstract;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public int getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getUserScoreStatus() {
        return userScoreStatus;
    }

    public void setUserScoreStatus(int userScoreStatus) {
        this.userScoreStatus = userScoreStatus;
    }

    public int getUserCollectionStatus() {
        return userCollectionStatus;
    }

    public void setUserCollectionStatus(int userCollectionStatus) {
        this.userCollectionStatus = userCollectionStatus;
    }

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
}
