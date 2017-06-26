package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.expendable.CRMPaymentAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.PaymentBean;
import com.yimiao100.sale.bean.PaymentList;
import com.yimiao100.sale.bean.PaymentResult;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.LoadMoreExpendableListView;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 回款累计
 */
public class PaymentActivity extends BaseActivity implements TitleView.TitleBarOnClickListener,
        SwipeRefreshLayout.OnRefreshListener, ExpandableListView.OnGroupExpandListener,
        CRMPaymentAdapter.ChartVisibilityListener, LoadMoreExpendableListView.OnLoadMoreListener {

    @BindView(R.id.payment_list_view)
    LoadMoreExpendableListView mPaymentListView;
    @BindView(R.id.payment_title)
    TitleView mPaymentTitle;
    @BindView(R.id.payment_refresh)
    SwipeRefreshLayout mPaymentRefresh;
    private CRMPaymentAdapter mPaymentAdapter;

    private final String URL_PAYMENT_STAT = Constant.BASE_URL + "/api/stat/order_payment_stat";

    private ArrayList<PaymentList> mPaymentList;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        initView();

        showLoadingProgress();

        onRefresh();
    }

    private void initView() {
        mPaymentTitle.setOnTitleBarClick(this);

        initEmptyView();

        initRefreshLayout();

        initExpendableListView();
    }

    private void initEmptyView() {
        mEmptyView = findViewById(R.id.payment_empty_view);
        TextView emptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
        emptyText.setText(getString(R.string.empty_view_payment));
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ico_deliver_goods), null, null);
    }

    private void initRefreshLayout() {
        //刷新控件
        mPaymentRefresh.setOnRefreshListener(this);
        //设置下拉圆圈的颜色
        mPaymentRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color
                .holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mPaymentRefresh.setDistanceToTriggerSync(400);
    }

    private void initExpendableListView() {
        //只能打开一个分组
        mPaymentListView.setOnGroupExpandListener(this);
        //加载更多监听
        mPaymentListView.setOnLoadMoreListener(this);
    }




    @Override
    public void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("回款累计E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                hideLoadingProgress();
                mPaymentRefresh.setRefreshing(false);
                LogUtil.d("回款累计：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        PaymentResult result = JSON.parseObject(response, PaymentBean.class)
                                .getPagedResult();
                        page =2;
                        totalPage = result.getTotalPage();

                        mPaymentList = result.getPagedList();
                        if (mPaymentList.size() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                        mPaymentAdapter = new CRMPaymentAdapter(mPaymentList);
                        mPaymentListView.setAdapter(mPaymentAdapter);
                        mPaymentAdapter.setChartVisibilityListener(PaymentActivity.this);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        int count = mPaymentListView.getExpandableListAdapter().getGroupCount();
        for (int j = 0; j < count; j++) {
            if (j != groupPosition) {
                mPaymentListView.collapseGroup(j);
            }
        }
    }

    @Override
    public void setChartVisibility(int groupPosition) {
        PaymentList list = mPaymentAdapter.getGroup(groupPosition);
        if (list.isShowTotal()) {
            mPaymentAdapter.showTotal();
        } else {
            mPaymentAdapter.showPerMonth();
        }
        list.setShowTotal(!list.isShowTotal());
        mPaymentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMore() {
        if (page <= totalPage) {
            getBuild(page).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    LogUtil.d("回款累计E：" + e.getLocalizedMessage());
                    Util.showTimeOutNotice(currentContext);
                }

                @Override
                public void onResponse(String response, int id) {
                    LogUtil.d("回款累计：" + response);
                    ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                    switch (errorBean.getStatus()) {
                        case "success":
                            page++;
                            mPaymentList.addAll(JSON.parseObject(response, PaymentBean.class)
                                    .getPagedResult().getPagedList());
                            mPaymentAdapter.notifyDataSetChanged();
                            break;
                        case "failure":
                            Util.showError(currentContext, errorBean.getReason());
                            break;
                    }
                    mPaymentListView.loadingMoreComplete();
                }
            });
        } else {
            mPaymentListView.noMore();
        }
    }
    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_PAYMENT_STAT).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, pageSize).build();
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
