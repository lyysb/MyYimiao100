package com.yimiao100.sale.insurance

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.activity.RichesActivity
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.CorporateBean
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.bean.PersonalBean
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.jetbrains.anko.find

class AssInsCorActivity : BaseActivity(), TitleView.TitleBarOnClickListener, CheckUtil.PersonalPassedListener, CheckUtil.CorporatePassedListener {

    lateinit var orderIds: String
    lateinit var userAccountType: String
    var applyNum: Double = 0.0
    lateinit var mAccount: TextView
    lateinit var mPhone: TextView
    lateinit var mCash: Button

    private val URL_CASH_WITHDRAWAL = "${Constant.BASE_URL}/api/insure/deposit_cash_withdrawal"
    private val ORDER_ID = "orderIds"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ass_ins_cor)

        initVariate()

        initView()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initVariate() {
        orderIds = intent.getStringExtra("orderIds")
        userAccountType = intent.getStringExtra("userAccountType")
        applyNum = intent.getDoubleExtra("applyNum", -1.0)
    }

    private fun initView() {
        find<TitleView>(R.id.ass_ins_cor_apply_title).setOnTitleBarClick(this)
        mAccount = find(R.id.ass_ins_cor_company_account)
        mAccount.text = "申请推广保证金提现：${FormatUtils.MoneyFormat(applyNum)}元"
        mPhone = find(R.id.ass_ins_cor_company_phone)
        mCash = find(R.id.ass_ins_cor_apply_cash)
        mCash.setOnClickListener {
            // 提示确认提现
            showDialog()
        }
        find<ImageView>(R.id.ass_ins_cor_apply_service).setOnClickListener {
            //打开客服
            Util.enterCustomerService(this)
        }
    }

    private fun initData() {
        when (userAccountType) {
            "personal" -> {
                CheckUtil.checkPersonal(this, this)
            }
            "corporate" -> {
                CheckUtil.checkCorporate(this, this)
            }
        }

    }

    override fun handlePersonal(personal: PersonalBean) {
        mPhone.text = "联系方式：${personal.personalPhoneNumber}"
    }

    override fun handleCorporate(corporate: CorporateBean) {
        mPhone.text = "联系方式：${corporate.personalPhoneNumber}"
    }

    /**
     * 提示确认提现
     */
    private fun showDialog() {
        val builder = AlertDialog.Builder(this@AssInsCorActivity, R
                .style.dialog)
        val v = View.inflate(this, R.layout.dialog_confirm_promotion, null)
        builder.setView(v)
        builder.setCancelable(false)
        val msg = v.find<TextView>(R.id.dialog_msg)

        when (userAccountType) {
            "personal" -> msg.text = getString(R.string.assurance_withdrawal_personal)
            "corporate" -> msg.text = getString(R.string.assurance_withdrawal_corporate)
        }
        val btn1 = v.find<TextView>(R.id.dialog_promotion_bt1)
        val btn2 = v.find<TextView>(R.id.dialog_promotion_bt2)
        val dialog = builder.create()
        btn1.setOnClickListener { dialog.dismiss() }
        btn2.setOnClickListener {
            dialog.dismiss()
            applyCash()
        }
        dialog.show()
    }

    /**
     * 提交申请
     */
    private fun applyCash() {
        //禁止点击按钮，避免重复点击造成重复请求
        mCash.isEnabled = false
        OkHttpUtils.post().url(URL_CASH_WITHDRAWAL).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(ORDER_ID, orderIds).build().execute(object : StringCallback() {
            override fun onError(call: Call, e: Exception, id: Int) {
                //再次设置按钮可以点击
                mCash.isEnabled = true
                Util.showTimeOutNotice(currentContext)
            }

            override fun onResponse(response: String, id: Int) {
                //再次设置按钮可以点击
                mCash.isEnabled = true
                LogUtil.d("保证金提现：" + response)
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
