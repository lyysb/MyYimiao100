package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * Created by 亿苗通 on 2016/11/4.
 */
public class ExamInfo {
    private int userId;
    private int vendorId;
    private int categoryId;
    private int productId;
    private int specId;
    private int dosageFormId;
    private double avgScore;
    private int totalQty;
    private int targetQty;
    private double totalAmount;
    private String vendorName;
    private String categoryName;
    private String productName;
    private String spec;
    private String dosageForm;
    private ArrayList<CourseExam> courseExamList;
    private ArrayList<Integer> courseExamItemIdList;

    public ArrayList<Integer> getCourseExamItemIdList() {
        return courseExamItemIdList;
    }

    public void setCourseExamItemIdList(ArrayList<Integer> courseExamItemIdList) {
        this.courseExamItemIdList = courseExamItemIdList;
    }

    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSpecId() {
        return specId;
    }

    public void setSpecId(int specId) {
        this.specId = specId;
    }

    public int getDosageFormId() {
        return dosageFormId;
    }

    public void setDosageFormId(int dosageFormId) {
        this.dosageFormId = dosageFormId;
    }

    public double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public int getTargetQty() {
        return targetQty;
    }

    public void setTargetQty(int targetQty) {
        this.targetQty = targetQty;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public ArrayList<CourseExam> getCourseExamList() {
        return courseExamList;
    }

    public void setCourseExamList(ArrayList<CourseExam> courseExamList) {
        this.courseExamList = courseExamList;
    }
}
