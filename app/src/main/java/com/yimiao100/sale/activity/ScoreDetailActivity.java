package com.yimiao100.sale.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.expendable.ScoreDetailAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ExamInfo;
import com.yimiao100.sale.bean.ExamInfoBean;
import com.yimiao100.sale.bean.ExamInfoStat;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * 成绩详情
 */
public class ScoreDetailActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, TitleView.TitleBarOnClickListener, View.OnClickListener {

    @BindView(R.id.score_detail_title)
    TitleView mScoreDetailTitle;
    @BindView(R.id.score_expandable_list_view)
    ExpandableListView mScoreExpandableListView;
    @BindView(R.id.score_swipe)
    SwipeRefreshLayout mScoreSwipe;

    private final String URL_EXAM_INFO = Constant.BASE_URL + "/api/course/exam_info";
    private final String VENDOR_ID = "vendorId";
    private final String USER_ACCOUNT_TYPE = "userAccountType";

    private int mVendorId;
    private String mUserAccountType;
    private String mLogUrl;
    private CircleImageView mLogoImage;
    private String mVendorName;
    private TextView mVendorNameTextView;
    private TextView mProductCount;
    private TextView mTotalBonus;
    private ScoreDetailAdapter mScoreDetailAdapter;
    private ArrayList<ExamInfo> mStatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_detail);
        ButterKnife.bind(this);

        showLoadingProgress();

        mVendorId = getIntent().getIntExtra("vendorId", -1);
        mLogUrl = getIntent().getStringExtra("logoImageUrl");
        mVendorName = getIntent().getStringExtra("vendorName");
        mUserAccountType = getIntent().getStringExtra("userAccountType");

        initView();

        initData();
    }

    private void initView() {
        mScoreDetailTitle.setOnTitleBarClick(this);
        initRefreshView();

        initExpandableListView();
    }

    private void initRefreshView() {
        //设置刷新
        mScoreSwipe.setOnRefreshListener(this);
        //设置下拉圆圈的颜色
        mScoreSwipe.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mScoreSwipe.setDistanceToTriggerSync(400);
    }

    private void initExpandableListView() {
        View headView = View.inflate(this, R.layout.head_vendor, null);

        mLogoImage = (CircleImageView) headView.findViewById(R.id.head_vendor_logo);
        Picasso.with(this).load(mLogUrl).placeholder(R.mipmap.ico_default_short_picture)
                .resize(DensityUtil.dp2px(this, 50), DensityUtil.dp2px(this, 50)).into(mLogoImage);

        mVendorNameTextView = (TextView) headView.findViewById(R.id.head_vendor_title);
        mVendorNameTextView.setText(mVendorName);

        mProductCount = (TextView) headView.findViewById(R.id.head_vendor_product);
        mTotalBonus = (TextView) headView.findViewById(R.id.head_vendor_money);

        ImageView imageView = (ImageView) headView.findViewById(R.id.head_vendor_notice);
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(this);


        mScoreExpandableListView.addHeaderView(headView, null, false);
    }

    private void initData() {
        //联网请求数据
        OkHttpUtils.post().url(URL_EXAM_INFO).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(VENDOR_ID, mVendorId + "")
                .addParams(USER_ACCOUNT_TYPE, mUserAccountType)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("课程考试结果E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("课程考试结果：" + response);
                hideLoadingProgress();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ExamInfoBean examInfoBean = JSON.parseObject(response, ExamInfoBean.class);
                        ExamInfoStat stat = examInfoBean.getStat();
                        mProductCount.setText(stat.getProductQtyStat() + "\n产品");
                        mTotalBonus.setText(FormatUtils.MoneyFormat(stat.getTotalAmountStat()) + "\n总奖励");

                        mStatList = examInfoBean.getStatList();
                        mScoreDetailAdapter = new ScoreDetailAdapter(mStatList);
                        mScoreExpandableListView.setAdapter(mScoreDetailAdapter);
                        break;
                    case "failure":
                        Util.showError(ScoreDetailActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        //联网请求数据
        OkHttpUtils.post().url(URL_EXAM_INFO).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(VENDOR_ID, mVendorId + "")
                .addParams(USER_ACCOUNT_TYPE, mUserAccountType)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("课程考试结果E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("课程考试结果：" + response);
                mScoreSwipe.setRefreshing(false);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ExamInfoBean examInfoBean = JSON.parseObject(response, ExamInfoBean.class);
                        ExamInfoStat stat = examInfoBean.getStat();
                        mProductCount.setText(stat.getProductQtyStat() + "\n产品");
                        mTotalBonus.setText(FormatUtils.MoneyFormat(stat.getTotalAmountStat()) + "\n总奖金");

                        mStatList = examInfoBean.getStatList();
                        mScoreDetailAdapter = new ScoreDetailAdapter(mStatList);
                        mScoreExpandableListView.setAdapter(mScoreDetailAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
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

    @Override
    public void onClick(View v) {
        //显示奖学金提示框
        showNoticeDialog();
    }

    private void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.score_detail_dialog_msg));
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
