package com.yimiao100.sale.ui.business.vaccine;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.blankj.utilcode.util.ToastUtils;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.OrderAlreadyActivity;
import com.yimiao100.sale.activity.OrderCompletedActivity;
import com.yimiao100.sale.activity.OrderEndActivity;
import com.yimiao100.sale.activity.OrderErrorActivity;
import com.yimiao100.sale.activity.OrderSubmitActivity;
import com.yimiao100.sale.activity.OrderUnpaidActivity;
import com.yimiao100.sale.adapter.listview.BusinessResAdapter;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.bean.ResourceResultBean;
import com.yimiao100.sale.mvpbase.BaseActivity;
import com.yimiao100.sale.mvpbase.CreatePresenter;
import com.yimiao100.sale.ui.pay.PayActivity;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.view.SelectAllView;
import com.yimiao100.sale.view.TitleView;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 我的业务列表
 */
@CreatePresenter(BusinessPresenter.class)
public class BusinessActivity extends BaseActivity<BusinessContract.View, BusinessContract.Presenter> implements BusinessContract.View {

    private String userAccountType;
    private String vendorId;
    private SelectAllView selectAllView;
    private ListView listView;
    private int totalPage;
    private ArrayList<ResourceListBean> list;
    private BusinessResAdapter adapter;
    private TitleView titleView;
    private View emptyView;
    private SparseArray<ResourceListBean> businesses = new SparseArray<>(); // 记录选中的未支付订单
    private int unpaidCount;
    private TwinklingRefreshLayout refreshLayout;
    private int page;

    public static void start(Context context, String userAccountType, String vendorId) {
        Intent intent = new Intent(context, BusinessActivity.class);
        intent.putExtra("userAccountType", userAccountType);
        intent.putExtra("vendorId", vendorId);
        context.startActivity(intent);
    }

    @Override
    protected void init() {
        super.init();

        initVariate();

        initView();

        initData();

        showProgress();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getPresenter().initList(userAccountType, vendorId);
    }

    private void initVariate() {
        userAccountType = getIntent().getStringExtra("userAccountType");
        vendorId = getIntent().getStringExtra("vendorId");
    }

