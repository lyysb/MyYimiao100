package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * 对公账户Bean
 * Created by 亿苗通 on 2016/8/19.
 */
public class CorporateBean {
    private String accountName;
    private String corporateAccount;
    private String bankName;
    private String corporatePhoneNumber;

    private String cnName;
    private String personalPhoneNumber;
    private String qq;
    private String email;
    private String idNumber;

    private String bizLicenceUrl;
    private String personalPhotoUrl;
    private String idPhotoUrl;
    private String accountStatus;
    private String experience;
    private String advantage;

    private String accountStatusName;

    public String getAccountStatusName() {
        return accountStatusName;
    }

    public void setAccountStatusName(String accountStatusName) {
        this.accountStatusName = accountStatusName;
    }

    private ArrayList<Experience> experienceList;

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getAdvantage() {
        return advantage;
    }

    public void setAdvantage(String advantage) {
        this.advantage = advantage;
    }

    public ArrayList<Experience> getExperienceList() {
        return experienceList;
    }

    public void setExperienceList(ArrayList<Experience> experienceList) {
        this.experienceList = experienceList;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonalPhotoUrl() {
        return personalPhotoUrl;
    }

    public void setPersonalPhotoUrl(String personalPhotoUrl) {
        this.personalPhotoUrl = personalPhotoUrl;
    }

    public String getIdPhotoUrl() {
        return idPhotoUrl;
    }

    public void setIdPhotoUrl(String idPhotoUrl) {
        this.idPhotoUrl = idPhotoUrl;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getPersonalPhoneNumber() {
        return personalPhoneNumber;
    }

    public void setPersonalPhoneNumber(String personalPhoneNumber) {
        this.personalPhoneNumber = personalPhoneNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }



    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCorporatePhoneNumber() {
        return corporatePhoneNumber;
    }

    public void setCorporatePhoneNumber(String corporatePhoneNumber) {
        this.corporatePhoneNumber = corporatePhoneNumber;
    }

    public String getBizLicenceUrl() {
        return bizLicenceUrl;
    }

    public void setBizLicenceUrl(String bizLicenceUrl) {
        this.bizLicenceUrl = bizLicenceUrl;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCorporateAccount() {
        return corporateAccount;
    }

    public void setCorporateAccount(String corporateAccount) {
        this.corporateAccount = corporateAccount;
    }
}
