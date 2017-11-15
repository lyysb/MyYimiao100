package com.yimiao100.sale.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.ReconciliationAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ReconciliationBean;
import com.yimiao100.sale.bean.ReconciliationList;
import com.yimiao100.sale.bean.ReconciliationResult;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.RegionSearchView;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

/**
 * 对账列表
 */
public class ReconciliationActivity extends BaseActivitySingleList implements RegionSearchView
        .onSearchClickListener {

    private final String URL_ORDER_LIST = Constant.BASE_URL + "/api/order/balance_order_list";
    private final String VENDOR_ID = "vendorId";
    private final String USER_ACCOUNT_TYPE = "userAccountType";

    private int mVendorId;
    private String mUserAccountType;

    private ArrayList<ReconciliationList> mReconciliationList;
    private ReconciliationAdapter mReconciliationAdapter;
    private HashMap<String, String> regionParams = new HashMap<>();

    public static void start(Context context, int vendorId, String userAccountType) {
        Intent intent = new Intent(context, ReconciliationActivity.class);
        intent.putExtra("vendorId", vendorId);
        intent.putExtra("userAccountType", userAccountType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mVendorId = getIntent().getIntExtra("vendorId", -1);
        mUserAccountType = getIntent().getStringExtra(USER_ACCOUNT_TYPE);
        showLoadingProgress();
        super.onCreate(savedInstanceState);
        LogUtil.d("userAccountType is " + mUserAccountType);
        setEmptyView(getString(R.string.empty_view_reconciliation), R.mipmap.ico_reconciliation);
    }

    @Override
    protected void initView() {
        super.initView();
        RegionSearchView searchView = new RegionSearchView(this);
        searchView.setOnSearchClickListener(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, DensityUtil.dp2px(this, 46), 0, 0);
        mEmptyView.setLayoutParams(layoutParams);
        mListView.addHeaderView(searchView, null, false);
        mListView.setDividerHeight(DensityUtil.dp2px(this, 0));
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("对账");
    }


    @Override
    public void regionSearch(@NotNull HashMap<String, String> regionIDs) {
        regionParams = regionIDs;
        onRefresh();
    }


    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("对账列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                hideLoadingProgress();
                LogUtil.d("对账列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        page = 2;
                        ReconciliationResult pagedResult = JSON.parseObject(response,
                                ReconciliationBean.class).getPagedResult();
                        totalPage = pagedResult.getTotalPage();
                        //解析JSON
                        mReconciliationList = pagedResult.getPagedList();
                        handleEmptyData(mReconciliationList);
                        mReconciliationAdapter = new ReconciliationAdapter(mReconciliationList);
                        //填充Adapter
                        mListView.setAdapter(mReconciliationAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }


    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //携带数据，打开对账详情列表
        ReconciliationList item = mReconciliationList.get(position - 1);
        // 修改为已阅读状态
        item.setTipStatus(0);
        mReconciliationAdapter.notifyDataSetChanged();
        String orderId = item.getId() + "";
        String categoryName = item.getCategoryName();
        String productName = item.getProductName();
        String vendorName = item.getVendorName();
        String customerName = item.getCustomerName();
        String dosageForm = item.getDosageForm();
        String spec = item.getSpec();
        String serialNo = item.getSerialNo();
        String userAccountType = item.getUserAccountType();
        int vendorId = item.getVendorId();
        String vendorLogoImageUrl = item.getVendorLogoImageUrl();
        // 进入对账详情
        ReconciliationDetailActivity.start(this, orderId, categoryName, productName, vendorName,
                customerName, dosageForm, spec, serialNo, userAccountType, vendorId, vendorLogoImageUrl);
    }

    @Override
    protected void onLoadMore() {
        getBuild(page).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("对账列表E：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                mListView.onLoadMoreComplete();
                LogUtil.d("对账列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        page ++;
                        ReconciliationResult pagedResult = JSON.parseObject(response,
                                ReconciliationBean.class).getPagedResult();

                        mReconciliationList.addAll(pagedResult.getPagedList());
                        mReconciliationAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_ORDER_LIST).addHeader(ACCESS_TOKEN, accessToken)
                .params(regionParams)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, pageSize)
                .addParams(VENDOR_ID, mVendorId + "")
                .addParams(USER_ACCOUNT_TYPE, mUserAccountType).build();
    }
}
