package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.AssuranceActivity;
import com.yimiao100.sale.activity.PromotionActivity;
import com.yimiao100.sale.activity.ReconciliationActivity;
import com.yimiao100.sale.activity.ScholarshipActivity;
import com.yimiao100.sale.activity.ScoreDetailActivity;
import com.yimiao100.sale.adapter.listview.VendorListAdapter;
import com.yimiao100.sale.base.BaseFragmentSingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Vendor;
import com.yimiao100.sale.bean.VendorListBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 厂家列表-个人推广
 * Created by Michel on 2017/3/3.
 */

public class VendorPersonalFragment extends BaseFragmentSingleList{

    private final String URL_VENDOR_LIST = Constant.BASE_URL + "/api/vendor/vendor_list_by_user";
    private final String MODULE_TYPE = "moduleType";
    private final String BALANCE_ORDER = "balance_order";                   //对账订单
    private final String EXAM_REWARD = "exam_reward";                       //课程考试奖励
    private final String SALE_WITHDRAWAL = "sale_withdrawal";               //销售资金可提现
    private final String DEPOSIT_WITHDRAWAL = "deposit_withdrawal";         //保证金可提现
    private final String EXAM_REWARD_WITHDRAWAL = "exam_reward_withdrawal"; //课程考试奖励可提现
    private final String USER_ACCOUNT_TYPE = "userAccountType";

    private String mModuleType;
    private String mUserAccountType = "personal";

    private ArrayList<Vendor> mList;
    @Override
    protected String initPageTitle() {
        return "个人推广";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mModuleType = getActivity().getIntent().getStringExtra("moduleType");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (mModuleType) {
            case BALANCE_ORDER:
                //来自对账
                setEmptyView("早起的鸟儿有虫吃，快到资源里面申请推广吧。", R.mipmap.ico_reconciliation);
                break;
            case EXAM_REWARD:
                //来自我的成就-我的奖学金
                setEmptyView("考试是可以“挣钱”的。但是，暂时没有学习任务。", R.mipmap.ico_study_extension);
                break;
            case SALE_WITHDRAWAL:
                //来自推广费
                setEmptyView("君子爱财、取之有道，快到资源里面申请推广吧。", R.mipmap.ico_extension_reward);
                break;
            case DEPOSIT_WITHDRAWAL:
                //来自保证金
                setEmptyView("有钱使得鬼推磨，快到资源里面申请推广吧。", R.mipmap.ico_bond_factory_list);
                break;
            case EXAM_REWARD_WITHDRAWAL:
                //来自奖学金
                setEmptyView("一寸光阴一寸金，快到资源里面申请推广吧。", R.mipmap.ico_scholarship_list);
                break;
        }
        mListView.cancleLoadMore();
    }

    @Override
    protected void onRefresh() {
        OkHttpUtils.post().url(URL_VENDOR_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(MODULE_TYPE, mModuleType).addParams(USER_ACCOUNT_TYPE, mUserAccountType)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("vendor list error is :");
                e.printStackTrace();
                Util.showTimeOutNotice(getActivity());
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                LogUtil.Companion.d("vendor list ：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mList = JSON.parseObject(response, VendorListBean
                                .class).getVendorList();
                        handleEmptyData(mList);
                        mListView.setAdapter(new VendorListAdapter(mList));
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Vendor vendor = mList.get(position);
        Intent intent = new Intent();
        Class clz = null;
        switch (mModuleType) {
            case EXAM_REWARD_WITHDRAWAL:
                //进入对应的奖学金提现界面
                clz = ScholarshipActivity.class;
                intent.putExtra("vendorId", vendor.getId());
                intent.putExtra("logoImageUrl", vendor.getLogoImageUrl());
                intent.putExtra("vendorName", vendor.getVendorName());
                break;
            case EXAM_REWARD:
                // 进入对应的学习成绩详情界面
                clz = ScoreDetailActivity.class;
                intent.putExtra("vendorId", vendor.getId());
                intent.putExtra("logoImageUrl", vendor.getLogoImageUrl());
                intent.putExtra("vendorName", vendor.getVendorName());
                break;
            case SALE_WITHDRAWAL:
                //进入对应的推广费界面
                clz = PromotionActivity.class;
                intent.putExtra("vendorId", vendor.getId());
                intent.putExtra("logoImageUrl", vendor.getLogoImageUrl());
                intent.putExtra("vendorName", vendor.getVendorName());
                break;
            case DEPOSIT_WITHDRAWAL:
                //进入对应的保证金界面
                clz = AssuranceActivity.class;
                intent.putExtra("vendorId", vendor.getId());
                intent.putExtra("logoImageUrl", vendor.getLogoImageUrl());
                intent.putExtra("vendorName", vendor.getVendorName());
                break;
            case BALANCE_ORDER:
                //进入对应的对账
                clz = ReconciliationActivity.class;
                intent.putExtra("vendorId", vendor.getId());
                break;
        }
        intent.setClass(getContext(), clz);
        intent.putExtra(USER_ACCOUNT_TYPE, mUserAccountType);
        startActivity(intent);
    }

    @Override
    protected void onLoadMore() {

    }
}
