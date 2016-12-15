package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/8/9.
 */
public class SignUpBean {


    /**
     * 用户信息
     */
    private UserInfoBean userInfo;
    /**
     * Token信息
     */

    private TokenInfoBean tokenInfo;

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    private int reason;
    /**
     * 状态
     */
    private String status;

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public TokenInfoBean getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfoBean tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SignUpBean{" +
                "userInfo=" + userInfo +
                ", tokenInfo=" + tokenInfo +
                ", status='" + status + '\'' +
                '}';
    }
}
