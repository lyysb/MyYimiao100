package com.yimiao100.sale.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 课程详情对象
 * Created by 亿苗通 on 2016/10/18.
 */
public class Course implements Parcelable {

    private int id;
    private int vendorId;
    private String vendorName;
    private String courseName;
    private String courseDesc;
    private String suitCrowds;
    private String courseType;
    private String courseTypeName;
    private String integralType;
    private String integralTypeName;
    private int integralValue;
    private String videoPath;
    private String videoUrl;
    private int isExam;
    private int isPublish;
    private int examDuration;
    private int playCount;
    private long startAt;
    private long endAt;
    private long publishAt;
    private long createdAt;
    private long updateAt;
    private ArrayList<QuestionList> questionList;

    private int integralStatus;
    private int collectionStatus;
    private int examStatus;
    private int examScore;


    public int getIntegralStatus() {
        return integralStatus;
    }

    public void setIntegralStatus(int integralStatus) {
        this.integralStatus = integralStatus;
    }

    public int getCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(int collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    public int getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(int examStatus) {
        this.examStatus = examStatus;
    }

    public int getExamScore() {
        return examScore;
    }

    public void setExamScore(int examScore) {
        this.examScore = examScore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    public void setCourseDesc(String courseDesc) {
        this.courseDesc = courseDesc;
    }

    public String getSuitCrowds() {
        return suitCrowds;
    }

    public void setSuitCrowds(String suitCrowds) {
        this.suitCrowds = suitCrowds;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCourseTypeName() {
        return courseTypeName;
    }

    public void setCourseTypeName(String courseTypeName) {
        this.courseTypeName = courseTypeName;
    }

    public String getIntegralType() {
        return integralType;
    }

    public void setIntegralType(String integralType) {
        this.integralType = integralType;
    }

    public String getIntegralTypeName() {
        return integralTypeName;
    }

    public void setIntegralTypeName(String integralTypeName) {
        this.integralTypeName = integralTypeName;
    }

    public int getIntegralValue() {
        return integralValue;
    }

    public void setIntegralValue(int integralValue) {
        this.integralValue = integralValue;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getIsExam() {
        return isExam;
    }

    public void setIsExam(int isExam) {
        this.isExam = isExam;
    }

    public int getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(int isPublish) {
        this.isPublish = isPublish;
    }

    public int getExamDuration() {
        return examDuration;
    }

    public void setExamDuration(int examDuration) {
        this.examDuration = examDuration;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public long getStartAt() {
        return startAt;
    }

    public void setStartAt(long startAt) {
        this.startAt = startAt;
    }

    public long getEndAt() {
        return endAt;
    }

    public void setEndAt(long endAt) {
        this.endAt = endAt;
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

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public ArrayList<QuestionList> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(ArrayList<QuestionList> questionList) {
        this.questionList = questionList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.vendorId);
        dest.writeString(this.vendorName);
        dest.writeString(this.courseName);
        dest.writeString(this.courseDesc);
        dest.writeString(this.suitCrowds);
        dest.writeString(this.courseType);
        dest.writeString(this.courseTypeName);
        dest.writeString(this.integralType);
        dest.writeString(this.integralTypeName);
        dest.writeInt(this.integralValue);
        dest.writeString(this.videoPath);
        dest.writeString(this.videoUrl);
        dest.writeInt(this.isExam);
        dest.writeInt(this.isPublish);
        dest.writeInt(this.examDuration);
        dest.writeInt(this.playCount);
        dest.writeLong(this.startAt);
        dest.writeLong(this.endAt);
        dest.writeLong(this.publishAt);
        dest.writeLong(this.createdAt);
        dest.writeLong(this.updateAt);
        dest.writeList(this.questionList);
        dest.writeInt(this.integralStatus);
        dest.writeInt(this.collectionStatus);
        dest.writeInt(this.examStatus);
        dest.writeInt(this.examScore);
    }

    public Course() {
    }

    protected Course(Parcel in) {
        this.id = in.readInt();
        this.vendorId = in.readInt();
        this.vendorName = in.readString();
        this.courseName = in.readString();
        this.courseDesc = in.readString();
        this.suitCrowds = in.readString();
        this.courseType = in.readString();
        this.courseTypeName = in.readString();
        this.integralType = in.readString();
        this.integralTypeName = in.readString();
        this.integralValue = in.readInt();
        this.videoPath = in.readString();
        this.videoUrl = in.readString();
        this.isExam = in.readInt();
        this.isPublish = in.readInt();
        this.examDuration = in.readInt();
        this.playCount = in.readInt();
        this.startAt = in.readLong();
        this.endAt = in.readLong();
        this.publishAt = in.readLong();
        this.createdAt = in.readLong();
        this.updateAt = in.readLong();
        this.questionList = new ArrayList<QuestionList>();
        in.readList(this.questionList, QuestionList.class.getClassLoader());
        this.integralStatus = in.readInt();
        this.collectionStatus = in.readInt();
        this.examStatus = in.readInt();
        this.examScore = in.readInt();
    }

    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
        public Course createFromParcel(Parcel source) {
            return new Course(source);
        }

        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
}
