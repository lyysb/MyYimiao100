package com.yimiao100.sale.vaccine

import android.os.Bundle
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

class RichVaccineActivity : BaseActivity(), TitleView.TitleBarOnClickListener {

    lateinit var titleView: TitleView
    lateinit var scholarshipItem: RelativeLayout
    lateinit var subTitle: TextView

    lateinit var totalAmount: TextView
    lateinit var scholarship: TextView
    lateinit var promotion: TextView
    lateinit var assurance: TextView
    lateinit var overdue: TextView

    var overdueCorporateAmount: Double = 0.0
    var overduePersonalAmount: Double = 0.0


    private val URL_USER_FUND_ALL = "${Constant.BASE_URL}/api/fund/user_fund_all"
    private val MODULE_TYPE = "moduleType"
    private val EXAM_REWARD_WITHDRAWAL = "exam_reward_withdrawal" //课程考试奖励可提现
    private val SALE_WITHDRAWAL = "sale_withdrawal"               //销售资金可提现
    private val DEPOSIT_WITHDRAWAL = "deposit_withdrawal"         //保证金可提现

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rich_vaccine)


        initView()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initView() {
        titleView = find(R.id.riches_vaccine_title)
        titleView.setOnTitleBarClick(this)
        subTitle = find(R.id.riches_vaccine_subtitle)
        scholarshipItem = find(R.id.riches_vaccine_scholarship)

        totalAmount = find(R.id.riches_vaccine_total_amount)
        scholarship = find(R.id.riches_vaccine_scholarship_count)
        promotion = find(R.id.riches_vaccine_promotion_count)
        assurance = find(R.id.riches_vaccine_assurance_count)
        overdue = find(R.id.riches_vaccine_overdue_count)

        // 奖学金
        find<RelativeLayout>(R.id.riches_vaccine_scholarship).setOnClickListener {
            startActivity<VendorListActivity>(MODULE_TYPE to EXAM_REWARD_WITHDRAWAL)
        }

        // 推广奖励
        find<RelativeLayout>(R.id.riches_vaccine_promotion).setOnClickListener {
            startActivity<VendorListActivity>(MODULE_TYPE to SALE_WITHDRAWAL)
        }

        // 推广保证金
        find<RelativeLayout>(R.id.riches_vaccine_assurance).setOnClickListener {
            startActivity<VendorListActivity>(MODULE_TYPE to DEPOSIT_WITHDRAWAL)
        }

        // 逾期垫款
        find<RelativeLayout>(R.id.riches_vaccine_overdue).setOnClickListener {
            startActivity<OverdueActivity>(
                    "overdueCorporateAmount" to overdueCorporateAmount,
                    "overduePersonalAmount" to overduePersonalAmount)
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
            totalAmount.text = FormatUtils.RMBFormat(userFundAll.vaccineTotalAmount)
            scholarship.text = FormatUtils.RMBFormat(userFundAll.vaccineTotalExamReward)
            promotion.text = FormatUtils.RMBFormat(userFundAll.vaccineTotalSale)
            assurance.text = FormatUtils.RMBFormat(userFundAll.vaccineDeposit)
            overdue.text = FormatUtils.RMBFormat(userFundAll.vaccineTotalAdvance)
            overdueCorporateAmount = userFundAll.vaccineCorporateAdvance
            overduePersonalAmount = userFundAll.vaccinePersonalAdvance
        } else {
            totalAmount.text = "¥0.00"
            scholarship.text = "¥0.00"
            promotion.text = "¥0.00"
            assurance.text = "¥0.00"
            overdue.text = "¥0.00"
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
