package com.yimiao100.sale.insurance

import android.os.Bundle
import android.text.Html
import android.widget.Button
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
import org.jetbrains.anko.startActivity

class BusinessUnpaidActivity : BaseActivity(), TitleView.TitleBarOnClickListener {


    lateinit var mTitle: TitleView
    lateinit var mService: ImageView
    lateinit var mTime: TextView
    lateinit var mCompanyName: TextView
    lateinit var mCommonName: TextView
    lateinit var mRegion: TextView
    lateinit var mOrderTime: TextView
    lateinit var mMoney: TextView
    lateinit var mNo: TextView
    lateinit var mHint: TextView
    lateinit var mExpiredAt: TextView

    lateinit var mOrder: Business
    lateinit var mBadge: Badge


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_unpaid)

        initVariate()

        initView()

        initData()
    }

    private fun initVariate() {
        mOrder = intent.getParcelableExtra("order")
    }

    private fun initView() {
        mTitle = find(R.id.business_unpaid_title)
        mTitle.setOnTitleBarClick(this)
        mService = find(R.id.business_unpaid_service)
        mService.setOnClickListener {
            //联系客服
            Util.enterCustomerService(this)
        }
        mTime = find(R.id.business_unpaid_submit_time)
        mCompanyName = find(R.id.business_unpaid_company_name)
        mCommonName = find(R.id.business_unpaid_common_name)
        mRegion = find(R.id.business_unpaid_region)
        mOrderTime = find(R.id.business_unpaid_time)
        mMoney = find(R.id.business_unpaid_money)
        mNo = find(R.id.business_unpaid_no)
        mHint = find(R.id.business_unpaid_hint)
        mExpiredAt = find(R.id.business_unpaid_expired_at)

        find<Button>(R.id.business_unpaid_submit).setOnClickListener {
            //进入支付界面
            startActivity<SubmitInsuranceActivity>(
                    "userAccountType" to mOrder.userAccountType,
                    "order" to mOrder,
                    "mark" to "order"
            )
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
        val submit_time = TimeUtil.timeStamp2Date(mOrder.createdAt.toString(), "yyyy年MM月dd日")
        mTime.text = submit_time + getString(R.string.order_unpaid_notice)
        //厂家名称
        mCompanyName.text = mOrder.companyName
        //产品名称-分类名
        mCommonName.text = "保险名称：${mOrder.productName}"
        //区域
        mRegion.text = "区域：${mOrder.provinceName}\t${mOrder.cityName}\t${mOrder.areaName}\t"
        //时间
        mOrderTime.text = "时间：${TimeUtil.timeStamp2Date(mOrder.createdAt.toString(), "yyyy.MM.dd")}"
        //保证金
        val totalDeposit = FormatUtils.MoneyFormat(mOrder.saleDeposit)
        val totalMoney = Html.fromHtml("推广保证金：<font color=\"#4188d2\">$totalDeposit</font> (人民币)")
        mMoney.text = totalMoney
        //协议单号
        mNo.text = "协议单号：${mOrder.serialNo}"
        //竞标保证金提示
        mHint.text = "本次推广资源的竞标保证金为${FormatUtils.MoneyFormat(mOrder.bidDeposit)}元，请于竞标截止日前尽快提交。"
        //竞标有效提示日期
        val bidExpiredTipAt = mOrder.bidExpiredTipAt
        val expire = TimeUtil.timeStamp2Date(bidExpiredTipAt.toString(), "yyyy年MM月dd日")
        mExpiredAt.text = Html.fromHtml("（<font color=\"#4188d2\">注意：</font>本资源竞标时间截止日为\t$expire ）")
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