    private void initView() {
        // TitleView
        titleView = (TitleView) findViewById(R.id.business_title);
        titleView.setOnTitleBarClick(new TitleView.TitleBarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {
                if (selectAllView.getVisibility() == View.GONE) {
                    edit();
                } else {
                    cancel();
                }
            }
        });
        // EmptyView
        emptyView = findViewById(R.id.business_empty);
        // TwinklingRefreshLayout
        refreshLayout = (TwinklingRefreshLayout) findViewById(R.id.business_refresh);
        refreshLayout.setFloatRefresh(true);
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                getPresenter().refreshList(userAccountType, vendorId);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                if (page <= totalPage) {
                    getPresenter().loadMoreList(userAccountType, vendorId, page);
                } else {
                    stopLoadMore();
                    ToastUtils.showShort("全部加载完成");
                }
            }
        });
        // ListView
        listView = (ListView) findViewById(R.id.business_list);
        listView.setOnItemClickListener(this::navigateToDetail);
        // 底部全选条
        selectAllView = (SelectAllView) findViewById(R.id.business_bottom);
        // 全选
        selectAllView.setOnSelectAllClickListener(this::selectAllUnpaidOrder);
        // 支付订单
        selectAllView.setOnConfirmClickListener(this::navigateToPay);
    }

    private void initData() {
        getPresenter().initList(userAccountType, vendorId);
    }

    @Override
    public void initSuccess(ResourceResultBean result) {
        // 初始化标题
        titleView.setRight("编辑");
        selectAllView.setVisibility(View.GONE);
        selectAllView.deselectAll();
        selectAllView.updateSelectCount("0个");
        businesses.clear();
        // 初始化未支付状态订单数量统计
        unpaidCount = 0;
        // 下一页页码
        page = 2;
        // 获取总页数
        totalPage = result.getTotalPage();
        // 获取数据列表
        list = result.getResourcesList();
        adapter = new BusinessResAdapter(list, false);
        listView.setAdapter(adapter);
        adapter.setOnCheckBoxClickListener(this::selectUnpaidOrder);
        // 处理空数据
        handleEmptyData(list);
        // 统计未支付订单数量
        countUnpaidOrder(list);
    }

    @Override
    public void stopRefreshing() {
        refreshLayout.finishRefreshing();
    }

    @Override
    public void stopLoadMore() {
        refreshLayout.finishLoadmore();
    }

    @Override
    public void loadMoreSuccess(ResourceResultBean result) {
        page++;
        list.addAll(result.getResourcesList());
        // 更新未支付状态订单数量统计
        countUnpaidOrder(result.getResourcesList());
        // 更新Adapter
        adapter.notifyDataSetChanged();
        // 数据增加，取消全选
        selectAllView.deselectAll();
    }

    /**
     * 统计当前列表中未支付状态下订单的数量
     */
    private void countUnpaidOrder(ArrayList<ResourceListBean> list) {
        for (ResourceListBean item : list) {
            if (TextUtils.equals(item.getOrderStatus(), "unpaid")) {
                unpaidCount++;
            }
        }
    }

    /**
     * 选择未支付订单
     */
    private void selectUnpaidOrder(CheckBox checkBox, int position) {
        // 获取当前选中订单
        ResourceListBean currentItem = list.get(position);
        // 记录选中的订单
        if (businesses.size() == 0) {
            businesses.put(currentItem.getId(), currentItem);
            currentItem.setChecked(true);
        } else {
            if (!currentItem.isChecked()) {
                // 当前未选中，获取第一条数据
//                ResourceListBean firstItem = businesses.valueAt(0);
//                if (!TextUtils.equals(currentItem.getCategoryName(), firstItem.getCategoryName())) {
//                    // 产品不同
//                    ToastUtils.showShort("请选择同一产品");
//                    checkBox.setChecked(false);
//                    return;
//                }
//                // 产品相同
                businesses.put(currentItem.getId(), currentItem);
                currentItem.setChecked(true);
            } else {
                // 取消当前选中
                businesses.remove(currentItem.getId());
                currentItem.setChecked(false);
            }
        }
        // 更新底部统计数据
        selectAllView.updateSelectCount(businesses.size() + "个");
        // 是否选中全选
        if (businesses.size() == unpaidCount) {
            selectAllView.selectAll();
        } else {
            selectAllView.deselectAll();
        }
    }

    /**
     * 全选未支付订单
     */
    private void selectAllUnpaidOrder() {
        if (list.size() == 0) {
            ToastUtils.showShort(getString(R.string.no_data));
            return;
        }
        // 如果不存在“未支付”状态订单，不做任何操作
        if (unpaidCount == 0) {
            ToastUtils.showShort(getString(R.string.no_unpaid_order));
            return;
        }

        // 存在未支付状态订单
        if (selectAllView.isSelectAll()) {
            selectAllView.deselectAll();
            // 清理数据
            businesses.clear();
            for (ResourceListBean item : list) {
                item.setChecked(false);
            }
        } else {
            // 判断
            for (int i = 0; i < list.size(); i++) {
                ResourceListBean item = list.get(i);
                // 判断当前条目是否为“未支付”状态
                if (TextUtils.equals(item.getOrderStatus(), "unpaid")) {
                    // 如果当前未支付订单集合没有数据
                    if (businesses.size() == 0) {
                        businesses.put(item.getId(), item);
                    } else {
                        // 判断当前的订单产品，是否和之前订单产品相同
                        ResourceListBean old = businesses.valueAt(0);
                        if (TextUtils.equals(old.getCategoryName(), item.getCategoryName())) {
                            // 相同，则添加记录
                            businesses.put(item.getId(), item);
                        } else {
                            // 存在不同，则清空记录
                            businesses.clear();
                            break;
                        }
                    }
                }
            }
            // 记录完毕，判断是否选中全选
            if (businesses.size() == unpaidCount) {
                selectAllView.selectAll();
                for (ResourceListBean item : list) {
                    item.setChecked(true);
                }
            } else {
                // 暂时并不做这一限制
                ToastUtils.showShort("只能选择同一产品订单");
            }
        }
        selectAllView.updateSelectCount(businesses.size() + "个");
        adapter.notifyDataSetChanged();
    }

    /**
     * 编辑
     */
    private void edit() {
        titleView.setRight("取消");
        // 出现底部全选条
        selectAllView.setVisibility(View.VISIBLE);
        // 出现复选按钮
        adapter.showMultiSelect();
    }

    /**
     * 取消
     */
    private void cancel() {
        titleView.setRight("编辑");
        selectAllView.setVisibility(View.GONE);
        adapter.hideMultiSelect();
    }

    /**
     * 去支付
     */
    private void navigateToPay() {
        StringBuilder orderIds = new StringBuilder();
        BigDecimal bigDecimal = new BigDecimal("0.0");
        for (int i = 0; i < businesses.size(); i++) {
            ResourceListBean order = businesses.valueAt(i);
            orderIds.append(order.getId()).append(",");
            double bidDeposit = order.getBidDeposit();
            BigDecimal decimal = new BigDecimal(bidDeposit + "");
            bigDecimal = bigDecimal.add(decimal);
        }
        double payAmount = bigDecimal.doubleValue();
        PayActivity.startFromVaccineBus(this, "vaccine_bus", payAmount, userAccountType, vendorId, orderIds.toString());
    }

    private void navigateToDetail(AdapterView<?> adapterView, View view, int position, long id) {
        ResourceListBean business = list.get(position);
        Class clazz = null;
        switch (business.getOrderStatus()) {
            case "unpaid":
                clazz = OrderUnpaidActivity.class;
                break;
            case "bidding":
                clazz = OrderSubmitActivity.class;
                break;
            case "to_be_signed":
                clazz = OrderAlreadyActivity.class;
                break;
            case "already_signed":
                clazz = OrderCompletedActivity.class;
                break;
            case "end":
                clazz = OrderEndActivity.class;
                break;
            case "not_passed":
            case "defaulted":
                clazz = OrderErrorActivity.class;
                break;
        }
        Intent intent = new Intent(this, clazz);
        intent.putExtra("order", business);
        startActivity(intent);
    }

    private void handleEmptyData(ArrayList<?> list) {
        if (list.size() == 0) {
            //显示emptyView
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_business_res;
    }
}
