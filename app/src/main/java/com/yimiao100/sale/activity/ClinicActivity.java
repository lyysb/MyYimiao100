package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.yimiao100.sale.adapter.listview.ClinicAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.CDCBean;
import com.yimiao100.sale.bean.CDCListBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 辖区门诊
 */
public class ClinicActivity extends BaseActivitySingleList{


    private final String URL_CLINIC_LIST = Constant.BASE_URL + "/api/cdc/clinic_list";
    private String mCdcId;
    private ArrayList<CDCListBean> mClinicList;
    private ClinicAdapter mClinicAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCdcId = getIntent().getStringExtra("cdcId");
        showLoadingProgress();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("辖区门诊");
    }

    @Override
    protected void onRefresh() {
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("辖区门诊E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                hideLoadingProgress();
                mSwipeRefreshLayout.setRefreshing(false);
                LogUtil.Companion.d("辖区门诊：" + response);
                //暂定直接用CDC的JavaBean，后期有需求再重新写
                CDCBean cdcBean = JSON.parseObject(response, CDCBean.class);
                switch (cdcBean.getStatus()) {
                    case "success":
                        mPage = 2;
                        mTotalPage = cdcBean.getPagedResult().getTotalPage();

                        mClinicList = cdcBean.getPagedResult().getPagedList();
                        handleEmptyData(mClinicList);
                        mClinicAdapter = new ClinicAdapter(getApplicationContext(), mClinicList);
                        mListView.setAdapter(mClinicAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, cdcBean.getReason());
                        break;
                }
            }
        });
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //携带数据到详情页
        Intent intent = new Intent(this, ClinicDetailActivity.class);
        CDCListBean clinic = mClinicList.get(position);
        Bundle bundle = new Bundle();
        //门诊名称
        String clinicName = clinic.getClinicName();
        bundle.putString("clinicName", clinicName);
        //新生儿建卡数
        int cardAmount = clinic.getCardAmount();
        bundle.putString("cardAmount", cardAmount + "");
        //狂犬病使用量
        int useAmount = clinic.getUseAmount();
        bundle.putString("useAmount", useAmount + "");
        //地址
        String address = clinic.getAddress();
        bundle.putString("address", address);
        //电话
        String phoneNumber = clinic.getPhoneNumber();
        bundle.putString("phoneNumber", phoneNumber);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onLoadMore() {
        getBuild(mPage).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("辖区门诊E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("辖区门诊：" + response);
                mListView.onLoadMoreComplete();
                //暂定直接用CDC的JavaBean，后期有需求再重新写
                CDCBean cdcBean = JSON.parseObject(response, CDCBean.class);
                switch (cdcBean.getStatus()) {
                    case "success":
                        mPage++;
                        mClinicList.addAll(cdcBean.getPagedResult().getPagedList());
                        mClinicAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(currentContext, cdcBean.getReason());
                        break;
                }
            }
        });
    }
    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_CLINIC_LIST)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "").addParams(PAGE_SIZE, mPageSize).addParams("cdcId", mCdcId)
                .build();
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
