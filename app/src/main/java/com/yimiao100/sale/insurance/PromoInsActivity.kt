package com.yimiao100.sale.insurance

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import com.yimiao100.sale.R
import com.yimiao100.sale.adapter.listview.PromoInsAdapter
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.*
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.PullToRefreshListView
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import okhttp3.Call
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import java.lang.Exception
import java.math.BigDecimal
import java.util.HashMap

class PromoInsActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, TitleView.TitleBarOnClickListener, PullToRefreshListView.OnRefreshingListener, PromoInsAdapter.OnCheckedChangeListener {

    lateinit var companyId: String
    lateinit var companyName: String
    lateinit var userAccountType: String
    lateinit var logoImageUrl: String
    lateinit var list: ArrayList<PromoIns>
    lateinit var adapter: PromoInsAdapter

    lateinit var mEmptyView: View
    lateinit var mRefreshLayout: SwipeRefreshLayout
    lateinit var mListView: PullToRefreshListView
    lateinit var mLogoImage: ImageView
    lateinit var mCheckCount: TextView
    lateinit var mProAccount: TextView
    lateinit var selectAllView: CheckBox

    private val URL_PROMOTION = "${Constant.BASE_URL}/api/insure/sale_withdraw_order_list"
    private val COMPANY_ID = "companyId"
    private val USER_ACCOUNT_TYPE = "userAccountType"

