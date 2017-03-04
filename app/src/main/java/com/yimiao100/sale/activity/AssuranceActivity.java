package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.AssuranceAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.AssuranceBean;
import com.yimiao100.sale.bean.AssuranceList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.PullToRefreshListView;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * 保证金提现
 */
public class AssuranceActivity extends BaseActivity implements TitleView.TitleBarOnClickListener,
        SwipeRefreshLayout.OnRefreshListener, AssuranceAdapter.OnCheckedChangeListener,
        PullToRefreshListView.OnRefreshingListener {

    @BindView(R.id.assurance_title)
    TitleView mAssuranceTitle;
    @BindView(R.id.assurance_confirm)
    ImageButton mAssuranceConfirm;
    @BindView(R.id.ll_assurance_confirm)
    LinearLayout mLlAssuranceConfirm;
    @BindView(R.id.assurance_refresh)
    SwipeRefreshLayout mAssuranceRefresh;
    @BindView(R.id.assurance_company_list_view)
    PullToRefreshListView mAssuranceCompanyListView;
    @BindView(R.id.assurance_check_count)
    TextView mAssuranceCheckCount;
    @BindView(R.id.assurance_account)
    TextView mAssuranceAccount;

    private final String URL_ASSURANCE = Constant.BASE_URL + "/api/fund/deposit_order_list";
    private final String VENDOR_ID = "vendorId";
    private final String USER_ACCOUNT_TYPE = "userAccountType";

    private int mVendorId;
    private String mUserAccountType;
    //计数
    private int mCheckedCount = 0;
    //统计申请提现金额
    private double mApplyNum = 0;
    //统计选中ID
    private HashMap<Integer, Integer> checkedIDs = new HashMap<>();

    //记录选中的订单
    private ArrayList<AssuranceList> mCheckedList = new ArrayList<>();
    private AssuranceAdapter mAssuranceAdapter;
    private ArrayList<AssuranceList> mAssuranceLists;
    private String mLogUrl;
    private String mVendorName;
    private CircleImageView mLogoImage;
    private TextView mVendorNameTextView;
    private TextView mProductCount;
    private TextView mTotalAmount;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mVendorId = getIntent().getIntExtra("vendorId", -1);
        mUserAccountType = getIntent().getStringExtra(USER_ACCOUNT_TYPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assurance);
        ButterKnife.bind(this);

        mLogUrl = getIntent().getStringExtra("logoImageUrl");
        mVendorName = getIntent().getStringExtra("vendorName");
        LogUtil.Companion.d("userAccountType is " + mUserAccountType);

        initView();

        onRefresh();
    }

    private void initView() {
        mAssuranceTitle.setOnTitleBarClick(this);
        initEmptyView();

        initRefreshView();


        initListView();
    }

    private void initEmptyView() {
        mEmptyView = findViewById(R.id.assurance_empty);
        TextView emptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
        emptyText.setText("一分耕耘，一分收获，请耐心等待。");
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ico_bond_factory_list_detailed), null, null);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //HeaderView的高度
        layoutParams.setMargins(0, DensityUtil.dp2px(this, 85), 0, 0);
        mEmptyView.setLayoutParams(layoutParams);
    }

    private void initRefreshView() {
        //设置刷新
        mAssuranceRefresh.setOnRefreshListener(this);
        //设置下拉圆圈的颜色
        mAssuranceRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R
                        .color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mAssuranceRefresh.setDistanceToTriggerSync(400);
    }


    private void initListView() {
        View headView = View.inflate(this, R.layout.head_vendor, null);
        //厂家logo
        mLogoImage = (CircleImageView) headView.findViewById(R.id.head_vendor_logo);
        Picasso.with(this).load(mLogUrl).placeholder(R.mipmap.ico_default_short_picture)
                .resize(DensityUtil.dp2px(this, 50), DensityUtil.dp2px(this, 50)).into(mLogoImage);
        //厂家名
        mVendorNameTextView = (TextView) headView.findViewById(R.id.head_vendor_title);
        mVendorNameTextView.setText(mVendorName);
        //产品数量
        mProductCount = (TextView) headView.findViewById(R.id.head_vendor_product);
        //总金额
        mTotalAmount = (TextView) headView.findViewById(R.id.head_vendor_money);


        mAssuranceCompanyListView.addHeaderView(headView);
        mAssuranceCompanyListView.setSwipeRefreshLayoutEnabled(new PullToRefreshListView
                .SwipeRefreshLayoutEnabledListener() {
            @Override
            public void swipeEnabled(boolean enable) {
                mAssuranceRefresh.setEnabled(enable);
            }
        });
        //上拉加载监听
        mAssuranceCompanyListView.setOnRefreshingListener(this);
    }


    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_ASSURANCE).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, "10").addParams(VENDOR_ID,
                        mVendorId + "").addParams(USER_ACCOUNT_TYPE, mUserAccountType).build();
    }


    @OnClick({R.id.assurance_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.assurance_confirm:
                //提交
                assuranceConfirm();
                break;
        }
    }

    /**
     * 进入到提现申请确认界面
     */
    private void assuranceConfirm() {
        //遍历Map，拼接订单id
        if (checkedIDs.size() != 0) {
            StringBuffer orderIds = new StringBuffer("");
            Set<Map.Entry<Integer, Integer>> entrySet = checkedIDs.entrySet();
            for (Map.Entry<Integer, Integer> entry : entrySet) {
                Integer orderId = entry.getValue();
                orderIds.append("," + orderId);
            }
            //删除第一个逗号
            orderIds.delete(0, 1);
            Intent intent = new Intent(this, AssuranceCompanyActivity.class);
            intent.putExtra(USER_ACCOUNT_TYPE, mUserAccountType);
            intent.putExtra("orderIds", orderIds.toString());
            intent.putExtra("applyNum", mApplyNum);
            startActivity(intent);
        } else {
            ToastUtil.showLong(getApplicationContext(), "请选择订单");
        }
    }


    @Override
    public void onCheckedChanged(int position, boolean isChecked) {
        companyCount(position, isChecked);
    }


    /**
     * 记录当前选中的内容
     * @param position
     * @param isChecked
     */
    private void companyCount(int position, boolean isChecked) {
        AssuranceList temp;
        temp = mAssuranceAdapter.getItem(position);
        mCheckedList.add(temp);
        //获得每个订单中的保证金
        double totalDepositWithdraw = temp != null ? temp.getTotalDepositWithdraw() : 0;
        //获得每个订单id
        int id = temp.getId();
        if (isChecked) {
            mCheckedCount++;
            mApplyNum += totalDepositWithdraw;
            checkedIDs.put(position, id);
        } else {
            mCheckedCount--;
            mApplyNum -= totalDepositWithdraw;
            checkedIDs.remove(position);
        }
        mAssuranceCheckCount.setText("已选择：" + mCheckedCount);
        mAssuranceAccount.setText("您目前申请提现的金额是：" + mApplyNum + "元");
    }

    @Override
    public void onRefresh() {
        refreshCompanyData();
    }

    private void refreshCompanyData() {
        mCheckedCount = 0;
        mApplyNum = 0;
        mAssuranceCheckCount.setText("已选择：" + mCheckedCount);
        mAssuranceAccount.setText("您目前申请提现的金额是：" + mApplyNum + "元");
        //重新加载对公
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("保证金提现账户E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 停止刷新
                        mAssuranceRefresh.setRefreshing(false);
                    }
                }, 2000);
                LogUtil.Companion.d("保证金提现账户：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage = 2;
                        AssuranceBean assuranceBean = JSON.parseObject(response, AssuranceBean.class);
                        mTotalPage = assuranceBean.getPagedResult().getTotalPage();
                        int productQtyStat = assuranceBean.getStat().getProductQtyStat();
                        double totalAmountStat = assuranceBean.getStat().getTotalAmountStat();
                        mProductCount.setText(productQtyStat + "\n产品");
                        mTotalAmount.setText(FormatUtils.MoneyFormat(totalAmountStat) + "\n总金额");
                        mAssuranceLists = JSON.parseObject(response, AssuranceBean.class)
                                .getPagedResult().getPagedList();
                        //控制数据显示和隐藏
                        if (mAssuranceLists.size() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                        mAssuranceAdapter = new AssuranceAdapter(mAssuranceLists);
                        mAssuranceAdapter.setOnCheckedChangeListener(AssuranceActivity.this);
                        mAssuranceCompanyListView.setAdapter(mAssuranceAdapter);
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
        //对公
        loadMoreCompany();
    }


    private void loadMoreCompany() {
        if (mPage <= mTotalPage) {
            getBuild(mPage).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    LogUtil.Companion.d("保证金可提现-对公账户E：" + e.getMessage());
                    Util.showTimeOutNotice(currentContext);
                }

                @Override
                public void onResponse(String response, int id) {
                    LogUtil.Companion.d("保证金可提现-对公账户：" + response);
                    ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                    mAssuranceCompanyListView.onLoadMoreComplete();
                    switch (errorBean.getStatus()) {
                        case "success":
                            mPage++;

                            mAssuranceLists.addAll(JSON.parseObject(response, AssuranceBean
                                    .class).getPagedResult().getPagedList());
                            mAssuranceAdapter.notifyDataSetChanged();
                            break;
                        case "failure":
                            Util.showError(currentContext, errorBean.getReason());
                            break;
                    }
                }
            });
        } else {
            mAssuranceCompanyListView.noMore();
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
