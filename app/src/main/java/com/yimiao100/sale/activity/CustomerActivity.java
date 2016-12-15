package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.CustomerAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.CDCBean;
import com.yimiao100.sale.bean.CDCListBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.PullToRefreshListView;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 客户界面
 */
public class CustomerActivity extends BaseActivity implements TitleView.TitleBarOnClickListener, SwipeRefreshLayout.OnRefreshListener,  AdapterView.OnItemClickListener, PullToRefreshListView.OnRefreshingListener {


    @BindView(R.id.customer_title)
    TitleView mCustomerTitle;
    @BindView(R.id.customer_my)
    RadioButton mCustomerMy;
    @BindView(R.id.customer_all)
    RadioButton mCustomerAll;
    @BindView(R.id.customer_my_customer)
    PullToRefreshListView mCustomerMyCustomer;
    @BindView(R.id.customer_all_customer)
    PullToRefreshListView mCustomerAllCustomer;
    @BindView(R.id.customer_refresh)
    SwipeRefreshLayout mCustomerRefresh;
    @BindView(R.id.customer_search)
    FrameLayout mCustomerSearch;


    private final String CDC_LIST = "/api/cdc/cdc_list";

    private final String USER_CDC_LIST = "/api/cdc/user_cdc_list";
    private ArrayList<CDCListBean> mCdcAllList;
    private CustomerAdapter mCdcAllAdapter;
    private ArrayList<CDCListBean> mUserCdcList;
    private CustomerAdapter mUserCdcAdapter;

    private int PAGE_MY;
    private int TOTAL_PAGE_MY;

    private int PAGE_ALL;
    private int TOTAL_PAGE_ALL;
    private String mUser_cdc_list_url;
    private String mCdc_list_url;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        ButterKnife.bind(this);

        initView();

        initData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    private void initView() {
        //标题栏
        mCustomerTitle.setOnTitleBarClick(this);
        //默认选中“我的客户”
        mCustomerMy.setChecked(true);

        initEmptyView();

        initRefreshView();

        initListView();
    }

