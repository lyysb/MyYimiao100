package com.yimiao100.sale.vaccine

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.bean.PersonalBean
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity


class OverduePersonalActivity : BaseActivity(), TitleView.TitleBarOnClickListener, CheckUtil.PersonalPassedListener {


    lateinit var tvEndNumber: TextView
    lateinit var tvPhoneNumber: TextView

    val URL_CASH_OVERDUE = "${Constant.BASE_URL}/api/advance/cash_withdrawal"
    val USER_ACCOUNT_TYPE = "userAccountType"
    val userAccountType = "personal"
    var applyAmount: Double = 0.0


    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overdue_personal)

        initVariate()

        initView()
    }


    override fun onResume() {
        super.onResume()
        CheckUtil.checkPersonal(this, this)
    }

    private fun initVariate() {
        applyAmount = intent.getDoubleExtra("applyAmount", 0.0)
    }

    private fun initView() {
        find<TitleView>(R.id.overdue_personal_title).setOnTitleBarClick(this)
        tvEndNumber = find(R.id.overdue_personal_end)
        find<TextView>(R.id.overdue_personal_money).text = FormatUtils.RMBFormat(applyAmount)
        tvPhoneNumber = find(R.id.overdue_personal_phone)
        find<ImageView>(R.id.overdue_personal_apply_service).setOnClickListener {
            //打开客服
            Util.enterCustomerService(this)
        }
        find<Button>(R.id.overdue_personal_apply_cash).setOnClickListener {
            //申请提现确定
            showDialog()
        }
    }

    /**
     * 申请提现确定
     */
    private fun showDialog() {
        val builder = AlertDialog.Builder(this, R.style.dialog)
        val v = View.inflate(this, R.layout.dialog_confirm_promotion, null)
        builder.setView(v)
        builder.setCancelable(false)
        val msg = v.find<TextView>(R.id.dialog_msg)
        msg.text = getString(R.string.overdue_withdrawal_per)

        val btn1 = v.find<Button>(R.id.dialog_promotion_bt1)
        val btn2 = v.find<Button>(R.id.dialog_promotion_bt2)
        val dialog = builder.create()
        btn1.setOnClickListener { dialog.dismiss() }
        btn2.setOnClickListener {
            dialog.dismiss()
            applyCash()
        }
        dialog.show()
    }

    private fun applyCash() {
        OkHttpUtils.post().url(URL_CASH_OVERDUE).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(USER_ACCOUNT_TYPE, userAccountType)
                .build().execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                LogUtil.d("逾期垫款对公提现：\n$response")
                JSON.parseObject(response, ErrorBean::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            startActivity<RichVaccineActivity>()
                        }
                        else -> {
                            Util.showError(currentContext, it.reason)
                        }
                    }
                }
            }

            override fun onError(call: okhttp3.Call?, e: java.lang.Exception, id: Int) {

            }
        })
    }
    override fun handlePersonal(personal: PersonalBean) {
        LogUtil.d("personal is\n$personal")
        val bankNumber = personal.bankCardNumber
        if (bankNumber.length > 4) {
            tvEndNumber.text = "尾号${bankNumber.substring(bankNumber.length - 4)}"
        } else {
            tvEndNumber.text = bankNumber
        }
        //设置联系方式
        tvPhoneNumber.text = "联系方式：${personal.personalPhoneNumber}"
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }


}

