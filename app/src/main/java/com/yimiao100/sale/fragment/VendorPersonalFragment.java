package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.AssuranceActivity;
import com.yimiao100.sale.activity.PromotionActivity;
import com.yimiao100.sale.activity.ReconciliationActivity;
import com.yimiao100.sale.activity.ScholarshipActivity;
import com.yimiao100.sale.activity.ScoreDetailActivity;
import com.yimiao100.sale.adapter.listview.VendorListAdapter;
import com.yimiao100.sale.base.BaseFragmentSingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Event;
import com.yimiao100.sale.bean.EventType;
import com.yimiao100.sale.bean.Vendor;
import com.yimiao100.sale.bean.VendorListBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jetbrains.annotations.NotNull;

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
    private final String RECONCILIATION = "reconciliation";                 // 从对账过来的
    private final String USER_ACCOUNT_TYPE = "userAccountType";

    private String mModuleType;
    private String mFrom;
    private String mUserAccountType = "personal";

    private ArrayList<Vendor> mList;
    private TextView mHeaderView;
    @Override
    protected String initPageTitle() {
        return "个人推广";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mModuleType = getActivity().getIntent().getStringExtra("moduleType");
        mFrom = getActivity().getIntent().getStringExtra("from");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (mModuleType) {
            case BALANCE_ORDER:
                //来自对账
                setEmptyView(getString(R.string.empty_view_vendor_reconciliation), R.mipmap.ico_reconciliation);
                break;
            case EXAM_REWARD:
                //来自我的成就-我的奖学金
                setEmptyView(getString(R.string.empty_view_vendor_study_exam), R.mipmap.ico_study_extension);
                break;
            case SALE_WITHDRAWAL:
                if (TextUtils.equals(mFrom, RECONCILIATION)) {
                    initOverdue();
                }
                //来自推广费
                setEmptyView(getString(R.string.empty_view_vendor_sale), R.mipmap.ico_extension_reward);
                break;
            case DEPOSIT_WITHDRAWAL:
                //来自保证金
                setEmptyView(getString(R.string.empty_view_vendor_deposit), R.mipmap.ico_bond_factory_list);
                break;
            case EXAM_REWARD_WITHDRAWAL:
                //来自奖学金
                setEmptyView(getString(R.string.empty_view_vendor_study_withdrawal), R.mipmap.ico_scholarship_list);
                break;
        }
        mListView.cancleLoadMore();
    }

    private void initOverdue() {
        // step1:  listView 添加头视图
        mHeaderView = new TextView(getContext());
        mHeaderView.setTextColor(getResources().getColor(R.color.colorMain));
        mHeaderView.setTextSize(12f);
        mHeaderView.setPadding(
                DensityUtil.dp2px(getContext(), 12),
                DensityUtil.dp2px(getContext(), 12),
                DensityUtil.dp2px(getContext(), 12),
                DensityUtil.dp2px(getContext(), 12)
        );
        mHeaderView.setBackgroundColor(Color.parseColor("#ffffffff"));
        mListView.addHeaderView(mHeaderView, null, false);
        // step2:  emptyView 设置margin
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, DensityUtil.dp2px(getContext(), 40f), 0, 0);
        mEmptyView.setLayoutParams(params);
        // step3:  获取数据后，根据厂家数量设置头视图内容
    }

    @Override
    public void onEventMainThread(@NotNull Event event) {
        super.onEventMainThread(event);
        if (Event.eventType == EventType.ORDER_BALANCE) {
            // 重新刷新提醒数据
            onRefresh();
        }
    }

    @Override
    protected void onRefresh() {
        OkHttpUtils.post().url(URL_VENDOR_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(MODULE_TYPE, mModuleType).addParams(USER_ACCOUNT_TYPE, mUserAccountType)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("vendor list error is :");
                e.printStackTrace();
                Util.showTimeOutNotice(getActivity());
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                LogUtil.d("vendor list ：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mList = JSON.parseObject(response, VendorListBean
                                .class).getVendorList();
                        if (TextUtils.equals(mFrom, RECONCILIATION)) {
                            // 如果从对账来的，则控制显示headerView的显示内容
                            if (mList == null || mList.size() == 0) {
                                mHeaderView.setText("您目前无可提现的推广费可以充值逾期垫款");
                            } else {
                                mHeaderView.setText("请选择充值逾期垫款的可提现推广费");
                            }
                        }
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
        if (TextUtils.equals(mFrom, RECONCILIATION)) {
            position = position - 1;
        }
        Vendor vendor = mList.get(position);
        int vendorId = vendor.getId();
        String logoImageUrl = vendor.getLogoImageUrl();
        String vendorName = vendor.getVendorName();
        switch (mModuleType) {
            case EXAM_REWARD_WITHDRAWAL:
                //进入对应的奖学金提现界面
                ScholarshipActivity.start(
                        getContext(), vendorId, mUserAccountType, logoImageUrl, vendorName
                );
                break;
            case EXAM_REWARD:
                // 进入对应的学习成绩详情界面
                ScoreDetailActivity.start(
                        getContext(), vendorId, mUserAccountType, logoImageUrl, vendorName
                );
                break;
            case SALE_WITHDRAWAL:
//                if (TextUtils.equals(mFrom, RECONCILIATION)) {
//                    intent.putExtra("from", mFrom);
//                }
                //进入对应的推广费界面
                PromotionActivity.start(
                        getContext(), "", vendorId, mUserAccountType, logoImageUrl, vendorName
                );
                break;
            case DEPOSIT_WITHDRAWAL:
                //进入对应的保证金界面
                AssuranceActivity.start(
                        getContext(), vendorId, mUserAccountType, logoImageUrl, vendorName
                );
                break;
            case BALANCE_ORDER:
                //进入对应的对账
                ReconciliationActivity.start(getContext(), vendorId, mUserAccountType);
                break;
        }
    }

    @Override
    protected void onLoadMore() {

    }
}
