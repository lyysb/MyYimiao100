package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

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
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 对账列表
 */
public class ReconciliationActivity extends BaseActivitySingleList {

    private final String URL_ORDER_LIST = Constant.BASE_URL + "/api/order/balance_order_list";
    private final String VENDOR_ID = "vendorId";
    private final String USER_ACCOUNT_TYPE = "userAccountType";

    private int mVendorId;
    private String mUserAccountType;

    private ArrayList<ReconciliationList> mReconciliationList;
    private ReconciliationAdapter mReconciliationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mVendorId = getIntent().getIntExtra("vendorId", -1);
        mUserAccountType = getIntent().getStringExtra(USER_ACCOUNT_TYPE);
        showLoadingProgress();
        super.onCreate(savedInstanceState);
        LogUtil.Companion.d("userAccountType is " + mUserAccountType);
        setEmptyView(getString(R.string.empty_view_reconciliation), R.mipmap.ico_reconciliation);
    }

    @Override
    protected void initView() {
        super.initView();
        mListView.setDividerHeight(DensityUtil.dp2px(this, 0));
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("对账");
    }


    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("对账列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                hideLoadingProgress();
                LogUtil.Companion.d("对账列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage = 2;
                        ReconciliationResult pagedResult = JSON.parseObject(response,
                                ReconciliationBean.class).getPagedResult();
                        mTotalPage = pagedResult.getTotalPage();
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
        ReconciliationList reconciliation = mReconciliationList.get(position);
        Intent intent = new Intent(this, ReconciliationDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("orderId", reconciliation.getId() + "");
        //产品名-分类名
        bundle.putString("categoryName", reconciliation.getCategoryName());
        //商品名
        bundle.putString("productName", reconciliation.getProductName());
        //厂家名称
        bundle.putString("vendorName", reconciliation.getVendorName());
        //客户名称
        bundle.putString("customerName", reconciliation.getCustomerName());
        //剂型
        bundle.putString("dosageForm", reconciliation.getDosageForm());
        //规格
        bundle.putString("spec", reconciliation.getSpec());
        //协议单号
        bundle.putString("serialNo", reconciliation.getSerialNo());
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    protected void onLoadMore() {
        getBuild(mPage).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("对账列表E：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                mListView.onLoadMoreComplete();
                LogUtil.Companion.d("对账列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage++;
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
        return OkHttpUtils.post().url(URL_ORDER_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, "10")
                .addParams(VENDOR_ID, mVendorId + "")
                .addParams(USER_ACCOUNT_TYPE, mUserAccountType).build();
    }
}
