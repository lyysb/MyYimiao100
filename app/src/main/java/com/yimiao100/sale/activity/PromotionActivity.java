package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.PromotionAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.PromotionBean;
import com.yimiao100.sale.bean.PromotionList;
import com.yimiao100.sale.ext.JSON;
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
 * 申请推广奖励提现
 */
public class PromotionActivity extends BaseActivity implements TitleView.TitleBarOnClickListener,
        SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,
        PullToRefreshListView.OnRefreshingListener, PromotionAdapter.OnCheckedChangeListener {

    @BindView(R.id.promotion_rich_title)
    TitleView mPromotionRichTitle;
    @BindView(R.id.promotion_rich_confirm)
    ImageButton mPromotionRichConfirm;
    @BindView(R.id.ll_promotion_rich_confirm)
    LinearLayout mLlPromotionRichConfirm;
    @BindView(R.id.promotion_rich_company_list_view)
    PullToRefreshListView mPromotionRichCompanyListView;
    @BindView(R.id.promotion_rich_refresh)
    SwipeRefreshLayout mPromotionRichRefresh;
    @BindView(R.id.promotion_check_count)
    TextView mPromotionCheckCount;
    @BindView(R.id.promotion_account)
    TextView mPromotionAccount;

    private final String URL_SALE = Constant.BASE_URL + "/api/fund/sale_order_list";
    private final String VENDOR_ID = "vendorId";
    private final String USER_ACCOUNT_TYPE = "userAccountType";

    private int mVendorId;
    private String mUserAccountType;

    private ArrayList<PromotionList> mPromotionLists;
    private PromotionAdapter mPromotionAdapter;
    //计数
    private int mCheckedCount = 0;
    //统计申请提现金额
    private double mApplyNum = 0;
    //统计选中ID
    private HashMap<Integer, Integer> checkedIDs = new HashMap<>();
    //记录选中的订单
    private ArrayList<PromotionList> mCheckedList = new ArrayList<>();
    private String mLogUrl;
    private String mVendorName;
    private CircleImageView mLogoImage;
    private TextView mVendorNameTextView;
    private TextView mProductCount;
    private TextView mTotalAmount;
    private View mEmptyView;
    private View mHeadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        ButterKnife.bind(this);

        mVendorId = getIntent().getIntExtra("vendorId", -1);
        mUserAccountType = getIntent().getStringExtra(USER_ACCOUNT_TYPE);
        mLogUrl = getIntent().getStringExtra("logoImageUrl");
        mVendorName = getIntent().getStringExtra("vendorName");
        LogUtil.d("userAccountType is " + mUserAccountType);

        initView();

        showLoadingProgress();

        onRefresh();
    }

    private void initView() {
        mPromotionRichTitle.setOnTitleBarClick(this);
        initRefreshView();

        initListView();

        initEmptyView();
    }

    private void initRefreshView() {
        //设置刷新
        mPromotionRichRefresh.setOnRefreshListener(this);
        //设置下拉圆圈的颜色
        mPromotionRichRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R
                        .color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mPromotionRichRefresh.setDistanceToTriggerSync(400);
    }

    private void initListView() {
        mHeadView = View.inflate(this, R.layout.head_vendor, null);
        //厂家logo
        mLogoImage = (CircleImageView) mHeadView.findViewById(R.id.head_vendor_logo);
        Picasso.with(this).load(mLogUrl).placeholder(R.mipmap.ico_default_short_picture)
                .resize(DensityUtil.dp2px(this, 50), DensityUtil.dp2px(this, 50)).into(mLogoImage);
        //厂家名字
        mVendorNameTextView = (TextView) mHeadView.findViewById(R.id.head_vendor_title);
        mVendorNameTextView.setText(mVendorName);
        //产品数量
        mProductCount = (TextView) mHeadView.findViewById(R.id.head_vendor_product);
        //总金额
        mTotalAmount = (TextView) mHeadView.findViewById(R.id.head_vendor_money);

        mPromotionRichCompanyListView.addHeaderView(mHeadView, null, false);
        mPromotionRichCompanyListView.setSwipeRefreshLayoutEnabled(new PullToRefreshListView
                .SwipeRefreshLayoutEnabledListener() {
            @Override
            public void swipeEnabled(boolean enable) {
                mPromotionRichRefresh.setEnabled(enable);
            }
        });
        //条目跳转
        mPromotionRichCompanyListView.setOnItemClickListener(this);
        //上拉加载监听
        mPromotionRichCompanyListView.setOnRefreshingListener(this);
    }


    private void initEmptyView() {
        mEmptyView = findViewById(R.id.promotion_empty_view);
        TextView emptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
        emptyText.setText(getString(R.string.empty_view_promotion));
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ico_promotion_award_detailed), null, null);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //HeaderView的高度
        layoutParams.setMargins(0, DensityUtil.dp2px(this, 85), 0, 0);
        mEmptyView.setLayoutParams(layoutParams);
    }


    @OnClick({R.id.promotion_rich_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.promotion_rich_confirm:
                //进入提现确认界面
                enterPromotionCashConfirm();
                break;
        }
    }


    @Override
    public void onRefresh() {
        mCheckedCount = 0;
        mApplyNum = 0;
        mPromotionCheckCount.setText("已选择：" + mCheckedCount);
        mPromotionAccount.setText("您目前申请提现的金额是：" + FormatUtils.MoneyFormat(mApplyNum) + "元");
        //重新加载
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("推广费可提现E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                mPromotionRichRefresh.setRefreshing(false);
                hideLoadingProgress();
                LogUtil.d("推广费提现：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        page =2;
                        PromotionBean promotionBean = JSON.parseObject(response, PromotionBean.class);
                        totalPage = promotionBean.getPagedResult().getTotalPage();
                        int productQtyStat = promotionBean.getStat().getProductQtyStat();
                        mProductCount.setText(productQtyStat + "\n产品");
                        double totalAmountStat = promotionBean.getStat().getTotalAmountStat();
                        mTotalAmount.setText(FormatUtils.MoneyFormat(totalAmountStat) + "\n总金额");

                        mPromotionLists = promotionBean.getPagedResult().getPagedList();

                        if (mPromotionLists.size() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }

                        mPromotionAdapter = new PromotionAdapter(mPromotionLists);
                        mPromotionAdapter.setOnCheckedChangeListener(PromotionActivity.this);
                        mPromotionRichCompanyListView.setAdapter(mPromotionAdapter);
                        break;
                    case "failure":
                        Util.showError(PromotionActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 进入推广费提现确认界面
     */
    private void enterPromotionCashConfirm() {
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
            Intent intent = new Intent();
            Class clz = null;
            switch (mUserAccountType) {
                case "personal":
                    clz = PromotionCashConfirmPersonalActivity.class;
                    break;
                case "corporate":
                    clz = PromotionCashConfirmActivity.class;
                    break;
            }
            intent.setClass(this, clz);
            intent.putExtra("orderIds", orderIds.toString());
            intent.putExtra("amount", mApplyNum);
            if (clz != null) {
                startActivity(intent);
            }
        } else {
            ToastUtil.showShort(currentContext, "请选择订单");
        }
    }




    /**
     * 进入订单详情
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, PromotionDetailActivity.class);
        //根据RadioButton选中状态的不同，携带不同List的数据
        PromotionList temp = mPromotionLists.get(position - 1);
        intent.putExtra("orderId", temp.getId() + "");
        //产品名
        intent.putExtra("productName", temp.getProductName());
        //厂家名
        intent.putExtra("vendorName", temp.getVendorName());
        //客户名
        intent.putExtra("customerName", temp.getCustomerName());
        //分类名
        intent.putExtra("categoryName", temp.getCategoryName());
        //剂型
        intent.putExtra("dosageForm", temp.getDosageForm());
        //规格
        intent.putExtra("spec", temp.getSpec());
        //订单号
        intent.putExtra("serialNo", temp.getSerialNo());

        startActivity(intent);
    }


    /**
     * 加载更多
     */
    @Override
    public void onLoadMore() {
        if (page <= totalPage) {
            getBuild(page).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    LogUtil.d("推广费提现E：" + e.getMessage());
                }

                @Override
                public void onResponse(String response, int id) {
                    LogUtil.d("推广费提现：" + response);
                    ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                    mPromotionRichCompanyListView.onLoadMoreComplete();
                    switch (errorBean.getStatus()) {
                        case "success":
                            page++;
                            mPromotionLists.addAll(JSON.parseObject(response,
                                    PromotionBean.class).getPagedResult().getPagedList());
                            mPromotionAdapter.notifyDataSetChanged();
                            break;
                        case "failure":
                            Util.showError(PromotionActivity.this, errorBean.getReason());
                            break;
                    }
                }
            });
        } else {
            mPromotionRichCompanyListView.noMore();
        }
    }

    @Override
    public void onCheckedChanged(int position, boolean isChecked) {
        PromotionList temp = mPromotionAdapter.getItem(position);
        mCheckedList.add(temp);
        //获得每个订单中的保证金
        double totalAmount = temp != null ? temp.getTotalAmount() : 0;
        //获得每个订单id
        int id = temp.getId();
        if (isChecked) {
            mCheckedCount++;
            mApplyNum += totalAmount;
            checkedIDs.put(position, id);
        } else {
            mCheckedCount--;
            mApplyNum -= totalAmount;
            checkedIDs.remove(position);
        }
        mPromotionCheckCount.setText("已选择：" + mCheckedCount);
        mPromotionAccount.setText("您目前申请提现的金额是：" + FormatUtils.MoneyFormat(mApplyNum)  + "元");
    }

    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_SALE).addHeader(ACCESS_TOKEN, accessToken).addParams
                (VENDOR_ID, mVendorId + "").addParams(PAGE, page + "").addParams(PAGE_SIZE, pageSize)
                .addParams(USER_ACCOUNT_TYPE, mUserAccountType).build();
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
