package com.yimiao100.sale.bean;

/**
 * 用户资金信息Bean
 * Created by 亿苗通 on 2016/10/29.
 */
public class UserFundNew {
    private int userId;
    private double totalAmount;
    private double totalSale;
    private double totalExamReward;
    private double corporateSale;
    private double personalSale;
    private double corporateExamReward;
    private double personalExamReward;
    private double deposit;
    private int integral;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(double totalSale) {
        this.totalSale = totalSale;
    }

    public double getTotalExamReward() {
        return totalExamReward;
    }

    public void setTotalExamReward(double totalExamReward) {
        this.totalExamReward = totalExamReward;
    }

    public double getCorporateSale() {
        return corporateSale;
    }

    public void setCorporateSale(double corporateSale) {
        this.corporateSale = corporateSale;
    }

    public double getPersonalSale() {
        return personalSale;
    }

    public void setPersonalSale(double personalSale) {
        this.personalSale = personalSale;
    }

    public double getCorporateExamReward() {
        return corporateExamReward;
    }

    public void setCorporateExamReward(double corporateExamReward) {
        this.corporateExamReward = corporateExamReward;
    }

    public double getPersonalExamReward() {
        return personalExamReward;
    }

    public void setPersonalExamReward(double personalExamReward) {
        this.personalExamReward = personalExamReward;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }
}
