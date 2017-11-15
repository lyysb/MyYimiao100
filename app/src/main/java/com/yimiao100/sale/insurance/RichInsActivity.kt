package com.yimiao100.sale.insurance

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.activity.VendorListActivity
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.UserFundAll
import com.yimiao100.sale.bean.UserFundBean
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import java.lang.Exception

class RichInsActivity : BaseActivity(), TitleView.TitleBarOnClickListener {

    lateinit var titleView: TitleView
    lateinit var subTitle: TextView

    lateinit var totalAmount: TextView
    lateinit var promotion: TextView
    lateinit var assurance: TextView


    private val URL_USER_FUND_ALL = "${Constant.BASE_URL}/api/fund/user_fund_all"
    private val MODULE_TYPE = "moduleType"
    private val SALE_WITHDRAWAL = "sale_withdrawal"               //销售资金可提现
    private val DEPOSIT_WITHDRAWAL = "deposit_withdrawal"         //保证金可提现

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rich_ins)

        initView()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initView() {
        titleView = find(R.id.riches_ins_title)
        titleView.setOnTitleBarClick(this)
        subTitle = find(R.id.riches_ins_subtitle)
        totalAmount = find(R.id.riches_ins_total_amount)
        promotion = find(R.id.riches_ins_promotion_count)
        assurance = find(R.id.riches_ins_assurance_count)

        // 推广奖励
        find<RelativeLayout>(R.id.riches_ins_promotion).setOnClickListener {
            startActivity<VenInsActivity>(MODULE_TYPE to SALE_WITHDRAWAL)
        }

        // 推广保证金
        find<RelativeLayout>(R.id.riches_ins_assurance).setOnClickListener {
            startActivity<VenInsActivity>(MODULE_TYPE to DEPOSIT_WITHDRAWAL)
        }
    }

    private fun initData() {
        showLoadingProgress()
        OkHttpUtils.post().url(URL_USER_FUND_ALL).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(object : StringCallback() {
            override fun onResponse(response: String, id: Int) {
                hideLoadingProgress()
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, UserFundBean::class.java)?.let {
                    when (it.status) {
                        "success" -> showDataAtView(it.userFundAll)
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                hideLoadingProgress()
                e.printStackTrace()
            }

        })
    }

    private fun showDataAtView(userFundAll: UserFundAll?) {
        if (userFundAll != null) {
            totalAmount.text = FormatUtils.RMBFormat(userFundAll.insuranceTotalAmount)
            promotion.text = FormatUtils.RMBFormat(userFundAll.insuranceTotalSale)
            assurance.text = FormatUtils.RMBFormat(userFundAll.insuranceDeposit)
        } else {
            totalAmount.text = "¥0.00"
            promotion.text = "¥0.00"
            assurance.text = "¥0.00"

        }
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
        // 进入明细
//        startActivity<RichesDetailActivity>("richType" to richType)
    }
}
