package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/9/13.
 */
public class UserFund {
    private double userId;
    private double totalAmount;
    private double totalWithdraw;
    private double corporateWithdraw;
    private double personalWithdraw;
    private double saleApplyWithdraw;
    private double depositWithdraw;

    public double getUserId() {
        return userId;
    }

    public void setUserId(double userId) {
        this.userId = userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotalWithdraw() {
        return totalWithdraw;
    }

    public void setTotalWithdraw(double totalWithdraw) {
        this.totalWithdraw = totalWithdraw;
    }

    public double getCorporateWithdraw() {
        return corporateWithdraw;
    }

    public void setCorporateWithdraw(double corporateWithdraw) {
        this.corporateWithdraw = corporateWithdraw;
    }

    public double getPersonalWithdraw() {
        return personalWithdraw;
    }

    public void setPersonalWithdraw(double personalWithdraw) {
        this.personalWithdraw = personalWithdraw;
    }

    public double getSaleApplyWithdraw() {
        return saleApplyWithdraw;
    }

    public void setSaleApplyWithdraw(double saleApplyWithdraw) {
        this.saleApplyWithdraw = saleApplyWithdraw;
    }

    public double getDepositWithdraw() {
        return depositWithdraw;
    }

    public void setDepositWithdraw(double depositWithdraw) {
        this.depositWithdraw = depositWithdraw;
    }
}
