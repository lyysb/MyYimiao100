package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * 资讯-照片墙
 * Created by 亿苗通 on 2016/9/20.
 */
public class InformationGroupList {
    private int id;
    private String title;
    private String newsContent;
    private String newsAbstract;
    private String newsSource;
    private int layoutType;
    private int score;
    private int commentNumber;
    private int newsType;
    private String newsTypeName;
    private int isTop;
    private int isPublish;
    private long publishAt;
    private long createdAt;
    private long updatedAt;
    private int userScoreStatus;
    private ArrayList<ImageListBean> imageList;

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

    public int getNewsType() {
        return newsType;
    }

    public void setNewsType(int newsType) {
        this.newsType = newsType;
    }

    public String getNewsTypeName() {
        return newsTypeName;
    }

    public void setNewsTypeName(String newsTypeName) {
        this.newsTypeName = newsTypeName;
    }

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public int getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(int isPublish) {
        this.isPublish = isPublish;
    }

    public long getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(long publishAt) {
        this.publishAt = publishAt;
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

    public ArrayList<ImageListBean> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageListBean> imageList) {
        this.imageList = imageList;
    }
}