    //计数
    private var mCheckedCount = 0
    //统计申请提现金额
    private var mApplyNum = 0.0
    //统计选中ID
    private val checkedIDs = HashMap<Int, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo_ins)

        showLoadingProgress()

        initVariate()

        initView()

        initData()
    }

    private fun initVariate() {
        companyId = intent.getIntExtra("companyId", -1).toString()
        companyName = intent.getStringExtra("companyName")
        userAccountType = intent.getStringExtra("userAccountType")
        logoImageUrl = intent.getStringExtra("logoImageUrl")
    }

    private fun initView() {
        find<TitleView>(R.id.promo_ins_rich_title).setOnTitleBarClick(this)

        mRefreshLayout = find(R.id.promo_ins_rich_refresh)
        mListView = find(R.id.promo_ins_rich_company_list_view)

        initEmptyView()

        initRefreshView()

        initListView()

        selectAllView = find(R.id.promo_ins_select_all)
        selectAllView.setOnClickListener {
            selectAllOrder()
        }

        find<Button>(R.id.promo_ins_rich_confirm).setOnClickListener {
            // 进入推广费提现确认界面
            enterPromotionCashConfirm()
        }
        mCheckCount = find(R.id.promo_ins_check_count)
        mProAccount = find(R.id.promo_ins_account)
    }

    private fun selectAllOrder() {
        if (selectAllView.isChecked) {
            mCheckedCount = list.size
            mApplyNum = 0.0
        } else {
            mCheckedCount = 0
            mApplyNum = 0.0
        }
        list.forEachIndexed { index, it ->
            it.isChecked = selectAllView.isChecked
            //获得每个订单id
            val id = it.id
            val totalDepositWithdraw = it.saleWithdrawTotalAmount
            if (selectAllView.isChecked) {
                // 如果全选
                mApplyNum = BigDecimal(mApplyNum.toString())
                        .add(BigDecimal(totalDepositWithdraw.toString()))
                        .toDouble()
                checkedIDs.put(index, id)
            } else {
                checkedIDs.remove(index)
            }
            LogUtil.d("index is $index")
        }
        mCheckCount.text = "已选择：$mCheckedCount"
        mProAccount.text = "金额：$mApplyNum 元"
        adapter.notifyDataSetChanged()
    }

    private fun initEmptyView() {
        mEmptyView = find(R.id.promo_ins_empty_view)
        val emptyText = mEmptyView.find<TextView>(R.id.empty_text)
        emptyText.text = getString(R.string.empty_view_promotion)
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.mipmap.ico_promotion_award_detailed), null, null)

        val layoutParams = RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        //HeaderView的高度
        layoutParams.setMargins(0, DensityUtil.dp2px(this, 85f), 0, 0)
        mEmptyView.layoutParams = layoutParams
    }

    private fun initRefreshView() {
        //设置刷新
        mRefreshLayout.setOnRefreshListener(this)
        //设置下拉圆圈的颜色
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R
                .color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light)
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mRefreshLayout.setDistanceToTriggerSync(400)
    }

    private fun initListView() {
        val headView = View.inflate(this, R.layout.head_vendor_ins, null)
        //厂家logo
        mLogoImage = headView.find<ImageView>(R.id.head_vendor_ins_logo)
        Picasso.with(this).load(logoImageUrl)
                .placeholder(R.mipmap.ico_default_short_picture)
                .into(mLogoImage)

        mListView.addHeaderView(headView, null, false)
        mListView.setOnItemClickListener { parent, view, position, id ->
            enterDetail(position)
        }
        mListView.setSwipeRefreshLayoutEnabled {
            enable ->  mRefreshLayout.isEnabled = enable
        }
        //上拉加载监听
        mListView.setOnRefreshingListener(this)
    }

    private fun enterDetail(position: Int) {
        val item = list[position - 1]
        val region = "${item.provinceName}${item.cityName}${item.areaName}"
        startActivity<PromoInsDetailActivity>(
                "orderId" to item.id.toString(),
                "logoImageUrl" to logoImageUrl,
                "companyName" to item.companyName,
                "productName" to item.productName,
                "region" to region
        )
    }

    private fun initData() {
        onRefresh()
    }

    override fun onRefresh() {
        mCheckedCount = 0
        mApplyNum = 0.0
        mCheckCount.text = "已选择：$mCheckedCount"
        mProAccount.text = "金额：$mApplyNum 元"

        getBuild(1).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                hideLoadingProgress()
                mRefreshLayout.isRefreshing = false
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, PromoInsJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page = 2
                            totalPage = it.pagedResult.totalPage
                            list = it.pagedResult.pagedList

                            if (list.size == 0) {
                                mEmptyView.visibility = View.VISIBLE
                            } else {
                                mEmptyView.visibility = View.GONE
                            }
                            adapter = PromoInsAdapter(list)
                            adapter.setOnCheckedChangeListener(this@PromoInsActivity)
                            mListView.adapter = adapter
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                hideLoadingProgress()
                e.printStackTrace()
                Util.showTimeOutNotice(currentContext)
            }

        })
    }

    override fun onCheckedChanged(position: Int, isChecked: Boolean) {
        LogUtil.d(list[position].toString())
        LogUtil.d("position is $position")
        val temp = list[position]
        //获得每个订单中的保证金
        val totalDepositWithdraw = temp.saleWithdrawTotalAmount
        //获得每个订单id
        val id = temp.id
        if (isChecked) {
            mCheckedCount++

            mApplyNum = BigDecimal(mApplyNum.toString())
                    .add(BigDecimal(totalDepositWithdraw.toString()))
                    .toDouble()
            checkedIDs.put(position, id)
        } else {
            mCheckedCount--

            mApplyNum = BigDecimal(mApplyNum.toString())
                    .subtract(BigDecimal(totalDepositWithdraw.toString()))
                    .toDouble()
            checkedIDs.remove(position)
        }
        mCheckCount.text = "已选择：$mCheckedCount"
        mProAccount.text = "金额：$mApplyNum 元"

        selectAllView.isChecked = mCheckedCount == list.size
    }


    override fun onLoadMore() {
        if (page <= totalPage) {
            getBuild(page).execute(object : StringCallback() {
                override fun onError(call: Call, e: Exception, id: Int) {
                    Util.showTimeOutNotice(currentContext)
                }

                override fun onResponse(response: String, id: Int) {
                    LogUtil.d("保证金可提现-对公账户：\n$response")
                    mListView.onLoadMoreComplete()
                    JSON.parseObject(response, PromoInsJson::class.java)?.let {
                        when (it.status) {
                            "success" -> {
                                page++
                                list.addAll(it.pagedResult.pagedList)
                                adapter.notifyDataSetChanged()
                            }
                            else -> Util.showError(currentContext, it.reason)
                        }
                    }

                }
            })
        } else {
            mListView.noMore()
        }
    }

    /**
     * 进入推广费提现确认界面
     */
    private fun enterPromotionCashConfirm() {
        //遍历Map，拼接订单id
        if (checkedIDs.size != 0) {
            val orderIds = StringBuffer("")
            val entrySet = checkedIDs.entries
            entrySet.map { it.value }
                    .forEach {
                        orderIds.append(",").append(it)
                    }
            //删除第一个逗号
            orderIds.delete(0, 1)
            val intent = Intent()
            var clz: Class<*>? = null
            when (userAccountType) {
                "personal" -> clz = PromoInsPerConfirmActivity::class.java
                "corporate" -> clz = PromoInsCorConfirmActivity::class.java
            }
            intent.setClass(this, clz)
            intent.putExtra("orderIds", orderIds.toString())
            intent.putExtra("amount", mApplyNum)

            LogUtil.d("orderIds is\n$orderIds")
            if (clz != null) {
                startActivity(intent)
            }
        } else {
            ToastUtil.showShort(currentContext, "请选择订单")
        }
    }

    private fun getBuild(page: Int): RequestCall {
        return OkHttpUtils.post().url(URL_PROMOTION).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(PAGE, page.toString()).addParams(PAGE_SIZE, pageSize)
                .addParams(COMPANY_ID, companyId)
                .addParams(USER_ACCOUNT_TYPE, userAccountType).build()
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }

}
