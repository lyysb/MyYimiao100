package com.yimiao100.sale.bean;

/**
 * 用户账户
 * Created by 亿苗通 on 2016/8/19.
 */
public class UserBean {



    private UserAccountBean userAccount;
    /**
     * userAccount : {"corporate":{"bankAccountName":"name","phoneNumber":"17710785423","bizLicenceUrl":"http://ob2m577lw.bkt.clouddn.com/2016/08/19/ca50ab3726e14c01a24eceb35664ba37.png","bankName":"农行","bankAccountNumber":"6222021001116245702"},"personal":{"phoneNumber":"18210816525","cnName":"雷鹏","bankName":"花旗银行","bankAccountNumber":"6210630247744086"}}
     * status : success
     */

    private String status;

    private int reason;

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public UserAccountBean getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccountBean userAccount) {
        this.userAccount = userAccount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
