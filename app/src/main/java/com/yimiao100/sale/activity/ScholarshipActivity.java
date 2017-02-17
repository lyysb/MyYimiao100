package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.ScholarshipCashConfirmActivity;
import com.yimiao100.sale.adapter.listview.ScholarshipAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ExamInfo;
import com.yimiao100.sale.bean.ExamInfoBean;
import com.yimiao100.sale.bean.ExamInfoStat;
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
 * 奖学金提现列表
 */
public class ScholarshipActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, TitleView.TitleBarOnClickListener, ScholarshipAdapter.OnCheckedChangeListener {

    @BindView(R.id.scholarship_list_title)
    TitleView mScholarshipListTitle;
    @BindView(R.id.scholarship_list_check_count)
    TextView mScholarshipListCheckCount;
    @BindView(R.id.scholarship_list_account)
    TextView mScholarshipListAccount;
    @BindView(R.id.scholarship_list_company_list_view)
    PullToRefreshListView mScholarshipListCompanyListView;
    @BindView(R.id.scholarship_list_refresh)
    SwipeRefreshLayout mScholarshipListRefresh;


    private final String URL_EXAM_REWARD_LIST = Constant.BASE_URL + "/api/fund/exam_reward_list";
    private final String VENDOR_ID = "vendorId";

    private int mVendorId;
    private ScholarshipAdapter mScholarshipAdapter;
    //计数
    private int mCheckedCount = 0;
    //统计申请提现金额
    private double mApplyNum = 0;
    //统计选中ID
    private HashMap<Integer, ArrayList<Integer>> checkedIDs = new HashMap<>();

    //记录选中的订单
    private ArrayList<ExamInfo> mCheckedList = new ArrayList<>();
    private String mLogUrl;
    private String mVendorName;
    private CircleImageView mLogoImage;
    private TextView mVendorNameTextView;
    private TextView mTotalMoney;
    private View mEmptyView;
    private View mHeadView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholarship);
        ButterKnife.bind(this);

        mVendorId = getIntent().getIntExtra("vendorId", -1);
        mLogUrl = getIntent().getStringExtra("logoImageUrl");
        mVendorName = getIntent().getStringExtra("vendorName");

        initView();

        onRefresh();
    }

    private void initView() {
        mScholarshipListTitle.setOnTitleBarClick(this);
        initRefreshLayout();

        initListView();

        initEmptyView();
    }
    private void initRefreshLayout() {
        //设置刷新
        mScholarshipListRefresh.setOnRefreshListener(this);
        //设置下拉圆圈的颜色
        mScholarshipListRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mScholarshipListRefresh.setDistanceToTriggerSync(400);
    }

    private void initListView() {
        mHeadView = View.inflate(this, R.layout.head_vendor, null);
        //厂家logo
        mLogoImage = (CircleImageView) mHeadView.findViewById(R.id.head_vendor_logo);
        Picasso.with(this).load(mLogUrl).placeholder(R.mipmap.ico_default_short_picture)
                .resize(DensityUtil.dp2px(this, 50), DensityUtil.dp2px(this, 50)).into(mLogoImage);
        //厂家名称
        mVendorNameTextView = (TextView) mHeadView.findViewById(R.id.head_vendor_title);
        mVendorNameTextView.setText(mVendorName);
        //总费用
        mTotalMoney = (TextView) mHeadView.findViewById(R.id.head_vendor_product);
        mTotalMoney.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        //右侧view设置隐藏
        mHeadView.findViewById(R.id.head_vendor_money).setVisibility(View.GONE);

        mScholarshipListCompanyListView.addHeaderView(mHeadView);
        mScholarshipListCompanyListView.setSwipeRefreshLayoutEnabled(new PullToRefreshListView
                .SwipeRefreshLayoutEnabledListener() {
            @Override
            public void swipeEnabled(boolean enable) {
                mScholarshipListRefresh.setEnabled(enable);
            }
        });
        //不需要上拉加载
        mScholarshipListCompanyListView.cancleLoadMore();
    }

    private void initEmptyView() {
        mEmptyView = findViewById(R.id.scholarship_empty);
        TextView emptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
        emptyText.setText("活到老、学到老，快去学习页面完成考试任务吧。");
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ico_scholarship_detailed), null, null);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //HeaderView的高度
        layoutParams.setMargins(0, DensityUtil.dp2px(this, 85), 0, 0);
        mEmptyView.setLayoutParams(layoutParams);
    }

    @OnClick(R.id.scholarship_list_confirm)
    public void onClick() {
        enterPromotionCashConfirm();
    }
    /**
     * 进入奖学金提现确认界面
     */
    private void enterPromotionCashConfirm() {
        //遍历Map，拼接订单id
        if (checkedIDs.size() != 0) {
            StringBuilder orderIds = new StringBuilder("");
            Set<Map.Entry<Integer, ArrayList<Integer>>> entries = checkedIDs.entrySet();
            for (Map.Entry<Integer, ArrayList<Integer>> entry : entries) {
                StringBuilder temp = new StringBuilder("");
                for (int i = 0; i < entry.getValue().size(); i++) {
                    if (i == 0) {
                        temp.append(entry.getValue().get(i));
                    } else {
                        temp.append("," + entry.getValue().get(i));
                    }
                }
                orderIds.append("," + temp.toString());
            }
            //删除第一个逗号
            orderIds.delete(0, 1);
            Intent intent = new Intent(this, ScholarshipCashConfirmActivity.class);
            intent.putExtra("orderIds", orderIds.toString());
            intent.putExtra("amount", mApplyNum);
            startActivity(intent);
        } else {
            ToastUtil.showShort(getApplicationContext(), "请选择订单");
        }
    }
    @Override
    public void onRefresh() {
        getBuild().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("奖学金提现E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                mScholarshipListRefresh.setRefreshing(false);
                LogUtil.Companion.d("奖学金提现：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ExamInfoBean examInfoBean = JSON.parseObject(response, ExamInfoBean.class);
                        ExamInfoStat stat = examInfoBean.getStat();
                        Spanned totalMoney = Html.fromHtml("总奖励：" + "<font color=\"#d24141\">" + FormatUtils.MoneyFormat(stat.getTotalAmountStat()) + "</font>" + "元");
                        mTotalMoney.setText(totalMoney);
                        ArrayList<ExamInfo> statList = examInfoBean.getStatList();
                        if (statList.size() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                        mScholarshipAdapter = new ScholarshipAdapter(statList);
                        mScholarshipAdapter.setOnCheckedChangeListener(ScholarshipActivity.this);
                        mScholarshipListCompanyListView.setAdapter(mScholarshipAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(int position, boolean isChecked) {
        ExamInfo scholarship = mScholarshipAdapter.getItem(position);
        mCheckedList.add(scholarship);
        //获得每个订单中的奖学金
        double totalAmount = scholarship != null ? scholarship.getTotalAmount() : 0;
        //获得每个订单id集合
        ArrayList<Integer> temp = scholarship.getCourseExamItemIdList();

        if (isChecked) {
            mCheckedCount++;
            mApplyNum += totalAmount;
            checkedIDs.put(position, temp);
        } else {
            mCheckedCount--;
            mApplyNum -= totalAmount;
            checkedIDs.remove(position);
        }
        mScholarshipListCheckCount.setText("已选择：" + mCheckedCount);
        mScholarshipListAccount.setText("您目前申请提现的金额是：" + mApplyNum + "元");

    }

    private RequestCall getBuild() {
        return OkHttpUtils.post().url(URL_EXAM_REWARD_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(VENDOR_ID, mVendorId + "").build();
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
