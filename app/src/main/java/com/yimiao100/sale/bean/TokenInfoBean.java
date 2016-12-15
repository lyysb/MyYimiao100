package com.yimiao100.sale.bean;

/**
 * Created by 亿苗通 on 2016/8/9.
 */
public class TokenInfoBean {
    private long accessTokenExpiredAt;
    private String accessToken;
    private String accessTokenExpireInterval;
    private String refreshToken;

    /** Access token过期时间 */
    public long getAccessTokenExpiredAt() {
        return accessTokenExpiredAt;
    }

    public void setAccessTokenExpiredAt(long accessTokenExpiredAt) {
        this.accessTokenExpiredAt = accessTokenExpiredAt;
    }

    /** Access token */
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /** Access token过期时间间隔 */
    public String getAccessTokenExpireInterval() {
        return accessTokenExpireInterval;
    }

    public void setAccessTokenExpireInterval(String accessTokenExpireInterval) {
        this.accessTokenExpireInterval = accessTokenExpireInterval;
    }

    /** Refresh token */
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "TokenInfoBean{" +
                "accessTokenExpiredAt=" + accessTokenExpiredAt +
                ", accessToken='" + accessToken + '\'' +
                ", accessTokenExpireInterval='" + accessTokenExpireInterval + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
