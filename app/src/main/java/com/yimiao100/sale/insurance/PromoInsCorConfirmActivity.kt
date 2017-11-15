package com.yimiao100.sale.insurance

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
import com.yimiao100.sale.bean.CorporateBean
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.jetbrains.anko.find

class PromoInsCorConfirmActivity : BaseActivity(), TitleView.TitleBarOnClickListener, CheckUtil.CorporatePassedListener {

    lateinit var mPromotionCashEnd: TextView
    lateinit var mPromotionCashPhone: TextView
    lateinit var orderIds: String
    var amount: Double = 0.0

    private val URL_APPLY_CASH = "${Constant.BASE_URL}/api/insure/sale_cash_withdrawal"
    private val ORDER_IDS = "orderIds"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo_ins_cor_confirm)

        initVariate()

        initView()
    }

    override fun onResume() {
        super.onResume()
        CheckUtil.checkCorporate(this, this)
    }

    override fun handleCorporate(corporate: CorporateBean) {
        val bankNumber = corporate.corporateAccount
        if (bankNumber.length > 4) {
            mPromotionCashEnd.text = "尾号${bankNumber.substring(bankNumber.length - 4)}"
        } else {
            mPromotionCashEnd.text = bankNumber
        }
        //设置联系方式
        mPromotionCashPhone.text = "联系方式：${corporate.personalPhoneNumber}"

    }

    private fun initVariate() {
        orderIds = intent.getStringExtra("orderIds")
        amount = intent.getDoubleExtra("amount", -1.0)
    }

    private fun initView() {
        find<TitleView>(R.id.promo_ins_cor_cash_title).setOnTitleBarClick(this)
        mPromotionCashEnd = find(R.id.promo_ins_cor_cash_end)
        mPromotionCashPhone = find(R.id.promo_ins_cor_cash_phone)
        find<TextView>(R.id.promo_ins_cor_cash_money).text = FormatUtils.RMBFormat(amount)
        find<ImageView>(R.id.promo_ins_cor_cash_apply_service).setOnClickListener {
            //打开客服
            Util.enterCustomerService(this)
        }
        find<ImageButton>(R.id.promo_ins_cor_cash_apply_cash).setOnClickListener {
            //申请提现确定
            showDialog()
        }
    }

    /**
     * 申请提现确定
     */
    private fun showDialog() {
        val builder = AlertDialog.Builder(this@PromoInsCorConfirmActivity,
                R.style.dialog)
        val v = View.inflate(this, R.layout.dialog_confirm_promotion, null)
        builder.setView(v)
        builder.setCancelable(false)
        val msg = v.findViewById(R.id.dialog_msg) as TextView
        msg.text = getString(R.string.promotion_withdrawal_corporate)

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
