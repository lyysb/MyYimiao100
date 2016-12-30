package com.yimiao100.sale.utils;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.bean.CorporateBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.UserBean;
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
     * 检查对公账户状态
     */
    public static void checkCorporate(final Activity activity, final CorporatePassedListener corporatePassedListener) {
        String mAccessToken = (String) SharePreferenceUtil.get(activity, Constant.ACCESSTOKEN, "");
        OkHttpUtils.post().url(CHECK_USER_ACCOUNT).addHeader(ACCESS_TOKEN, mAccessToken).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("检查对公账户状态E：" + e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("检查对公账户状态：" + response);
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

    public interface CorporatePassedListener {
        void handleCorporate(CorporateBean corporate);
    }

}
