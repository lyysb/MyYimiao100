package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ErrorReasonBean;
import com.yimiao100.sale.bean.ErrorReasonListBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 资讯详情-纠错页面
 */
public class ChangeErrorActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.error_title)
    TitleView mErrorTitle;
    @BindView(R.id.error_list)
    RadioGroup mErrorList;
    @BindView(R.id.error_submit)
    Button mErrorSubmit;

    private final String URL_ALL_ERROR_REASON = Constant.BASE_URL + "/api/news/all_error_reason";

    private final String URL_SUBMIT_NEWS_ERROR = Constant.BASE_URL + "/api/news/submit_news_error";
    private int mNewsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_error);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mNewsId = intent.getIntExtra("newsId", -1);

        initView();

        initData();

    }

    private void initView() {
        mErrorTitle.setOnTitleBarClick(this);
    }


    private void initData() {
        //请求网络。获取报错原因列表
        OkHttpUtils.post().url(URL_ALL_ERROR_REASON)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("资讯详情-纠错E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //解析json，填充Adapter
                        LogUtil.Companion.d("纠错列表json：" + response);
                        List<ErrorReasonListBean> errorReasonList = JSON.parseObject
                                (response, ErrorReasonBean.class).getErrorReasonList();
                        //动态添加RadioButton
                        addRadioBottom(errorReasonList);
                        break;
                    case "failure":
                        //显示错误信息
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 添加错误属性
     *
     * @param errorReasonList
     */
    private void addRadioBottom(List<ErrorReasonListBean> errorReasonList) {
        for (ErrorReasonListBean errorReason : errorReasonList) {
            RadioButton radioButton = new RadioButton(ChangeErrorActivity.this);
            //设置按钮样式
            radioButton.setButtonDrawable(R.drawable.selector_change_error);
            //设置文字距离
            radioButton.setPadding(DensityUtil.dp2px(ChangeErrorActivity.this, 21), DensityUtil
                    .dp2px(ChangeErrorActivity.this, 11), 0, DensityUtil.dp2px
                    (ChangeErrorActivity.this, 11));
            //设置按钮文字
            radioButton.setText(errorReason.getErrorReason());
            //设置按钮id
            radioButton.setId(errorReason.getId());
            //设置文字大小
            radioButton.setTextSize(14);
            //设置文字颜色
            radioButton.setTextColor(getResources().getColorStateList(R.color.change_error));
            //放入RadioGroup
            mErrorList.addView(radioButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                    .LayoutParams.WRAP_CONTENT);
        }
    }

    @OnClick(R.id.error_submit)
    public void onClick() {
        //点击后禁止按钮点击
        mErrorSubmit.setEnabled(false);
        //提交错误信息
        OkHttpUtils.post().url(URL_SUBMIT_NEWS_ERROR)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addParams("newsId", mNewsId + "")
                .addParams("errorReasonId", mErrorList.getCheckedRadioButtonId() + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //允许按钮点击
                mErrorSubmit.setEnabled(true);
                LogUtil.Companion.d("提交纠错E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                //允许按钮点击
                mErrorSubmit.setEnabled(true);
                LogUtil.Companion.d("提交纠错：" + response);
                //解析JSON
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //提交成功，返回上一层
                        ToastUtil.showShort(currentContext, getString(R.string.news_submit_error));
                        finish();
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
        //返回上一个界面
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
