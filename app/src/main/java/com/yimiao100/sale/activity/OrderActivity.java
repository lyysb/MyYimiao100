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
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
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


    private final String USER_ORDER_LIST = "/api/order/user_order_list";
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
        return OkHttpUtils.post().url(Constant.BASE_URL + USER_ORDER_LIST)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize)
                .build();
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("业务列表E：" + e.getMessage());
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
                LogUtil.d("业务列表：" + response);
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
                LogUtil.d("业务列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("业务列表：" + response);
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
        String orderStatusName = order.getOrderStatusName();
        //获取订单状态Key
        String orderStatus = order.getOrderStatus();
        Bundle bundle = new Bundle();
        //根据订单状态名，选择打开不同的Activity
        switch (orderStatus) {
            case "bidding":
                //第一状态-竞标中
                enterOrderSubmit(order, bundle);
                break;
            case "auditing":
                //第二状态-审核中
                enterOrderLater(order, bundle);
                break;
            case "to_be_signed":
                //第三状态-待签约
                enterIntoOrderAlready(order, bundle);
                break;
            case "already_signed":
                //第四状态-已签约
                enterOrderComplete(order, bundle);
                break;
            default:
                //进入错误界面--已违约|未通过
                Intent errorIntent = new Intent(this, OrderErrorActivity.class);
                //订单状态名称
                bundle.putString("orderStatusName", orderStatusName);
                //无效原因
                bundle.putString("invalidReason", order.getInvalidReason());
                errorIntent.putExtras(bundle);
                startActivity(errorIntent);
                break;
        }
    }

    /**
     * 进入第一状态界面-竞标中
     * @param order
     * @param bundle
     */
    private void enterOrderSubmit(ResourceListBean order, Bundle bundle) {
        Intent laterIntent = new Intent(this, OrderSubmitActivity.class);
        //提交时间
        bundle.putString("submit_time", TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy年MM月dd日"));
        //厂家名
        bundle.putString("vendorName", order.getVendorName());
        //产品名-分类名
        bundle.putString("categoryName", order.getCategoryName());
        //规格
        bundle.putString("spec", order.getSpec());
        //剂型
        bundle.putString("dosageForm", order.getDosageForm());
        //区域
        bundle.putString("region", order.getProvinceName() + "\t" + order.getCityName() + "\t" + order.getAreaName());
        //时间
        bundle.putString("time", TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy.MM.dd"));
        //保证金
        bundle.putString("totalDeposit", order.getSaleDeposit() + "");
        //协议单号：
        bundle.putString("serialNo", order.getSerialNo());
        //竞标保证金
        bundle.putString("bidDeposit", order.getBidDeposit() + "");
        //封装数据
        laterIntent.putExtras(bundle);
        startActivity(laterIntent);
    }

    /**
     * 进入第二状态界面-审核中
     * @param order
     * @param bundle
     */
    private void enterOrderLater(ResourceListBean order, Bundle bundle) {
        Intent laterIntent = new Intent(this, OrderLaterActivity.class);
        //提交时间
        bundle.putString("submit_time", TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy年MM月dd日"));
        //厂家名
        bundle.putString("vendorName", order.getVendorName());
        //产品名-分类名
        bundle.putString("categoryName", order.getCategoryName());
        //规格
        bundle.putString("spec", order.getSpec());
        //剂型
        bundle.putString("dosageForm", order.getDosageForm());
        //区域
        bundle.putString("region", order.getProvinceName() + "\t" + order.getCityName() + "\t" + order.getAreaName());
        //时间
        bundle.putString("time", TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy.MM.dd"));
        //保证金
        bundle.putString("totalDeposit", order.getSaleDeposit() + "");
        //协议单号：
        bundle.putString("serialNo", order.getSerialNo());
        //竞标保证金
        bundle.putString("bidDeposit", order.getBidDeposit() + "");
        //封装数据
        laterIntent.putExtras(bundle);
        startActivity(laterIntent);
    }
    /**
     * 第三状态-待签约
     * @param order
     * @param bundle
     */
    private void enterIntoOrderAlready(ResourceListBean order, Bundle bundle) {
        Intent alreadyIntent = new Intent(this, OrderAlreadyActivity.class);
        //订单id
        bundle.putString("orderId", order.getId() + "");
        //提交时间
        bundle.putString("submit_time", TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy年MM月dd日"));
        //厂家名
        bundle.putString("vendorName", order.getVendorName());
        //产品名-分类名
        bundle.putString("categoryName", order.getCategoryName());
        //规格
        bundle.putString("spec", order.getSpec());
        //剂型
        bundle.putString("dosageForm", order.getDosageForm());
        //区域
        bundle.putString("region", order.getProvinceName() + "\t" + order.getCityName() + "\t" + order.getAreaName());
        //时间
        bundle.putString("time", TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy.MM.dd"));
        //保证金
        bundle.putString("totalDeposit", order.getSaleDeposit() + "");
        //协议单号：
        bundle.putString("serialNo", order.getSerialNo());
        //总额保证金
        bundle.putString("orderTotalDeposit", FormatUtils.MoneyFormat(order.getSaleDeposit()));
        //违约有效时间
        bundle.putString("defaultExpiredAt", TimeUtil.timeStamp2Date(order.getDefaultExpiredAt() + "", "yyyy年MM月dd日"));
        //协议文件url
        bundle.putString("resourceProtocolUrl", order.getResourceProtocolUrl());
        LogUtil.d("列表resourceProtocolUrl：" + order.getResourceProtocolUrl());
        //是否已经阅读过免责信息
        bundle.putBoolean("isRead", order.isRead());
        //封装数据
        alreadyIntent.putExtras(bundle);
        startActivity(alreadyIntent);
    }
    /**
     * 第四状态-已签约
     * @param order
     * @param bundle
     */
    private void enterOrderComplete(ResourceListBean order, Bundle bundle) {
        Intent completeIntent = new Intent(this, OrderCompletedActivity.class);
        //订单id
        bundle.putString("orderId", order.getId() + "");
        //提交时间
        bundle.putString("submit_time", TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy年MM月dd日"));
        //厂家名
        bundle.putString("vendorName", order.getVendorName());
        //客户名称
        bundle.putString("customerName", order.getCustomerName());
        //产品-分类名
        bundle.putString("categoryName", order.getCategoryName());
        //商品名-产品名
        bundle.putString("productName", order.getProductName());
        //规格
        bundle.putString("spec", order.getSpec());
        //剂型
        bundle.putString("dosageForm", order.getDosageForm());
        //区域
        bundle.putString("region", order.getProvinceName() + "\t" + order.getCityName() + "\t" + order.getAreaName());
        //时间
        bundle.putString("time", TimeUtil.timeStamp2Date(order.getCreatedAt() + "", "yyyy.MM.dd"));
        //保证金
        bundle.putString("totalDeposit", order.getSaleDeposit() + "");
        //协议单号
        bundle.putString("serialNo", order.getSerialNo());
        //已签约协议-其实就是那个图
        bundle.putString("orderProtocolUrl", order.getOrderProtocolUrl());
        //协议文件url
        bundle.putString("resourceProtocolUrl", order.getResourceProtocolUrl());
        LogUtil.d("列表resourceProtocolUrl：" + order.getResourceProtocolUrl());
        //封装数据
        completeIntent.putExtras(bundle);
        startActivity(completeIntent);
    }
}
