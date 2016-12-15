package com.yimiao100.sale.bean;

/**
 * 评论内容
 * Created by 亿苗通 on 2016/8/15.
 */
public class CommentListBean {
    private int id;
    private int objectId;
    private int userId;
    private String commentContent;
    private int score;
    private long createdAt;
    private long updatedAt;
    private String userName;
    private String profileImageUrl;
    private int userScoreStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getUserScoreStatus() {
        return userScoreStatus;
    }

    public void setUserScoreStatus(int userScoreStatus) {
        this.userScoreStatus = userScoreStatus;
    }
}
