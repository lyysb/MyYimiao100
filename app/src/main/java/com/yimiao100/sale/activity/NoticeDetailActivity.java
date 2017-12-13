package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.ActivityCollector;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.NoticeDetailBean;
import com.yimiao100.sale.bean.NoticedListBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.login.*;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.Html5WebView;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 我的通知-通知详情
 */
public class NoticeDetailActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.notice_detail_head)
    TitleView mNoticeDetailHead;
    @BindView(R.id.notice_detail_title)
    TextView mNoticeDetailTitle;
    @BindView(R.id.notice_detail_from)
    TextView mNoticeDetailFrom;
    @BindView(R.id.notice_detail_time)
    TextView mNoticeDetailTime;
    @BindView(R.id.notice_detail_content)
    LinearLayout mNoticeDetailContent;
    @BindView(R.id.notice_detail_account_type)
    TextView mTvAccountType;
    private int mNoticeId;

    private final String URL_USER_NOTICE = Constant.BASE_URL + "/api/notice/user_notice";
    private Html5WebView mWebView;
    private TextView textView;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mNoticeId = intent.getIntExtra("noticeId", -1);


        mNoticeDetailHead.setOnTitleBarClick(this);

        showLoadingProgress();

        initView();

        if (mNoticeId != -1) {
            initData();
        }

    }

    private void initView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mWebView = new Html5WebView(this);
        Html5WebView.showLoadingProgress = false;
        mWebView.setLayoutParams(params);

        mScrollView = new ScrollView(this);
        LinearLayout.LayoutParams svParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int _10dp = DensityUtil.dp2px(this, 10);
        svParams.setMargins(_10dp, _10dp, _10dp, _10dp);
        mScrollView.setLayoutParams(svParams);

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView = new TextView(this);
        textView.setTextColor(getResources().getColor(R.color.color333));
        textView.setLineSpacing(0, 1.2f);
        textView.setLayoutParams(tvParams);
        mScrollView.addView(textView);
    }


    private void initData() {
        OkHttpUtils.post().url(URL_USER_NOTICE)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addParams("noticeId", mNoticeId + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("通知详情E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                hideLoadingProgress();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        LogUtil.d("通知详情：" + response);
                        //解析JSON
                        NoticeDetailBean noticeDetailBean = JSON.parseObject(response,
                                NoticeDetailBean.class);
                        if (noticeDetailBean != null && noticeDetailBean.getUserNotice() != null) {
                            processingResult(noticeDetailBean);
                        } else {
                            //请重新登录
                            ToastUtil.showShort(currentContext, "账号异常，请重新登录");
                            ActivityCollector.finishAll();
                            SharePreferenceUtil.clear(currentContext);
                            com.yimiao100.sale.login.LoginActivity.start(currentContext);
                        }
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    private void processingResult(NoticeDetailBean noticeDetailBean) {
        NoticedListBean userNotice = noticeDetailBean.getUserNotice();
        if (userNotice.getNoticeTitle() != null) {
            mNoticeDetailTitle.setText(userNotice.getNoticeTitle());
        } else {
            mNoticeDetailTitle.setText("");
        }
        if (userNotice.getAccountType() != null) {
            switch (userNotice.getAccountType()) {
                case "corporate":
                    mTvAccountType.setVisibility(View.VISIBLE);
                    mTvAccountType.setBackgroundResource(R.mipmap.ico_notice_company);
                    mTvAccountType.setText("对公");
                    mTvAccountType.setTextColor(getResources().getColor(R.color.colorOrigin));
                    break;
                case "personal":
                    mTvAccountType.setText("个人");
                    mTvAccountType.setBackgroundResource(R.mipmap.ico_notice_personal);
                    mTvAccountType.setTextColor(getResources().getColor(R.color.colorMain));
                    mTvAccountType.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            mTvAccountType.setVisibility(View.GONE);
        }
        if (userNotice.getNoticeSource() != null) {
            mNoticeDetailFrom.setText("来源：" + userNotice.getNoticeSource());
        } else {
            mNoticeDetailFrom.setText("来源：");
        }
        mNoticeDetailTime.setText(TimeUtil.timeStamp2Date(userNotice
                .getCreatedAt()
                + "", "yyyy年MM月dd日 HH:mm:ss"));
        if (userNotice.getNoticeContent() != null) {
            String regex = "<(\\S*?) [^>]*>.*?</\\1>|<.*? />";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(userNotice.getNoticeContent());
            boolean matches = matcher.find();
            LogUtil.d("是否是HTML文本 : " + matches);
            if (matches) {
                LogUtil.d("加载网页通知\n" + userNotice.getNoticeContent());
                mNoticeDetailContent.addView(mWebView);
                mWebView.loadData(userNotice.getNoticeContent(), "text/html; charset=UTF-8", null);
            } else {
                LogUtil.d("加载文本通知");
                mNoticeDetailContent.addView(mScrollView);
                textView.setText(userNotice.getNoticeContent());
            }
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
