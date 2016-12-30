package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.UserBean;
import com.yimiao100.sale.bean.UserFundBean;
import com.yimiao100.sale.bean.UserFundNew;
import com.yimiao100.sale.fragment.CRMFragment;
import com.yimiao100.sale.fragment.FragmentTabHost;
import com.yimiao100.sale.fragment.InformationFragment;
import com.yimiao100.sale.fragment.MineFragment;
import com.yimiao100.sale.fragment.StudyFragment;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {


    @BindView(android.R.id.tabhost)
    FragmentTabHost mTabHost;
    private long exitTime;

    //用户账户URL
    private final String URL_USER_ACCOUNT = Constant.BASE_URL + "/api/user/get_user_account";
    //用户资金URL
    private final String URL_USER_FUND = Constant.BASE_URL + "/api/fund/user_fund";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initTabs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //获取用户账户信息
        initAccountInformation();
        //获取用户资金信息
        initFundInformation();
    }

    /**
     * 获取用户账户信息
     */
    private void initAccountInformation() {
        //请求网络，获取推广账户信息
        OkHttpUtils.post().url(URL_USER_ACCOUNT).addHeader(ACCESS_TOKEN, mAccessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("用户账户信息E：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("用户账户信息：" + response);
                UserBean userBean = JSON.parseObject(response, UserBean.class);
                switch (userBean.getStatus()) {
                    case "success":
                        if (userBean.getUserAccount().getCorporate() != null) {
                            //记录对公账户存在
                            SharePreferenceUtil.put(getApplicationContext(),
                                    Constant.CORPORATE_EXIT, true);
                            //对公账户-开户银行
                            SharePreferenceUtil.put(getApplicationContext(),
                                    Constant.CORPORATE_BANK_NAME,
                                    userBean.getUserAccount().getCorporate().getBankName());
                            //对公账户-公司账号
                            SharePreferenceUtil.put(getApplicationContext(),
                                    Constant.CORPORATE_ACCOUNT_NUMBER,
                                    userBean.getUserAccount().getCorporate().getCorporateAccount
                                            ());
                            //对公账户-公司电话号码
                            SharePreferenceUtil.put(getApplicationContext(),
                                    Constant.CORPORATE_PHONE_NUMBER,
                                    userBean.getUserAccount().getCorporate().getCorporatePhoneNumber());
                            //对公账户-开户名称
                            SharePreferenceUtil.put(getApplicationContext(),
                                    Constant.CORPORATE_ACCOUNT_NAME,
                                    userBean.getUserAccount().getCorporate().getAccountName());

                            //对公账户-企业营业执照地址
                            SharePreferenceUtil.put(getApplicationContext(),
                                    Constant.CORPORATE_BIZ_LICENCE_URL,
                                    userBean.getUserAccount().getCorporate().getBizLicenceUrl());
                            //对公账户-证件照1
                            SharePreferenceUtil.put(getApplicationContext(), Constant.CORPORATE_PERSONAL_URL,
                                    userBean.getUserAccount().getCorporate().getPersonalPhotoUrl());
                            //对公账户-证件照2
                            SharePreferenceUtil.put(getApplicationContext(), Constant.CORPORATE_ID_URL,
                                    userBean.getUserAccount().getCorporate().getIdPhotoUrl());

                            //对公账户-姓名
                            SharePreferenceUtil.put(getApplicationContext(),
                                    Constant.CORPORATE_CN_NAME,
                                    userBean.getUserAccount().getCorporate().getCnName());
                            //对公账户-电话
                            SharePreferenceUtil.put(getApplicationContext(),
                                    Constant.CORPORATION_PERSONAL_PHONE_NUMBER,
                                    userBean.getUserAccount().getCorporate()
                                            .getPersonalPhoneNumber());
                            //对公账户-邮箱
                            SharePreferenceUtil.put(getApplicationContext(), Constant.CORPORATE_EMAIL,
                                    userBean.getUserAccount().getCorporate().getEmail());
                            //对公账户-身份证号
                            SharePreferenceUtil.put(getApplicationContext(),
                                    Constant.CORPORATE_ID_NUMBER,
                                    userBean.getUserAccount().getCorporate().getIdNumber());
                        }
                        break;
                    case "failure":
                        Util.showError(MainActivity.this, userBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 获取用户资金信息
     */
    private void initFundInformation() {
        OkHttpUtils.post().url(URL_USER_FUND).addHeader(ACCESS_TOKEN, mAccessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("获取用户资金信息E：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("获取用户资金信息：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        UserFundNew userFund = JSON.parseObject(response, UserFundBean.class)
                                .getUserFund();
                        //用户账户总金额-double
                        SharePreferenceUtil.put(getApplicationContext(), Constant.TOTAL_AMOUNT,
                                userFund.getTotalAmount());
                        //用户积分-int
                        SharePreferenceUtil.put(getApplicationContext(), Constant.INTEGRAL,
                                 userFund.getIntegral());
                        //用户奖学金-double
                        SharePreferenceUtil.put(getApplicationContext(), Constant.TOTAL_EXAM_REWARD,
                                userFund.getTotalExamReward());
                        //用户推广费-double
                        SharePreferenceUtil.put(getApplicationContext(), Constant.TOTAL_SALE,
                                userFund.getTotalSale());
                        //用户保证金-double
                        SharePreferenceUtil.put(getApplicationContext(), Constant.DEPOSIT,
                                userFund.getDeposit());
                        break;
                    case "failure":
                        Util.showError(MainActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 设置底部导航按钮
     */
    private void initTabs() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.fl_fragment_container);
        //去除分隔线
        mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);

        addTabToTabHost("资讯", R.drawable.selector_home_information, InformationFragment.class,
                null);
        addTabToTabHost("商务", R.drawable.selector_home_crm, CRMFragment.class, null);
        addTabToTabHost("学习", R.drawable.selector_home_study, StudyFragment.class, null);
        addTabToTabHost("我的", R.drawable.selector_home_mine, MineFragment.class, null);


    }

    /**
     * 具体实现导航按钮的添加
     *
     * @param tabName
     * @param tabIcon
     * @param fragmentClass
     * @param args
     */
    private void addTabToTabHost(String tabName, int tabIcon, Class<?> fragmentClass, Bundle args) {
        //创建一个TextView
        TextView tabView = new TextView(this);
        tabView.setTextSize(12);
        tabView.setText(tabName);
        tabView.setGravity(Gravity.CENTER);
        tabView.setPadding(0, DensityUtil.dp2px(this, 5), 0, DensityUtil.dp2px(this, 2));
        tabView.setTextColor(getResources().getColorStateList(R.color
                .viewpage_selector_slide_title));
        tabView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcon, 0, 0);

        //创建一个Tab
        TabHost.TabSpec tab = mTabHost.newTabSpec(fragmentClass.getCanonicalName());
        tab.setIndicator(tabView);          // 指定tab显示的内容就是一个TextView

        //把tab与tab对应的Fragment添加到FragmentTabHost中
        mTabHost.addTab(tab, fragmentClass, args);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //System.currentTimeMillis()无论何时调用，肯定大于2000
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.showShort(getApplicationContext(), "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
