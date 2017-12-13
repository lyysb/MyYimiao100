package com.yimiao100.sale.vaccine

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.activity.ReconciliationDetailActivity
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.lang.Exception

class OverduePerConfirmActivity : BaseActivity(), TitleView.TitleBarOnClickListener {

    val URL_RECHARGE = "${Constant.BASE_URL}/api/advance/recharge"
    val ORDER_IDS = "orderIds"
    val RECONCILIATION = "reconciliation"                // 从对账过来的


    lateinit var tvApplyAmount: TextView
    lateinit var tvActualAmount: TextView

    lateinit var orderIds: String
    lateinit var from: String
    var applyAmount: Double = 0.0
    var actualAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overdue_per_confirm)

        initVariate()

        initView()
    }

    private fun initVariate() {
        applyAmount = intent.getDoubleExtra("amount", 0.0)
        actualAmount = intent.getDoubleExtra("actual", 0.0)
        orderIds = intent.getStringExtra("orderIds")
        from = intent.getStringExtra("from")

    }

    private fun initView() {
        find<TitleView>(R.id.overdue_per_cor_title).setOnTitleBarClick(this)
        tvApplyAmount = find(R.id.overdue_per_cor_apply_amount)
        tvApplyAmount.text = FormatUtils.RMBFormat(applyAmount)
        tvActualAmount = find(R.id.overdue_per_cor_actual_amount)
        tvActualAmount.text = FormatUtils.RMBFormat(actualAmount)
        find<ImageView>(R.id.overdue_per_cor_service).setOnClickListener {
            // 进入客服
            Util.enterCustomerService(this)
        }
        find<Button>(R.id.overdue_per_cor_submit).setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this@OverduePerConfirmActivity, R.style.dialog)
        val v = View.inflate(this, R.layout.dialog_confirm_promotion, null)
        builder.setView(v)
        builder.setCancelable(false)
        val msg = v.find<TextView>(R.id.dialog_msg)

        msg.text = getString(R.string.overdue_per_confirm)
        val btn1 = v.find<Button>(R.id.dialog_promotion_bt1)
        val btn2 = v.find<Button>(R.id.dialog_promotion_bt2)
        val dialog = builder.create()
        btn1.setOnClickListener { dialog.dismiss() }
        btn2.setOnClickListener {
            dialog.dismiss()
            submit()
        }
        dialog.show()
    }

    private fun submit() {
        val loadingProgress = ProgressDialogUtil.getLoadingProgress(this, "提交中")
        loadingProgress.show()
        OkHttpUtils.post().url(URL_RECHARGE).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(ORDER_IDS, orderIds)
                .build().execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                loadingProgress.dismiss()
                JSON.parseObject(response, ErrorBean::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            toast("充值成功")
                            if (TextUtils.equals(from, RECONCILIATION)) {
                                startActivity<ReconciliationDetailActivity>()
                            } else {
                                startActivity<RichVaccineActivity>()
                            }
                        }
                        else -> {
                            Util.showError(currentContext, it.reason)
                        }
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                e.printStackTrace()
                loadingProgress.dismiss()
                Util.showTimeOutNotice(currentContext)
            }

        })
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }

}
