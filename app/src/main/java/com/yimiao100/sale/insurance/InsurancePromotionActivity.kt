package com.yimiao100.sale.insurance

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.activity.BindCompanyActivity
import com.yimiao100.sale.activity.BindPersonalActivity
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.CorporateBean
import com.yimiao100.sale.bean.InsuranceInfo
import com.yimiao100.sale.bean.PersonalBean
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.TitleView
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import java.math.BigDecimal

class InsurancePromotionActivity : BaseActivity(), CheckUtil.PersonalPassedListener, CheckUtil.CorporatePassedListener, TitleView.TitleBarOnClickListener {


    lateinit var insuranceInfo: InsuranceInfo
    lateinit var userAccountType: String
//    lateinit var unitChange: BigDecimal
//    lateinit var changes: BigDecimal

    lateinit var promotionTitle: TitleView
    lateinit var promotionItem1: TextView
    lateinit var promotionItem2: TextView
    lateinit var promotionItem3: TextView
    lateinit var promotionOption1: TextView
    lateinit var promotionOption2: TextView
    lateinit var promotionOption3: TextView
    lateinit var promotionNum: TextView
    lateinit var promotionTotalCount: TextView
    lateinit var promotionTotalAmount: TextView

//    var baseAmount: Double = 0.0
//    var saleDeposit: Double = 0.0
    var frequency: Int = 0  // 增量改变倍数
    private val PERSONAL = "personal"
    private val CORPORATE = "corporate"

    var increment = 0.0
    var quota = 0.0
    var saleDepositPercent = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insurance_promotion)

        initVariate()

        initView()

        initDate()
    }

    override fun onResume() {
        super.onResume()
        when (userAccountType) {
            PERSONAL -> CheckUtil.checkPersonal(this, this)
            CORPORATE -> CheckUtil.checkCorporate(this, this)
            else -> ToastUtil.showShort(this, "error")
        }
    }

    private fun initVariate() {
        insuranceInfo = intent.getParcelableExtra("insuranceInfo")
        userAccountType = intent.getStringExtra("userAccountType")
        LogUtil.d("userAccountType is $userAccountType")
        increment = insuranceInfo.increment
        quota = insuranceInfo.quota
        saleDepositPercent = insuranceInfo.saleDepositPercent
    }

    private fun initView() {
        promotionTitle = find(R.id.insurance_promotion_title)
        promotionTitle.setOnTitleBarClick(this)
        promotionItem1 = find(R.id.insurance_promotion_item1)
        promotionItem2 = find(R.id.insurance_promotion_item2)
        promotionItem3 = find(R.id.insurance_promotion_item3)
        promotionOption1 = find(R.id.insurance_promotion_option1)
        promotionOption2 = find(R.id.insurance_promotion_option2)
        promotionOption3 = find(R.id.insurance_promotion_option3)
        find<TextView>(R.id.insurance_promotion_company_name).text = insuranceInfo.companyName
        find<TextView>(R.id.insurance_promotion_product_name).text = insuranceInfo.productName
        find<TextView>(R.id.insurance_promotion_region).text =
            "${insuranceInfo.provinceName}\t\t${insuranceInfo.cityName}\t\t${insuranceInfo.areaName}"

        val startTime = TimeUtil.timeStamp2Date(insuranceInfo.startAt.toString(), "yyyy年MM月dd日")
        val endTime = TimeUtil.timeStamp2Date(insuranceInfo.endAt.toString(), "yyyy年MM月dd日")
        find<TextView>(R.id.insurance_promotion_time).text = "$startTime-$endTime"
        find<TextView>(R.id.insurance_promotion_quota).text = "${FormatUtils.MoneyFormat(insuranceInfo.quota)}元"
        find<TextView>(R.id.insurance_promotion_scale).text = "${insuranceInfo.saleDepositPercent}%"
        promotionNum = find(R.id.insurance_promotion_num)
        promotionTotalCount = find(R.id.insurance_promotion_total_count)
        promotionTotalAmount = find(R.id.insurance_promotion_total_amount)

        find<LinearLayout>(R.id.insurance_promotion_bind).setOnClickListener{
            // 进入到推广主体界面
            enterBind()
        }
        find<Button>(R.id.insurance_promotion_subtract).setOnClickListener {
            if (frequency > 0) {
                frequency--
                calculatePromotion(frequency)
            }
        }
        find<Button>(R.id.insurance_promotion_add).setOnClickListener {
            frequency++
            calculatePromotion(frequency)
        }
        find<ImageButton>(R.id.insurance_promotion_submit).setOnClickListener {
            // 显示确认弹窗
            showConfirmDialog()
        }
    }

    private fun initDate() {
        promotionNum.text = "0.00%"
        // 最终竞标数量
        promotionTotalCount.text = quota.toString()
        // 最终需要缴纳的保证金--（ 基础指标+竞标数量）*推广保证金基数-只是作为显示
        promotionTotalAmount.text =  "${FormatUtils.MoneyFormat(
                CalculateUtils.round(BigDecimal(quota.toString())
                        .multiply(BigDecimal(saleDepositPercent.toString()))
                        .divide(BigDecimal("100")).toDouble(),
                        2)
        )}（人民币）"
