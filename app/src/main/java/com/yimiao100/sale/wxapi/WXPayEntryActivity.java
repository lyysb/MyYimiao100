package com.yimiao100.sale.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.utils.Constant;



/**
 * 微信支付回调
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI mWeChatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //调用微信API之前需要向微信注册APP_ID
        mWeChatId = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false);

        mWeChatId.handleIntent(getIntent(), this);
    }

    /**
     * 启动模式是SingleTop的时候，再次启动自己，调用该方法
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        mWeChatId.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {


    }

    /**
     * 得到支付结果回调
     * @param baseResp
     */
    @Override
    public void onResp(BaseResp baseResp) {
         //检查是否由微信支付
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Log.d("onResp", "onPayFinish, errCode = " + baseResp.errCode);// 支付结果码
            Intent intent = new Intent();
            intent.setAction("com.yimiao100.wx");
            intent.putExtra("code",  baseResp.errCode);
            sendBroadcast(intent);
            finish();
        }
//        0 支付成功
//        -1 发生错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//        -2 用户取消 发生场景：用户不支付了，点击取消，返回APP。
    }
}