    private void initEmptyView() {
        mEmptyView = findViewById(R.id.customer_empty_view);
        TextView emptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
        emptyText.setText("太孤单了吧，赶快去全部客户里挑选您想要\n推广的客户吧。");
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ico_my_client), null, null);
    }

    private void initRefreshView() {
        //刷新控件
        mCustomerRefresh.setOnRefreshListener(this);
        //设置下拉圆圈的颜色
        mCustomerRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mCustomerRefresh.setDistanceToTriggerSync(400);
        //解决滑动冲突
        mCustomerMyCustomer.setSwipeRefreshLayoutEnabled(new PullToRefreshListView.SwipeRefreshLayoutEnabledListener() {
            @Override
            public void swipeEnabled(boolean enable) {
                mCustomerRefresh.setEnabled(enable);
            }
        });
        mCustomerAllCustomer.setSwipeRefreshLayoutEnabled(new PullToRefreshListView.SwipeRefreshLayoutEnabledListener() {
            @Override
            public void swipeEnabled(boolean enable) {
                mCustomerRefresh.setEnabled(enable);
            }
        });
    }

    private void initListView() {
        //设置条目点击跳转
        mCustomerMyCustomer.setOnItemClickListener(this);
        mCustomerAllCustomer.setOnItemClickListener(this);
        //设置加载更多监听
        mCustomerMyCustomer.setOnRefreshingListener(this);
        mCustomerAllCustomer.setOnRefreshingListener(this);
    }

    private void initData() {
        //加载我的客户
        loadUserCDC();
        //加载全部客户
        loadAllCDC();
    }

    /**
     * 加载我的客户
     */
    private void loadUserCDC() {
        //用户CDC列表url
        mUser_cdc_list_url = Constant.BASE_URL + USER_CDC_LIST;
        //联网请求数据
        getBuild(mUser_cdc_list_url, 1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("用户CDC列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("用户CDC列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        PAGE_MY = 2;
                        TOTAL_PAGE_MY = JSON.parseObject(response, CDCBean.class).getPagedResult().getTotalPage();
                        //解析JSON数据
                        mUserCdcList = JSON.parseObject(response, CDCBean.class).getPagedResult().getPagedList();
                        //如果列表为空
                        if (mUserCdcList.size() == 0 && mCustomerMy.isChecked()) {
                            //显示空白页
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            //如果列表不为空
                            //隐藏空白页
                            mEmptyView.setVisibility(View.INVISIBLE);
                        }
                        //填充“我的客户”Adapter
                        mUserCdcAdapter = new CustomerAdapter(getApplicationContext(), mUserCdcList);
                        mCustomerMyCustomer.setAdapter(mUserCdcAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }
    /**
     * 加载全部客户
     */
    private void loadAllCDC() {
        //CDC列表url
        mCdc_list_url = Constant.BASE_URL + CDC_LIST;
        //联网请求数据
        getBuild(mCdc_list_url, 1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("CDC列表E：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("CDC列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //解析JSON数据
                        mCdcAllList = JSON.parseObject(response, CDCBean.class).getPagedResult().getPagedList();
                        TOTAL_PAGE_ALL = JSON.parseObject(response, CDCBean.class).getPagedResult().getTotalPage();
                        PAGE_ALL = 2;
                        //填充“全部客户”Adapter
                        mCdcAllAdapter = new CustomerAdapter(getApplicationContext(), mCdcAllList);
                        mCustomerAllCustomer.setAdapter(mCdcAllAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        //根据Radio的选中的状态的不同，进行不同的网络请求
        if (mCustomerMy.isChecked()) {
            //联网请求数据
            getBuild(mUser_cdc_list_url, 1).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    LogUtil.d("用户CDC列表E：" + e.getMessage());
                    Util.showTimeOutNotice(currentContext);
                }

                @Override
                public void onResponse(String response, int id) {
                    LogUtil.d("用户CDC列表：" + response);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 停止刷新
                            mCustomerRefresh.setRefreshing(false);
                        }
                    }, 1000);
                    ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                    switch (errorBean.getStatus()) {
                        case "success":
                            PAGE_MY = 2;
                            TOTAL_PAGE_MY = JSON.parseObject(response, CDCBean.class).getPagedResult().getTotalPage();
                            //解析JSON数据
                            mUserCdcList = JSON.parseObject(response, CDCBean.class).getPagedResult().getPagedList();
                            if (mUserCdcList.size() == 0 && mCustomerMy.isChecked()) {
                                //显示空白页
                                mEmptyView.setVisibility(View.VISIBLE);
                            } else {
                                //如果列表不为空
                                //隐藏空白页
                                mEmptyView.setVisibility(View.INVISIBLE);
                            }
                            //填充“我的客户”Adapter
                            mUserCdcAdapter = new CustomerAdapter(getApplicationContext(), mUserCdcList);
                            mCustomerMyCustomer.setAdapter(mUserCdcAdapter);
                            break;
                        case "failure":
                            Util.showError(currentContext, errorBean.getReason());
                            break;
                    }
                }
            });
        } else if (mCustomerAll.isChecked()) {
            //联网请求数据
            getBuild(mCdc_list_url, 1).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    LogUtil.d("CDC列表E：" + e.getMessage());
                    Util.showTimeOutNotice(currentContext);
                }

                @Override
                public void onResponse(String response, int id) {
                    LogUtil.d("CDC列表：" + response);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 停止刷新
                            mCustomerRefresh.setRefreshing(false);
                        }
                    }, 1000);
                    ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                    switch (errorBean.getStatus()) {
                        case "success":
                            PAGE_ALL = 2;
                            TOTAL_PAGE_ALL = JSON.parseObject(response, CDCBean.class).getPagedResult().getTotalPage();
                            //解析JSON数据
                            mCdcAllList = JSON.parseObject(response, CDCBean.class).getPagedResult().getPagedList();
                            //填充“全部客户”Adapter
                            mCdcAllAdapter = new CustomerAdapter(getApplicationContext(), mCdcAllList);
                            mCustomerAllCustomer.setAdapter(mCdcAllAdapter);
                            break;
                        case "failure":
                            Util.showError(currentContext, errorBean.getReason());
                            break;
                    }
                }
            });
        }
    }

    private RequestCall getBuild(String url, int page) {
        return OkHttpUtils.post().url(url)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize)
                .build();
    }

    @OnClick({R.id.customer_my, R.id.customer_all, R.id.customer_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.customer_my:
                //显示“我的客户”相关
                showMyCustomer();
                break;
            case R.id.customer_all:
                //显示“全部客户”相关
                showAllCustomer();
                break;
            case R.id.customer_search:
                ToastUtil.showShort(getApplicationContext(), "敬请期待");
                break;
        }
    }

    private void showMyCustomer() {
        //标题显示
        mCustomerTitle.setTitle("我的客户");
        //Radio的选中状态
        mCustomerMy.setChecked(true);
        mCustomerAll.setChecked(false);
        //列表显示
        mCustomerMyCustomer.setVisibility(View.VISIBLE);
        if (mUserCdcList.size() == 0) {
            //显示空白页
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            //隐藏空白页
            mEmptyView.setVisibility(View.INVISIBLE);
        }
        mCustomerAllCustomer.setVisibility(View.GONE);
    }

    private void showAllCustomer() {
        //标题显示
        mCustomerTitle.setTitle("全部客户");
        //Radio的选中状态
        mCustomerMy.setChecked(false);
        mCustomerAll.setChecked(true);
        //列表显示
        mCustomerMyCustomer.setVisibility(View.GONE);
        mCustomerAllCustomer.setVisibility(View.VISIBLE);
        //隐藏空白页
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //根据RadioButton选中状态，携带不同列表的数据进入详情页
        Intent intent = new Intent(this, CustomerDetailActivity.class);
        if (mCustomerMy.isChecked()) {
            //携带“我的客户”的数据
            CDCListBean cdc = mUserCdcList.get(position);
            Bundle bundle = new Bundle();
            //CDC名称
            String cdcName = cdc.getCdcName();
            bundle.putString("cdcName", cdcName);
            //新生儿建卡数
            int cardAmount = cdc.getCardAmount();
            bundle.putString("cardAmount", cardAmount + "");
            //狂犬病使用量
            int useAmount = cdc.getUseAmount();
            bundle.putString("useAmount", useAmount + "");
            //地址
            String address = cdc.getAddress();
            bundle.putString("address", address);
            //电话
            String phoneNumber = cdc.getPhoneNumber();
            bundle.putString("phoneNumber", phoneNumber);
            //和用户的关系0-未收藏，1-已收藏
            int userAddStatus = cdc.getUserAddStatus();
            bundle.putInt("userAddStatus", userAddStatus);
            //CDCId
            int cdcId = cdc.getId();
            bundle.putString("cdcId", cdcId + "");
            intent.putExtras(bundle);
        } else {
            CDCListBean cdc = mCdcAllList.get(position);
            Bundle bundle = new Bundle();
            //携带“全部客户”的数据
            //CDC名称
            String cdcName = cdc.getCdcName();
            bundle.putString("cdcName", cdcName);
            //新生儿建卡数
            int cardAmount = cdc.getCardAmount();
            bundle.putString("cardAmount", cardAmount + "");
            //狂犬病使用量
            int useAmount = cdc.getUseAmount();
            bundle.putString("useAmount", useAmount + "");
            //地址
            String address = cdc.getAddress();
            bundle.putString("address", address);
            //电话
            String phoneNumber = cdc.getPhoneNumber();
            bundle.putString("phoneNumber", phoneNumber);
            //和用户的关系0-未收藏，1-已收藏
            int userAddStatus = cdc.getUserAddStatus();
            bundle.putInt("userAddStatus", userAddStatus);
            //CDCId
            int cdcId = cdc.getId();
            bundle.putString("cdcId", cdcId + "");
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    public void onLoadMore() {
        if (mCustomerMy.isChecked()) {
            //加载“我的客户”的更多数据
            if (PAGE_MY <= TOTAL_PAGE_MY) {
                loadMyMore();
            }else {
                mCustomerMyCustomer.noMore();
            }
        } else if (mCustomerAll.isChecked()) {
            //加载“全部客户”的更多数据
            if (PAGE_ALL <= TOTAL_PAGE_ALL) {
                loadAllMore();
            } else {
                mCustomerMyCustomer.noMore();
            }
        }
    }

    private void loadMyMore() {
        //联网请求数据
        getBuild(mUser_cdc_list_url, PAGE_MY).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("用户CDC列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("用户CDC列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        PAGE_MY ++;
                        mUserCdcList.addAll(JSON.parseObject(response, CDCBean.class).getPagedResult().getPagedList());
                        mUserCdcAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
                mCustomerMyCustomer.onLoadMoreComplete();
            }
        });
    }

    private void loadAllMore() {
        //联网请求数据
        getBuild(mCdc_list_url, PAGE_ALL).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("CDC列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("CDC列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        PAGE_ALL++;
                        LogUtil.d(PAGE_ALL + "");
                        mCdcAllList.addAll(JSON.parseObject(response, CDCBean.class).getPagedResult().getPagedList());
                        mCdcAllAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
                mCustomerAllCustomer.onLoadMoreComplete();
            }
        });
    }




    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
