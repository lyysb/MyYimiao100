package com.yimiao100.sale.utils;

import android.app.Activity;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.yimiao100.sale.base.ActivityCollector;
import com.yimiao100.sale.bean.CorporateBean;
import com.yimiao100.sale.bean.PersonalBean;
import com.yimiao100.sale.bean.UserAccountBean;
import com.yimiao100.sale.bean.UserBean;
import com.yimiao100.sale.ext.JSON;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

import static com.zhy.http.okhttp.OkHttpUtils.post;

/**
 * 数据
 * Created by Michel on 2017/2/21.
 */

public class DataUtil {

    private static final String URL_USER_ACCOUNT = Constant.BASE_URL + "/api/user/get_user_account";

    private static final String ACCESS_TOKEN = "X-Authorization-Token";

    public static void updateUserAccount(final String accessToken) {
        OkHttpUtils.post().url(URL_USER_ACCOUNT).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("updateUserAccount：" + response);
                UserBean userBean = JSON.parseObject(response, UserBean.class);
                Activity topActivity = ActivityCollector.getTopActivity();
                switch (userBean.getStatus()) {
                    case "success":
                        UserAccountBean userAccount = userBean.getUserAccount();
                        if (userAccount.getCorporate() != null) {
                            //更新对公账户数据
                            updateCorporate(topActivity, userAccount.getCorporate());
                        }
                        if (userAccount.getPersonal() != null) {
                            // 更新个人主体数据
                            updatePersonal(topActivity, userAccount.getPersonal());
                        }
                        break;
                    case "failure":
                        Util.showError(topActivity, userBean.getReason());
                        break;
                }
            }
        });
    }


    public static void updateUserAccount(final String accessToken, final onSuccessListener listener) {
        post().url(URL_USER_ACCOUNT).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("updateUserAccount：" + response);
                UserBean userBean = JSON.parseObject(response, UserBean.class);
                Activity topActivity = ActivityCollector.getTopActivity();
                switch (userBean.getStatus()) {
                    case "success":
                        UserAccountBean userAccount = userBean.getUserAccount();
                        if (userAccount.getCorporate() != null) {
                            //更新对公账户数据
                            updateCorporate(topActivity, userAccount.getCorporate());
                        }
                        if (userAccount.getPersonal() != null) {
                            // 更新个人主体数据
                            updatePersonal(topActivity, userAccount.getPersonal());
                        }
                        if (listener != null) {
                            listener.echoData(userAccount);
                        }
                        break;
                    case "failure":
                        Util.showError(topActivity, userBean.getReason());
                        break;
                }
            }
        });
    }

    private static void updatePersonal(Activity activity, PersonalBean personal) {
        // 记录个人主体存在
        SharePreferenceUtil.put(activity, Constant.PERSONAL_EXIT, true);
        // 姓名
        SharePreferenceUtil.put(activity, Constant.PERSONAL_CN_NAME, personal.getCnName());
        // 银行卡号
        SharePreferenceUtil.put(activity, Constant.PERSONAL_BANK_CARD_NUMBER, personal.getBankCardNumber());
        // 审核状态
        SharePreferenceUtil.put(activity, Constant.PERSONAL_ACCOUNT_STATUS, personal.getAccountStatus());
        // 身份证号
        SharePreferenceUtil.put(activity, Constant.PERSONAL_ID_CARD, personal.getIdNumber());
        // 联系电话
        SharePreferenceUtil.put(activity, Constant.PERSONAL_PHONE_NUMBER, personal.getPersonalPhoneNumber());
    }

    public static void updateUserAccount(String accessToken, final TwinklingRefreshLayout refreshLayout, final onSuccessListener listener) {
        post().url(URL_USER_ACCOUNT).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                if (refreshLayout.isShown()) {
                    refreshLayout.finishRefreshing();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("updateUserAccount：" + response);
                if (refreshLayout.isShown()) {
                    refreshLayout.finishRefreshing();
                }
                UserBean userBean = JSON.parseObject(response, UserBean.class);
                Activity topActivity = ActivityCollector.getTopActivity();
                switch (userBean.getStatus()) {
                    case "success":
                        UserAccountBean userAccount = userBean.getUserAccount();
                        if (userAccount.getCorporate() != null) {
                            //更新对公账户数据
                            updateCorporate(topActivity, userAccount.getCorporate());
                        }
                        if (userAccount.getPersonal() != null) {
                            // 更新个人主体数据
                            updatePersonal(topActivity, userAccount.getPersonal());
                        }
                        if (listener != null) {
                            listener.echoData(userAccount);
                        }
                        break;
                    case "failure":
                        Util.showError(topActivity, userBean.getReason());
                        break;
                }
            }
        });
    }

    public interface onSuccessListener {
        void echoData(UserAccountBean userAccount);

    }

    private static void updateCorporate(Activity topActivity, CorporateBean corporate) {
        //记录对公账户存在
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_EXIT, true);
        //对公账户-开户名称
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_ACCOUNT_NAME, corporate.getAccountName());
        //对公账户-公司账号
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_ACCOUNT_NUMBER, corporate.getCorporateAccount());
        //对公账户-开户银行
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_BANK_NAME, corporate.getBankName());
        //对公账户-公司电话号码(固话)
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_PHONE_NUMBER, corporate.getCorporatePhoneNumber());

        //对公账户-企业营业执照地址
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_BIZ_LICENCE_URL, corporate.getBizLicenceUrl());

        //对公账户-姓名
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_CN_NAME, corporate.getCnName());
        //对公账户-身份证号
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_ID_NUMBER, corporate.getIdNumber());
        //对公账户-电话
        SharePreferenceUtil.put(topActivity, Constant.CORPORATION_PERSONAL_PHONE_NUMBER, corporate.getPersonalPhoneNumber());
        //对公账户-QQ
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_QQ, corporate.getQq());
        //对公账户-邮箱
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_EMAIL, corporate.getEmail());
        //对公账户-证件照1
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_PERSONAL_URL, corporate.getPersonalPhotoUrl());
        //对公账户-证件照2
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_ID_URL, corporate.getIdPhotoUrl());
        //对公账户-审核状态
        SharePreferenceUtil.put(topActivity, Constant.CORPORATE_ACCOUNT_STATUS, corporate.getAccountStatus());
    }
}
