package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.VendorListAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Vendor;
import com.yimiao100.sale.bean.VendorListBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 厂家列表
 * Created by 亿苗通 on 2016/10/30.
 */

public class VendorListActivity extends BaseActivitySingleList {

    private final String URL_VENDOR_LIST = Constant.BASE_URL + "/api/vendor/vendor_list_by_user";
    private final String MODULE_TYPE = "moduleType";

    private String mModuleType = "deposit_withdrawal";  //保证金可提现

    private int ENTER_FROM;

    private final int FROM_EXAM_REWARD = 0;         //来自奖学金
    private final int FROM_SALE = 1;                //来自推广费
    private final int FROM_DEPOSIT = 2;             //来自保证金
    private final int FROM_STUDY = 3;               //来自学习
    private final int FROM_RECONCILIATION = 4;      //来自对账

    private ArrayList<Vendor> mVendorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ENTER_FROM = getIntent().getIntExtra("enter_from", -1);
        super.onCreate(savedInstanceState);
        switch (ENTER_FROM) {
            case FROM_EXAM_REWARD:
                //来自奖学金
                setEmptyView("一寸光阴一寸金，快到资源里面申请推广吧。", R.mipmap.ico_scholarship_list);
                break;
            case FROM_SALE:
                //来自推广费
                setEmptyView("君子爱财、取之有道，快到资源里面申请推广吧。", R.mipmap.ico_extension_reward);
                break;
            case FROM_DEPOSIT:
                //来自保证金
                setEmptyView("有钱使得鬼推磨，快到资源里面申请推广吧。", R.mipmap.ico_bond_factory_list);
                break;
            case FROM_STUDY:
                //来自学习
                setEmptyView("考试是可以“挣钱”的。但是，暂时没有学习任务。", R.mipmap.ico_study_extension);
                break;
            case FROM_RECONCILIATION:
                //来自对账
                setEmptyView("早起的鸟儿有虫吃，快到资源里面申请推广吧。", R.mipmap.ico_reconciliation);
                break;
        }
    }


    @Override
    protected void initView() {
        super.initView();
        mListView.cancleLoadMore();
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("厂家列表");
    }

    @Override
    protected void onRefresh() {
        PostFormBuilder postFormBuilder = OkHttpUtils.post().url(URL_VENDOR_LIST)
                .addHeader(ACCESS_TOKEN, mAccessToken);
        if (ENTER_FROM == FROM_DEPOSIT) {
            //保证金提现，增加一个参数
            postFormBuilder.addParams(MODULE_TYPE, mModuleType);
        }
        postFormBuilder.build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("厂家列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                LogUtil.d("厂家列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mVendorList = JSON.parseObject(response, VendorListBean
                                .class).getVendorList();
                        handleEmptyData(mVendorList);
                        mListView.setAdapter(new VendorListAdapter(mVendorList));
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Vendor vendor = mVendorList.get(position);
        switch (ENTER_FROM) {
            case FROM_EXAM_REWARD:
                //进入对应的奖学金提现界面
                Intent scholarshipIntent = new Intent(this, ScholarshipActivity.class);
                scholarshipIntent.putExtra("vendorId", vendor.getId());
                scholarshipIntent.putExtra("logoImageUrl", vendor.getLogoImageUrl());
                scholarshipIntent.putExtra("vendorName", vendor.getVendorName());
                startActivity(scholarshipIntent);
                break;
            case FROM_SALE:
                //进入对应的推广费界面
                Intent promotionIntent = new Intent(this, PromotionActivity.class);
                promotionIntent.putExtra("vendorId", vendor.getId());
                promotionIntent.putExtra("logoImageUrl", vendor.getLogoImageUrl());
                promotionIntent.putExtra("vendorName", vendor.getVendorName());
                startActivity(promotionIntent);
                break;
            case FROM_DEPOSIT:
                //进入对应的保证金界面
                Intent assuranceIntent = new Intent(this, AssuranceActivity.class);
                assuranceIntent.putExtra("vendorId", vendor.getId());
                assuranceIntent.putExtra("logoImageUrl", vendor.getLogoImageUrl());
                assuranceIntent.putExtra("vendorName", vendor.getVendorName());
                startActivity(assuranceIntent);
                break;
            case FROM_STUDY:
                //进入对应的学习-我的成就-学习成绩详情界面
                Intent scoreIntent = new Intent(this, ScoreDetailActivity.class);
                scoreIntent.putExtra("vendorId", vendor.getId());
                scoreIntent.putExtra("logoImageUrl", vendor.getLogoImageUrl());
                scoreIntent.putExtra("vendorName", vendor.getVendorName());
                startActivity(scoreIntent);
                break;
            case FROM_RECONCILIATION:
                //进入对应的对账
                Intent reconciliationIntent = new Intent(this, ReconciliationActivity.class);
                reconciliationIntent.putExtra("vendorId", vendor.getId());
                startActivity(reconciliationIntent);
                break;
        }
    }



    @Override
    protected void onLoadMore() {

    }
}
