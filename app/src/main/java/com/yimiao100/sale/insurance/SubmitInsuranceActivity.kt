package com.yimiao100.sale.insurance

import android.app.ProgressDialog
import android.content.*
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import butterknife.ButterKnife
import com.tencent.mm.sdk.constants.Build
import com.tencent.mm.sdk.modelpay.PayReq
import com.tencent.mm.sdk.openapi.IWXAPI
import com.tencent.mm.sdk.openapi.WXAPIFactory
import com.yimiao100.sale.R
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.Business
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.bean.InsuranceInfo
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.jetbrains.anko.find
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class SubmitInsuranceActivity : BaseActivity(), TitleView.TitleBarOnClickListener {


    lateinit var mTitle: TitleView
    lateinit var mAmount: TextView
    lateinit var mWx: RadioButton
    lateinit var mSubmitPromotionPay: Button
    lateinit var mProgressDialog: ProgressDialog
    lateinit var mFrequency: String
    lateinit var mMark: String
    lateinit var mChannel: String
    lateinit var mUserAccountType: String
    lateinit var URL_PAY: String

    private val RESOURCE_ID = "resourceId"
    private val FREQUENCY = "frequency"
    private val USER_ACCOUNT_TYPE = "userAccountType"
    private val CHANNEL = "channel"
    private val ORDER_ID = "orderId"

    private var mParams: HashMap<String, String> = HashMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_insurance)
        ButterKnife.bind(this)

        initView()

        initData()

        initReceiver()
    }

    private fun initView() {
        mTitle = find(R.id.submit_insurance_title)
        mTitle.setOnTitleBarClick(this)
        mAmount = find(R.id.submit_insurance_amount)
        mWx = find(R.id.submit_insurance_wx)
        mSubmitPromotionPay = find(R.id.submit_insurance_pay)
        //默认选中微信支付
        mWx.isChecked = true
        mSubmitPromotionPay.setOnClickListener {
            //判断选中的支付方式
            if (mWx.isChecked) {
                //按钮禁止点击
                mSubmitPromotionPay.isEnabled = false
                mChannel = "wx"
                //调用微信支付接口
                weChatPay()
            }
        }
    }

    private fun initData() {
        mUserAccountType = intent.getStringExtra("userAccountType")
        LogUtil.d("userAccountType is " + mUserAccountType)
        //根据不同的来源添加不同的参数，显示不同的数据
        mMark = intent.getStringExtra("mark")
        LogUtil.d("mark is " + mMark)
        when (mMark) {
            "insurance" -> {
                URL_PAY = "${Constant.BASE_URL}/api/insure/place_order"
                val insuranceInfo = intent.getParcelableExtra<InsuranceInfo>("insuranceInfo")
                // 实付款金额
                mAmount.text = "实付款：${FormatUtils.RMBFormat(insuranceInfo.bidDeposit)}"
                mFrequency = intent.getStringExtra("frequency")
                LogUtil.d("frequency is $mFrequency")
                //添加参数
                mParams.put(RESOURCE_ID, insuranceInfo.id.toString())
                mParams.put(FREQUENCY, mFrequency)
            }
            "order" -> {
                URL_PAY = "${Constant.BASE_URL}/api/insure/pay_bid_deposit"
                val order = intent.getParcelableExtra<Business>("order")
                mAmount.text = "实付款：${FormatUtils.RMBFormat(order.bidDeposit)}"
                //添加参数
                mParams.put(ORDER_ID, order.id.toString())
            }
        }

    }

    private fun initReceiver() {
        val filter = IntentFilter()
        filter.addAction("com.yimiao100.wx")
        registerReceiver(mReceiver, filter)
    }


    /**
     * 微信原生支付接口
     */
    private fun weChatPay() {
        //调用微信API之前需要向微信注册APP_ID
        val weChatId = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false)
        //将该app注册到微信
        weChatId.registerApp(Constant.WX_APP_ID)
        //调起支付之前可以检查是否安装微信
        val wxAppInstalled = weChatId.isWXAppInstalled
        if (!wxAppInstalled) {
            //提示用户安装微信
            ToastUtil.showShort(currentContext, "请先安装微信")
            mSubmitPromotionPay.isEnabled = true
            return
        }
        //如果安装微信，检查微信版本是否支持支付
        val isPaySupported = weChatId.wxAppSupportAPI >= Build.PAY_SUPPORTED_SDK_INT
        if (isPaySupported) {
            //可以发起支付
            toWxPay(weChatId)
        } else {
            ToastUtil.showShort(currentContext, "您目前微信版本不支持支付，请先升级微信")
            mSubmitPromotionPay.isEnabled = true
        }
    }

    /**
     * 进行微信支付

     * @param weChatId
     */
    private fun toWxPay(weChatId: IWXAPI) {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog.setMessage("请求数据中..请稍后")
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
        //链接服务器，生成支付订单
        OkHttpUtils.post().url(URL_PAY).addHeader(ACCESS_TOKEN, accessToken)
                .params(mParams)
                .addParams(USER_ACCOUNT_TYPE, mUserAccountType).addParams(CHANNEL, mChannel)
                .build().connTimeOut(30000L).readTimeOut(30000L).execute(object : StringCallback() {
            override fun onError(call: Call, e: Exception, id: Int) {
                mProgressDialog.dismiss()
                mSubmitPromotionPay.isEnabled = true
                e.printStackTrace()
                Util.showTimeOutNotice(currentContext)
            }


            override fun onResponse(response: String, id: Int) {
                mProgressDialog.dismiss()
                mSubmitPromotionPay.isEnabled = true
                LogUtil.d(response)
                val errorBean = JSON.parseObject(response, ErrorBean::class.java)
                when (errorBean!!.status) {
                    "success" -> {
                        LogUtil.d("result success")
                        //从服务端获取返回信息
                        val jsonObject: JSONObject?
                        try {
                            jsonObject = JSONObject(response)
                            if (null != jsonObject && jsonObject.has("payRequest")) {
                                val payRequest = jsonObject.getJSONObject("payRequest")
                                val req = PayReq()
                                req.appId = payRequest.getString("appid")
                                req.partnerId = payRequest.getString("partnerid")
                                req.prepayId = payRequest.getString("prepayid")
                                req.nonceStr = payRequest.getString("noncestr")
                                req.timeStamp = payRequest.getString("timestamp")
                                req.packageValue = payRequest.getString("package")
                                req.sign = payRequest.getString("sign")
                                LogUtil.d("SubmitPromotionActivity enter wx")
                                //去WXPayEntry做回调
                                weChatId.sendReq(req)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            ToastUtil.showShort(currentContext, "支付异常，请稍后再试")
                        }

                    }
                    "failure" -> Util.showError(currentContext, errorBean.reason)
                }
            }
        })
    }

    private val mReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val code = intent.getIntExtra("code", 1)
            //收到广播后，处理结果
            handleResult(code)
        }
    }

    private fun handleResult(code: Int) {
        LogUtil.d("SubmitInsuranceActivity receive broadcast, code is " + code)
        //0 支付成功
        //-1 发生错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
        //-2 用户取消 发生场景：用户不支付了，点击取消，返回APP。
        val builder = AlertDialog.Builder(this@SubmitInsuranceActivity)
        builder.setTitle("支付结果")
        when (code) {
            0 -> builder.setMessage("支付成功")
            -1 -> builder.setMessage("支付失败，请稍后再试")
            -2 -> builder.setMessage("支付取消")
            else -> builder.setMessage("支付失败，请稍后再试")
        }
        builder.setCancelable(false)
        builder.setPositiveButton("ok") { dialog, which ->
            if (0 == code) {
                //支付成功，返回列表页
                var clz: Class<*>? = null
                when (mMark) {
                    "insurance" -> clz = InsuranceActivity::class.java
                    "order" -> clz = BusinessInsuranceActivity::class.java
                }
                val intent = Intent(this@SubmitInsuranceActivity, clz)
                intent.putExtra("userAccountType", mUserAccountType)
                startActivity(intent)
            }
            //支付出现问题（取消，失败），不需要做任何操作。停留在支付页面
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {

    }
}
