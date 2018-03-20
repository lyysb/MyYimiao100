package com.yimiao100.sale.ui.pay;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.yimiao100.sale.mvpbase.BasePresenter;

/**
 * PayPresenter
 * Created by Michel on 2018/3/20.
 */

public class PayPresenter extends BasePresenter<PayContract.View> implements PayContract.Presenter{

    private final PayModel model;
    private IWXAPI weChatId;

    public PayPresenter() {
        model = new PayModel(this);
    }

    /**
     * 请求支付
     */
    @Override
    public void requestPay(IWXAPI weChatId, String bizData) {
        this.weChatId = weChatId;
        model.requestPay(bizData);
        getView().showPayLoading();
    }

    /**
     * 支付接口请求成功
     */
    @Override
    public void payRequestSuccess(PayReq req) {
        // 去WXPayEntry做回调
        weChatId.sendReq(req);
        getView().dismissPayLoading();
    }

    /**
     * 支付接口出错
     */
    @Override
    public void payRequestFailure(int reason) {
        getView().showFailureInfo(reason);
        getView().dismissPayLoading();
    }

    /**
     * 链接出现问题
     */
    @Override
    public void onError(String msg) {
        super.onError(msg);
        getView().dismissPayLoading();
    }
}
