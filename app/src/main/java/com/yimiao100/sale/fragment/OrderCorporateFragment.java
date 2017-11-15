package com.yimiao100.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.*;
import com.yimiao100.sale.adapter.listview.OrderAdapter;
import com.yimiao100.sale.base.BaseFragmentSingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ResourceBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.bean.ResourceResultBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 公司推广--已弃用
 * Created by Michel on 2017/3/1.
 */

public class OrderCorporateFragment extends BaseFragmentSingleList {

    private final String USER_ORDER_LIST = Constant.BASE_URL + "/api/order/user_order_list";
    private final String USER_ACCOUNT_TYPE = "userAccountType";
    private ArrayList<ResourceListBean> mList;
    private OrderAdapter mAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyView(getString(R.string.empty_view_order), R.mipmap.ico_my_business);
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    protected String initPageTitle() {
        return "公司推广";
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                Util.showTimeOutNotice(getActivity());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("corporate oder ：" + response);
                mSwipeRefreshLayout.setRefreshing(false);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage = 2;
                        ResourceResultBean resourceResult = JSON.parseObject(response, ResourceBean.class).getResourceResult();
                        mTotalPage = resourceResult.getTotalPage();
                        mList = resourceResult.getResourcesList();
                        handleEmptyData(mList);
                        mAdapter = new OrderAdapter(getContext(), mList);
                        mListView.setAdapter(mAdapter);
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }
            }
        });
    }

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(USER_ORDER_LIST)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize)
                .addParams(USER_ACCOUNT_TYPE, "corporate")
                .build();
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获得条目数据，携带到详细界面
        ResourceListBean order = mList.get(position);
        Class clz = null;
        //获取订单状态Key
        String orderStatus = order.getOrderStatus();
        //根据订单状态名，选择打开不同的Activity
        switch (orderStatus) {
            case "unpaid":
                //最初状态-待支付
                clz = OrderUnpaidActivity.class;
                break;
            case "bidding":
                //第一状态-竞标中
                clz = OrderSubmitActivity.class;
                break;
            case "auditing":
                //第二状态-审核中-作废
                return;
//                clz = OrderLaterActivity.class;
//                break;
            case "to_be_signed":
                //第三状态-待签约
                clz = OrderAlreadyActivity.class;
                break;
            case "already_signed":
                //第四状态-已签约
                clz = OrderCompletedActivity.class;
                break;
            case "end":
                // 最终状态-已终止
                clz = OrderEndActivity.class;
                break;
            case "not_passed":
            case "defaulted":
                //进入错误界面--已违约|未通过
                clz = OrderErrorActivity.class;
                break;
        }
        Intent intent = new Intent(getActivity(), clz);
        intent.putExtra("order", order);
        startActivity(intent);
    }

    @Override
    protected void onLoadMore() {
        getBuild(mPage).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("corporate oder error：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("corporate oder ：" + response);
                mListView.onLoadMoreComplete();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage++;
                        ResourceResultBean resourceResult = JSON.parseObject(response, ResourceBean.class).getResourceResult();
                        mList.addAll(resourceResult.getResourcesList());
                        mAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(getActivity(), errorBean.getReason());
                        break;
                }
            }
        });
    }
}
