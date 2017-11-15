package com.yimiao100.sale.insurance

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.meiqia.core.MQManager
import com.meiqia.core.bean.MQMessage
import com.meiqia.core.callback.OnGetMessageListCallback
import com.yimiao100.sale.R
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.Business
import com.yimiao100.sale.bean.Event
import com.yimiao100.sale.bean.EventType
import com.yimiao100.sale.utils.FormatUtils
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.TimeUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.view.TitleView
import org.jetbrains.anko.find
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

class BusinessSubmitActivity : BaseActivity(), TitleView.TitleBarOnClickListener {

    lateinit var order: Business
    lateinit var mService: ImageView
    lateinit var mBadge: Badge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_submit)

        initVariate()

        initView()

        initData()
    }

    private fun initVariate() {
        order = intent.getParcelableExtra("order")
    }

    private fun initView() {
        find<TitleView>(R.id.business_submit_title).setOnTitleBarClick(this)
        mService = find(R.id.business_submit_service)
        mService.setOnClickListener {
            //联系客服
            Util.enterCustomerService(this)
        }
        find<ImageButton>(R.id.business_submit_confirm).setOnClickListener {
            finish()
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
        //提交日期
        val submit_time = TimeUtil.timeStamp2Date(order.createdAt.toString(), "yyyy年MM月dd日")
        find<TextView>(R.id.business_submit_submit_time).text = submit_time + getString(R.string.order_submit_notice)
        find<TextView>(R.id.business_submit_company_name).text = order.companyName
        find<TextView>(R.id.business_submit_common_name).text = "保险名称：${order.productName}"
        find<TextView>(R.id.business_submit_region).text = "区域：${order.provinceName}\t${order.cityName}\t${order.areaName}"
        find<TextView>(R.id.business_submit_time).text = "时间：${TimeUtil.timeStamp2Date(order.createdAt.toString(), "yyyy.MM.dd")}"
        find<TextView>(R.id.business_submit_money).text =
                Html.fromHtml("推广保证金：<font color=\"#4188d2\">${FormatUtils.MoneyFormat(order.saleDeposit)}</font>(人民币)")
        val customerView = find<TextView>(R.id.business_submit_customer)
        customerView.text = "客户：${order.customerName}"
        customerView.visibility = if (order.customerName == null) View.GONE else View.VISIBLE
        find<TextView>(R.id.business_submit_no).text = "协议单号：${order.serialNo}"
    }

    override fun onEventMainThread(event: Event) {
        super.onEventMainThread(event)
        when (Event.eventType) {
            EventType.RECEIVE_MSG ->
                // 收到客服消息，显示小圆点
                mBadge.badgeNumber = -1
            EventType.READ_MSG ->
                // 设置小圆点为0
                mBadge.badgeNumber = 0
            else -> LogUtil.d("unknown event type is " + Event.eventType)
        }
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {

    }
}
