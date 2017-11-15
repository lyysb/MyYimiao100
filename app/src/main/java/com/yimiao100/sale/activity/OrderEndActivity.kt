package com.yimiao100.sale.activity

import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import com.meiqia.core.MQManager
import com.meiqia.core.bean.MQMessage
import com.meiqia.core.callback.OnGetMessageListCallback
import com.yimiao100.sale.R
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.Event
import com.yimiao100.sale.bean.EventType
import com.yimiao100.sale.bean.ResourceListBean
import com.yimiao100.sale.utils.FormatUtils
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.TimeUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.view.TitleView
import org.jetbrains.anko.find
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

class OrderEndActivity : BaseActivity(), TitleView.TitleBarOnClickListener {


    lateinit var order: ResourceListBean
    lateinit var service: ImageView
    lateinit var badge: Badge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_end)

        initVariate()

        initView()

        initData()
    }

    private fun initVariate() {
        order = intent.getParcelableExtra("order")
    }

    private fun initView() {
        find<TitleView>(R.id.order_end_title).setOnTitleBarClick(this)
        service = find(R.id.order_end_service)
        service.setOnClickListener {
            //联系客服
            Util.enterCustomerService(this)
        }
        badge = QBadgeView(this).bindTarget(service)
                .setBadgePadding(4f, true)
                .setGravityOffset(9f, true)
                .setShowShadow(false)
        MQManager.getInstance(this).getUnreadMessages(object : OnGetMessageListCallback {
            override fun onSuccess(list: List<MQMessage>) {
                if (list.isNotEmpty()) {
                    badge.badgeNumber = -1
                }
            }

            override fun onFailure(i: Int, s: String) {

            }
        })
    }

    private fun initData() {
        find<TextView>(R.id.order_end_vendor_name).text = order.vendorName
        find<TextView>(R.id.order_end_category_name).text = "产品：${order.categoryName}"
        find<TextView>(R.id.order_end_spec).text = "规格：${order.spec}"
        find<TextView>(R.id.order_end_dosage_form).text = "剂型：${order.dosageForm}"
        find<TextView>(R.id.order_end_region).text =
                "区域：${order.provinceName}\t${order.cityName}\t${order.areaName}"
        find<TextView>(R.id.order_end_time).text = "时间：${TimeUtil.timeStamp2Date(
                order.createdAt.toString(), "yyyy.MM.dd"
        )}"
        find<TextView>(R.id.order_end_total_money).text = Html.fromHtml(
                "推广保证金：<font color=\"#4188d2\">${FormatUtils.MoneyFormat(order.saleDeposit)}</font>(人民币)"
        )
        find<TextView>(R.id.order_end_customer).text = "客户：${order.customerName}"
        find<TextView>(R.id.order_end_serial_no).text = "协议单号：${order.serialNo}"
    }

    override fun onEventMainThread(event: Event) {
        super.onEventMainThread(event)
        when (Event.eventType) {
            EventType.RECEIVE_MSG ->
                // 收到客服消息，显示小圆点
                badge.badgeNumber = -1
            EventType.READ_MSG ->
                // 设置小圆点为0
                badge.badgeNumber = 0
            else -> LogUtil.d("unknown event type is " + Event.eventType)
        }
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }
}
