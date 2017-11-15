package com.yimiao100.sale.insurance

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.activity.RichesActivity
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.bean.PersonalBean
import com.yimiao100.sale.bean.TaxBean
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.utils.Constant.TAX_RATE
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.jetbrains.anko.find

class PromoInsPerConfirmActivity : BaseActivity(), TitleView.TitleBarOnClickListener, CheckUtil.PersonalPassedListener {

    lateinit var mPromotionCashEnd: TextView
    lateinit var mPromotionCashPhone: TextView
    lateinit var mPromotionCashFinal: TextView
    lateinit var orderIds: String
    var amount: Double = 0.0

    private val URL_APPLY_CASH = "${Constant.BASE_URL}/api/insure/sale_cash_withdrawal"
    private val URL_TEX = "${Constant.BASE_URL}/api/tax/default"
    private val ORDER_IDS = "orderIds"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo_ins_per_confirm)

        initVariate()

        initView()

        initData()
    }

    override fun onResume() {
        super.onResume()
        CheckUtil.checkPersonal(this, this)
    }

    override fun handlePersonal(personal: PersonalBean) {
        LogUtil.d("personal is\n$personal")
        val bankNumber = personal.bankCardNumber
        if (bankNumber.length > 4) {
            mPromotionCashEnd.text = "尾号${bankNumber.substring(bankNumber.length - 4)}"
        } else {
            mPromotionCashEnd.text = bankNumber
        }
        //设置联系方式
        mPromotionCashPhone.text = "联系方式：${personal.personalPhoneNumber}"
    }


    private fun initVariate() {
        orderIds = intent.getStringExtra("orderIds")
        amount = intent.getDoubleExtra("amount", -1.0)
    }

    private fun initView() {
        find<TitleView>(R.id.promo_ins_per_title).setOnTitleBarClick(this)
        mPromotionCashEnd = find(R.id.promo_ins_per_end)
        mPromotionCashPhone = find(R.id.promo_ins_per_phone)
        find<TextView>(R.id.promo_ins_per_money).text = FormatUtils.RMBFormat(amount)
        mPromotionCashFinal = find(R.id.promo_ins_per_final)
        find<ImageView>(R.id.promo_ins_per_apply_service).setOnClickListener {
            //打开客服
            Util.enterCustomerService(this)
        }

        find<ImageButton>(R.id.promo_ins_per_apply_cash).setOnClickListener {
            //申请提现确认
            showDialog()
        }
    }

    private fun initData() {
        //根据说率计算税后金额
        OkHttpUtils.post().url(URL_TEX).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(object : StringCallback() {
            override fun onError(call: Call, e: Exception, id: Int) {
                LogUtil.d("tax error is :")
                e.printStackTrace()
            }

            override fun onResponse(response: String, id: Int) {
                LogUtil.d("tax :" + response)
                JSON.parseObject(response, TaxBean::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            val taxRate = it.tax.taxRate / 100
                            mPromotionCashFinal.text = FormatUtils.RMBFormat(amount * (1 - taxRate))
                        }
                        else -> Util.showError(currentContext, it.reason)
                    }
                }
            }
        })

    }

    /**
     * 申请提现确定弹窗
     */
    private fun showDialog() {
        val builder = AlertDialog.Builder(this@PromoInsPerConfirmActivity, R.style.dialog)
        val v = View.inflate(this, R.layout.dialog_confirm_promotion, null)
        builder.setView(v)
        builder.setCancelable(false)
        val msg = v.findViewById(R.id.dialog_msg) as TextView

        msg.text = getString(R.string.promotion_withdrawal_personal)
        val btn1 = v.findViewById(R.id.dialog_promotion_bt1) as Button
        val btn2 = v.findViewById(R.id.dialog_promotion_bt2) as Button
        val dialog = builder.create()
        btn1.setOnClickListener { dialog.dismiss() }
        btn2.setOnClickListener {
            dialog.dismiss()
            applyCash()
        }
        dialog.show()
    }

    /**
     * 申请提现
     */
    private fun applyCash() {
        OkHttpUtils.post().url(URL_APPLY_CASH)
                .addHeader(ACCESS_TOKEN, accessToken)
                .addParams(ORDER_IDS, orderIds)
                .build().execute(object : StringCallback() {

            override fun onError(call: Call, e: Exception, id: Int) {
                LogUtil.d("推广费申请提现确认E：" + e.localizedMessage)
                Util.showTimeOutNotice(currentContext)
            }

            override fun onResponse(response: String, id: Int) {
                LogUtil.d("推广费申请提现：" + response)
                val errorBean = JSON.parseObject(response, ErrorBean::class.java)
                when (errorBean!!.status) {
                    "success" -> {
                        ToastUtil.showShort(currentContext, "申请成功")
                        //申请提现成功，返回财富列表
                        startActivity(Intent(applicationContext, RichInsActivity::class.java))
                    }
                    "failure" -> Util.showError(currentContext, errorBean.reason)
                }
            }
        })
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }

}
