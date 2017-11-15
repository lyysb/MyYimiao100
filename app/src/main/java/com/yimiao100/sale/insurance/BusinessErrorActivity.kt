package com.yimiao100.sale.insurance

import android.os.Bundle
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
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.view.TitleView
import org.jetbrains.anko.find
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

class BusinessErrorActivity : BaseActivity(), TitleView.TitleBarOnClickListener {


    lateinit var order: Business
    lateinit var mService: ImageView
    lateinit var mBadge: Badge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_error)

        initVariate()

        initView()

        initData()
    }

    private fun initVariate() {
        order = intent.getParcelableExtra("order")
    }

    private fun initView() {
        find<TitleView>(R.id.business_error_title).setOnTitleBarClick(this)
        mService = find(R.id.business_error_service)
        mService.setOnClickListener {
            //联系客服
            Util.enterCustomerService(this)
        }
        find<ImageView>(R.id.business_error_read).setOnClickListener {
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
        find<TextView>(R.id.business_status_name).text = order.orderStatusName
        find<TextView>(R.id.business_error_reason).text = "\t\t${order.invalidReason}"
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
