package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.expendable.CRMShipAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ShipBean;
import com.yimiao100.sale.bean.ShipList;
import com.yimiao100.sale.bean.ShipResult;
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
 * CRM-发货累计
 */
public class ShipActivity extends BaseActivity implements TitleView.TitleBarOnClickListener,
        CRMShipAdapter.ChartVisibilityListener, ExpandableListView.OnGroupExpandListener,
        SwipeRefreshLayout.OnRefreshListener, LoadMoreExpendableListView.OnLoadMoreListener {

    @BindView(R.id.ship_list_view)
    LoadMoreExpendableListView mShipListView;
    @BindView(R.id.ship_title)
    TitleView mShipTitle;
    @BindView(R.id.ship_refresh)
    SwipeRefreshLayout mShipRefresh;
    private CRMShipAdapter mShipAdapter;
    private ArrayList<ShipList> mShipList;

    private final String URL_DELIVERY_STAT = Constant.BASE_URL + "/api/stat/order_delivery_stat";

    private View mEmptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship);
        ButterKnife.bind(this);

        showLoadingProgress();

        initView();

        onRefresh();
    }

    private void initView() {
        mShipTitle.setOnTitleBarClick(this);
        initEmptyView();

        initRefreshLayout();

        initExpendableListView();
    }

    private void initEmptyView() {
        mEmptyView = findViewById(R.id.ship_empty_view);
        TextView emptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
        emptyText.setText("还在等什么，赶快到资源里面申请推广吧。");
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ico_deliver_goods), null, null);
    }

    private void initRefreshLayout() {
        //刷新控件
        mShipRefresh.setOnRefreshListener(this);
        //设置下拉圆圈的颜色
        mShipRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color
                .holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mShipRefresh.setDistanceToTriggerSync(400);
    }

    private void initExpendableListView() {
        //只能打开一个分组
        mShipListView.setOnGroupExpandListener(this);
        //加载更多
        mShipListView.setOnLoadMoreListener(this);
    }


    @Override
    public void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("发货累计E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                mShipRefresh.setRefreshing(false);
                hideLoadingProgress();
                LogUtil.Companion.d("发货累计：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ShipResult result = JSON.parseObject(response, ShipBean.class)
                                .getPagedResult();
                        mPage = 2;
                        mTotalPage = result.getTotalPage();

                        mShipList = result.getPagedList();
                        if (mShipList.size() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                        mShipAdapter = new CRMShipAdapter(mShipList);
                        //切换图表类型
                        mShipAdapter.setChartVisibilityListener(ShipActivity.this);
                        mShipListView.setAdapter(mShipAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }

            }
        });

    }

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_DELIVERY_STAT).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, "10").build();
    }

    /**
     * 切换图表类型显示
     *
     * @param groupPosition
     */
    @Override
    public void setChartVisibility(int groupPosition) {
        ShipList list = mShipAdapter.getGroup(groupPosition);
        if (list.isShowPerMonth()) {
            mShipAdapter.showPerMonth();
        } else {
            mShipAdapter.showTotal();
        }
        list.setShowPerMonth(!list.isShowPerMonth());
        mShipAdapter.notifyDataSetChanged();
    }

    /**
     * 只能打开一个条目
     *
     * @param groupPosition
     */
    @Override
    public void onGroupExpand(int groupPosition) {
        int count = mShipListView.getExpandableListAdapter().getGroupCount();
        for (int j = 0; j < count; j++) {
            if (j != groupPosition) {
                mShipListView.collapseGroup(j);
            }
        }
    }


    @Override
    public void onLoadMore() {
        if (mPage <= mTotalPage) {
            getBuild(mPage).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    LogUtil.Companion.d("发货累计E：" + e.getLocalizedMessage());
                    Util.showTimeOutNotice(currentContext);
                }

                @Override
                public void onResponse(String response, int id) {
                    LogUtil.Companion.d("发货累计：" + response);
                    ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                    switch (errorBean.getStatus()) {
                        case "success":
                            mPage++;
                            mShipList.addAll(JSON.parseObject(response, ShipBean.class)
                                    .getPagedResult().getPagedList());
                            mShipAdapter.notifyDataSetChanged();
                            break;
                        case "failure":
                            Util.showError(currentContext, errorBean.getReason());
                            break;
                    }
                    mShipListView.loadingMoreComplete();
                }
            });
        } else {
            mShipListView.noMore();
        }
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
