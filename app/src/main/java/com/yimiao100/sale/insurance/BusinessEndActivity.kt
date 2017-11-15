package com.yimiao100.sale.insurance

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.meiqia.core.MQManager
import com.meiqia.core.bean.MQMessage
import com.meiqia.core.callback.OnGetMessageListCallback
import com.yimiao100.sale.R
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.Business
import com.yimiao100.sale.utils.FormatUtils
import com.yimiao100.sale.utils.TimeUtil
import com.yimiao100.sale.utils.ToastUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.view.TitleView
import org.jetbrains.anko.find
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

class BusinessEndActivity : BaseActivity(), TitleView.TitleBarOnClickListener {

    lateinit var mOrder: Business
    lateinit var mBadge: Badge
    lateinit var mService: ImageView
    lateinit var mProgressDownloadDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_end)

        initVariate()

        initView()

        initData()
    }

    private fun initVariate() {
        mOrder = intent.getParcelableExtra("order")
    }

    private fun initView() {
        find<TitleView>(R.id.business_end_title).setOnTitleBarClick(this)
        mService = find(R.id.business_end_service)
        mService.setOnClickListener {
            //联系客服
            Util.enterCustomerService(this)
        }
        mBadge = QBadgeView(this).bindTarget(mService)
                .setBadgePadding(4f, true)
                .setGravityOffset(9f, true)
                .setShowShadow(false)
        MQManager.getInstance(this).getUnreadMessages(object : OnGetMessageListCallback {
            override fun onSuccess(list: List<MQMessage>) {
                if (list.isNotEmpty()) {
                    mBadge.badgeNumber = -1
                }
            }

            override fun onFailure(i: Int, s: String) {

            }
        })
    }

    private fun initData() {
        val submit_time = TimeUtil.timeStamp2Date(mOrder.createdAt.toString(), "yyyy年MM月dd日")
//        find<TextView>(R.id.business_end_submit_time).text = submit_time + getString(R.string.order_end_notice)
        find<TextView>(R.id.business_end_company_name).text = mOrder.companyName
        find<TextView>(R.id.business_end_common_name).text = "保险名称：${mOrder.productName}"
        find<TextView>(R.id.business_end_region).text =
                "区域：${mOrder.provinceName}\t${mOrder.cityName}\t${mOrder.areaName}"
        find<TextView>(R.id.business_end_time).text = "时间：$submit_time"
        val totalDeposit = FormatUtils.MoneyFormat(mOrder.saleDeposit)
        val totalMoney = Html.fromHtml("推广保证金：<font color=\"#4188d2\">$totalDeposit</font>(人民币)")
        find<TextView>(R.id.business_end_total_money).text = totalMoney
        find<TextView>(R.id.business_end_serial_no).text = "协议单号：${mOrder.serialNo}"
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {

    }
}
