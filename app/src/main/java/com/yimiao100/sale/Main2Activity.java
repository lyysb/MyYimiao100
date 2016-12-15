package com.yimiao100.sale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pingplusplus.android.Pingpp;
import com.pingplusplus.android.PingppLog;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.utils.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import okhttp3.Call;

public class Main2Activity extends BaseActivity implements View.OnClickListener {


    private static String YOUR_URL ="http://161p3p2316.iask.in/ymt/api/order/pay_order";
    public static final String URL = "http://218.244.151.190/demo/charge";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button button = (Button) findViewById(R.id.button);

        PingppLog.DEBUG = true;

        button.setOnClickListener(this);
    }
    /**
    * ping++ sdk 使用流程如下：
    * 1）客户端已经有订单号、订单金额、支付渠道
    * 2）客户端请求服务端获得charge。
    * 3）收到服务端的charge，调用ping++ sdk 。
    * 4）onActivityResult 中获得支付结构。
    * 5）如果支付成功。服务端会收到ping++ 异步通知，支付成功依据服务端异步通知为准。
    */
    @Override
    public void onClick(View v) {
        //模拟向服务端请求数据，获取json数据（charge对象）
        OkHttpUtils.post().url(YOUR_URL)
                .addHeader("X-Authorization-Token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhY2Nlc3NfdG9rZW58MXw4YjJhYzlhMTBmOTk0ZjE3YTQwZTFiMTNiYzI1MDVmNiJ9.4UBjP5o6dyeTTzWlCNQTucchjoFUIZEkexRxvtWhtbjuOwv__g06sdh81sMSitwR--6B_2NI4CG2B2HasU2lAA")
                .addParams("resourceId", "1")
                .addParams("bidQty", "500")
                .addParams("userAccountType", "corporate")
                .addParams("channel", "wx")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String charge = jsonObject.getString("charge");
                    System.out.println("$$$$$$$$$$$$$$$$$");
                    System.out.println(charge);
                    System.out.println("$$$$$$$$$$$$$$$$$");
                    Pingpp.createPayment(Main2Activity.this, charge);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //发送请求
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                showMsg(result, errorMsg, extraMsg);
            }
        }
    }
    public void showMsg(String title, String msg1, String msg2) {
        String str = title;
        if (null !=msg1 && msg1.length() != 0) {
            str += "\n" + msg1;
        }
        if (null !=msg2 && msg2.length() != 0) {
            str += "\n" + msg2;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }
}
