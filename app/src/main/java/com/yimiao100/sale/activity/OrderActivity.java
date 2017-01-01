package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.OrderAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ResourceBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.bean.ResourceResultBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 我的业务-业务列表
 */
public class OrderActivity extends BaseActivitySingleList{


    private final String USER_ORDER_LIST = Constant.BASE_URL + "/api/order/user_order_list";
    private ArrayList<ResourceListBean> mOrderList;

    private OrderAdapter mOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEmptyView("不是没有业务状态，只是没有申请推广而已", R.mipmap.ico_my_business);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onRefresh();
    }


    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("我的业务");
    }


    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(USER_ORDER_LIST)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize)
                .build();
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 停止刷新
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 300);
                if (response.length() > 4000) {
                    for (int i = 0; i < response.length(); i += 4000) {
                        if (i + 4000 < response.length()) {
                            LogUtil.Companion.d(i + "业务列表：" + response.substring(i, i + 4000));
                        } else {
                            LogUtil.Companion.d(i + "业务列表：" + response.substring(i, response.length()));
                        }
                    }
                } else {
                    LogUtil.Companion.d("业务列表：" + response);
                }
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage = 2;
                        ResourceResultBean resourceResult = JSON.parseObject(response, ResourceBean.class).getResourceResult();
                        mTotalPage = resourceResult.getTotalPage();
                        mOrderList = resourceResult.getResourcesList();
                        handleEmptyData(mOrderList);
                        mOrderAdapter = new OrderAdapter(getApplicationContext(), mOrderList);
                        mListView.setAdapter(mOrderAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }


    @Override
    public void onLoadMore() {
        getBuild(mPage).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("业务列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("业务列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage++;
                        ResourceResultBean resourceResult = JSON.parseObject(response, ResourceBean.class).getResourceResult();

                        mOrderList.addAll(resourceResult.getResourcesList());
                        mOrderAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
                mListView.onLoadMoreComplete();
            }
        });
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //获得条目数据，携带到详细界面
        ResourceListBean order = mOrderList.get(position);
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
            case "not_passed":
            case "defaulted":
                //进入错误界面--已违约|未通过
                clz = OrderErrorActivity.class;
                break;
        }
        Intent intent = new Intent(this, clz);
        intent.putExtra("order", order);
        startActivity(intent);
    }
}
