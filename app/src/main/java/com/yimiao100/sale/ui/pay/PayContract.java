package com.yimiao100.sale.ui.pay;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.yimiao100.sale.mvpbase.IBaseModel;
import com.yimiao100.sale.mvpbase.IBasePresenter;
import com.yimiao100.sale.mvpbase.IBaseView;

/**
 * 支付界面总管
 * Created by Michel on 2018/3/19.
 */

public interface PayContract {

    interface View extends IBaseView<Presenter>{
        void showPayLoading();

        void dismissPayLoading();
    }

    interface Presenter extends IBasePresenter<View>{
        void requestBizDataPay(IWXAPI weChatId, String bizData);

        void requestBidDeposit(IWXAPI weChatId, String orderIds);

        void payRequestSuccess(PayReq payReq);

        void payRequestFailure(int reason);
    }

    interface Model extends IBaseModel{
        void requestBizDataPay(String bizData);

        void requestBidDeposit(String orderIds);
    }
}
