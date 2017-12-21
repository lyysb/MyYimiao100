package com.yimiao100.sale.utils;

import android.app.Activity;
import android.app.ProgressDialog;

import com.blankj.utilcode.util.SPUtils;
import com.yimiao100.sale.bean.CorporateBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.PersonalBean;
import com.yimiao100.sale.bean.UserBean;
import com.yimiao100.sale.ext.JSON;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * 检查账户信息的工具类
 * Created by 亿苗通 on 2016/11/17.
 */

public class CheckUtil {
    /**
     * 获取用户账户信息URL
     */
    private static final String CHECK_USER_ACCOUNT = Constant.BASE_URL + "/api/user/get_user_account";

    private static final String ACCESS_TOKEN = "X-Authorization-Token";



    private CheckUtil(){
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 检查个人账户状态
     */
    public static void checkPersonal(final Activity activity, final PersonalPassedListener personalPassedListener) {
        String mAccessToken = SPUtils.getInstance().getString(Constant.ACCESSTOKEN);
        final ProgressDialog loadingProgress = ProgressDialogUtil.getLoadingProgress(activity);
        loadingProgress.show();
        OkHttpUtils.post().url(CHECK_USER_ACCOUNT).addHeader(ACCESS_TOKEN, mAccessToken).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("检查个人账户状态E：" + e.toString());
                e.printStackTrace();
                if (loadingProgress.isShowing()) {
                    loadingProgress.dismiss();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("检查个人账户状态：" + response);
                if (loadingProgress.isShowing()) {
                    loadingProgress.dismiss();
                }
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        PersonalBean personal = JSON.parseObject(response, UserBean.class)
                                .getUserAccount().getPersonal();
                        if (personal == null) {
                            //没有个人账户，提示设置个人账户
                            DialogUtil.nonePersonal(activity);
                        } else {
                            //存在个人账户，判断账户状态
                            switch (personal.getAccountStatus()) {
                                case "auditing":        //待审核
                                    DialogUtil.personalAuditing(activity);
                                    break;
                                case "not_passed":      //审核未通过
                                    DialogUtil.personalNotPassed(activity);
                                    break;
                                case "passed":          //通过审核
                                    //回传数据
                                    if (personalPassedListener != null) {
                                        personalPassedListener.handlePersonal(personal);
                                    }
                                    break;
                            }
                        }
                        break;
                    case "failure":
                        Util.showError(activity, errorBean.getReason());
                        break;
                }
            }
        });
    }
    /**
     * 检查对公账户状态
     */
    public static void checkCorporate(final Activity activity, final CorporatePassedListener corporatePassedListener) {
        String mAccessToken = SPUtils.getInstance().getString(Constant.ACCESSTOKEN);
        final ProgressDialog loadingProgress = ProgressDialogUtil.getLoadingProgress(activity);
        loadingProgress.show();
        OkHttpUtils.post().url(CHECK_USER_ACCOUNT).addHeader(ACCESS_TOKEN, mAccessToken).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("检查对公账户状态E：" + e.toString());
                e.printStackTrace();
                if (loadingProgress.isShowing()) {
                    loadingProgress.dismiss();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("检查对公账户状态：" + response);
                if (loadingProgress.isShowing()) {
                    loadingProgress.dismiss();
                }
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        CorporateBean corporate = JSON.parseObject(response, UserBean.class)
                                .getUserAccount().getCorporate();
                        if (corporate == null) {
                            //没有对公账户，提示设置对公账户
                            DialogUtil.noneCorporate(activity);
                        } else {
                            //存在对公账户，判断账户状态
                            switch (corporate.getAccountStatus()) {
                                case "auditing":        //待审核
                                    DialogUtil.corporateAuditing(activity);
                                    break;
                                case "not_passed":      //审核未通过
                                    DialogUtil.corporateNotPassed(activity);
                                    break;
                                case "passed":          //通过审核
                                    //回传数据
                                    if (corporatePassedListener != null) {
                                        corporatePassedListener.handleCorporate(corporate);
                                    }
                                    break;
                            }
                        }
                        break;
                    case "failure":
                        Util.showError(activity, errorBean.getReason());
                        break;
                }
            }
        });
    }

    public interface PersonalPassedListener {
        void handlePersonal(PersonalBean personal);
    }
    public interface CorporatePassedListener {
        void handleCorporate(CorporateBean corporate);
    }

}