//        // 每次的改变量
//        unitChange = BigDecimal(baseAmount.toString())
//                .multiply(BigDecimal(increment.toString()))
//                .divide(BigDecimal("100"))
    }

    private fun calculatePromotion(frequency: Int) {

        /*
        增量百分比计算公式:         increment_percent%  =   increment * frequency
        推广指标计算公式:           sale_quota         =   quota * (1 + increment / 100 * frequency)
        推广保证金计算公式:         sale_deposit       =   sale_quota * (saleDepositPercent / 100)
        */
        val increment_percent = BigDecimal(increment.toString()).multiply(BigDecimal(frequency.toString()))
        promotionNum.text = "${CalculateUtils.round(increment_percent.toDouble(), 2)}%"

        val sale_quota = BigDecimal(quota.toString()).multiply(BigDecimal("1").add((increment_percent.divide(BigDecimal("100")))))
        promotionTotalCount.text = CalculateUtils.round(sale_quota.toDouble(), 2).toString()

        val sale_deposit = sale_quota.multiply((BigDecimal(saleDepositPercent.toString()).divide(BigDecimal("100"))))
        promotionTotalAmount.text = "${FormatUtils.MoneyFormat(
                CalculateUtils.round(sale_deposit.toInt().toDouble(), 2)
        )}（人民币）"

        LogUtil.d("数据变化：\nincrement_percent is $increment_percent%\nsale_quota is $sale_quota\nsale_deposit is $sale_deposit")


        // 废弃--2017年9月6日
//        // changes = baseAmount * increment% * frequency---废弃2017年9月6日
//        changes = unitChange.multiply(BigDecimal(frequency.toString()))
//        promotionNum.text = CalculateUtils.round(changes.toDouble(), 2).toString()
//        // finalCount = baseAmount + baseAmount * increment% * frequency
//        val finalCount = BigDecimal(baseAmount.toString())
//                .add(changes)
//        promotionTotalCount.text = CalculateUtils.round(finalCount.toDouble(), 2).toString()
//        // finalAmount = saleDeposit * (baseAmount + baseAmount * increment% * frequency)---废弃2017年9月6日
//        val finalAmount = BigDecimal(saleDeposit.toString()).multiply(finalCount)
//        promotionTotalAmount.text = "${FormatUtils.MoneyFormat(
//                CalculateUtils.round(finalAmount.toDouble(), 2)
//        )}（人民币）"
    }

    private fun enterBind() {
        when (userAccountType) {
            PERSONAL -> startActivity<BindPersonalActivity>()
            CORPORATE -> startActivity<BindCompanyActivity>()
            else -> ToastUtil.showShort(this, "error")
        }
    }



    override fun handlePersonal(personal: PersonalBean) {
        promotionTitle.setTitle("个人推广")
        promotionItem1.text = getString(R.string.resources_promotion_personal_item1)
        promotionItem2.text = getString(R.string.resources_promotion_personal_item2)
        promotionItem3.text = getString(R.string.resources_promotion_personal_item3)
        promotionOption1.hint = getString(R.string.resources_promotion_personal_option1)
        promotionOption2.hint = getString(R.string.resources_promotion_personal_option2)
        promotionOption3.hint = getString(R.string.resources_promotion_personal_option3)
        promotionOption1.text = personal.cnName
        promotionOption2.text = personal.idNumber
        promotionOption3.text = personal.personalPhoneNumber
    }

    override fun handleCorporate(corporate: CorporateBean) {
        promotionTitle.setTitle("公司推广")
        promotionItem1.text = getString(R.string.resources_promotion_corporate_item1)
        promotionItem2.text = getString(R.string.resources_promotion_corporate_item2)
        promotionItem3.text = getString(R.string.resources_promotion_corporate_item3)
        promotionOption1.hint = getString(R.string.resources_promotion_corporate_option1)
        promotionOption2.hint = getString(R.string.resources_promotion_corporate_option2)
        promotionOption3.hint = getString(R.string.resources_promotion_corporate_option3)
        promotionOption1.text = corporate.accountName
        promotionOption2.text = corporate.corporateAccount
        promotionOption3.text = corporate.cnName
    }

    private fun showConfirmDialog() {
        val builder = AlertDialog.Builder(this, R.style.dialog)
        val view = View.inflate(this, R.layout.dialog_submit_promotion, null)
        builder.setView(view)
        val dialog = builder.create()
        view.findViewById(R.id.dialog_submit).setOnClickListener {
            dialog.dismiss()
            startActivity<SubmitInsuranceActivity>(
                    "userAccountType" to userAccountType ,
                    "mark" to "insurance",
                    "frequency" to frequency.toString(),
                    "insuranceInfo" to insuranceInfo)
        }
        dialog.show()
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }
}
