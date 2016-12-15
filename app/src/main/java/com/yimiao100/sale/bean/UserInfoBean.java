package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/8/9.
 */
public class UserInfoBean {
    private int id;
    private String accountNumber;
    private String cnName;
    private Object sex;
    private Object age;
    private String phoneNumber;
    private String email;
    private int provinceId;
    private int cityId;
    private int areaId;
    private String idNumber;
    private String profileImagePath;
    private String profileImageUrl;
    private String signupSource;
    private long createdAt;
    private long updatedAt;
    private String provinceName;
    private String cityName;
    private String areaName;
    /** 用户id */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    /** 用户账号 */
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    /** 用户姓名 */
    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public Object getSex() {
        return sex;
    }

    public void setSex(Object sex) {
        this.sex = sex;
    }

    public Object getAge() {
        return age;
    }

    public void setAge(Object age) {
        this.age = age;
    }
    /** 电话 */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /** 邮箱 */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    /** 省id */
    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
    /** 市id*/
    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
    /** 区域id */
    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }
    /** 身份证号 */
    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
    /** 用户头像地址 */
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    /** 注册来源 */
    public String getSignupSource() {
        return signupSource;
    }

    public void setSignupSource(String signupSource) {
        this.signupSource = signupSource;
    }
    /** 创建时间 */
    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    /** 更新时间 */
    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    /** 省名称 */
    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
    /** 市名称 */
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    /** 区县名称 */
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public String toString() {
        return "UserInfoBean{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", cnName='" + cnName + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", provinceId=" + provinceId +
                ", cityId=" + cityId +
                ", areaId=" + areaId +
                ", idNumber='" + idNumber + '\'' +
                ", profileImagePath='" + profileImagePath + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", signupSource='" + signupSource + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", provinceName='" + provinceName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", areaName='" + areaName + '\'' +
                '}';
    }
}
