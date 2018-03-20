package com.yimiao100.sale.ui.pay;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.yimiao100.sale.api.Api;
import com.yimiao100.sale.bean.PayRequest;
import com.yimiao100.sale.mvpbase.BaseModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 支付网络请求
 * Created by Michel on 2018/3/20.
 */
public class PayModel extends BaseModel implements PayContract.Model {

    private final PayPresenter presenter;

    public PayModel(PayPresenter presenter) {
        super();
        this.presenter = presenter;
    }

    /**
     * 请求支付
     */
    @Override
    public void requestPay(String bizData) {
        Api.getInstance().requestPay(accessToken, bizData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(payResult -> {
                    switch (payResult.getStatus()) {
                        case "success":
                            PayRequest payRequest = payResult.getPayRequest();
                            PayReq req = new PayReq();
                            req.appId = payRequest.getAppid();
                            req.partnerId = payRequest.getPartnerid();
                            req.prepayId = payRequest.getPrepayid();
                            req.nonceStr = payRequest.getNoncestr();
                            req.timeStamp = payRequest.getTimestamp();
                            req.packageValue = payRequest.getPackageValue();
                            req.sign = payRequest.getSign();
                            presenter.payRequestSuccess(req);
                            break;
                        case "failure":
                            presenter.payRequestFailure(payResult.getReason());
                            break;
                    }
                }, throwable -> presenter.onError(throwable.getMessage()));
    }
}
