package com.yimiao100.sale.insurance

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import com.yimiao100.sale.R
import com.yimiao100.sale.activity.AssuranceCompanyActivity
import com.yimiao100.sale.adapter.listview.AssInsAdapter
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.*
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.PullToRefreshListView
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.Call
import org.jetbrains.anko.find
import java.lang.Exception
import java.math.BigDecimal
import java.util.HashMap

class AssInsActivity : BaseActivity(), TitleView.TitleBarOnClickListener, SwipeRefreshLayout.OnRefreshListener, PullToRefreshListView.OnRefreshingListener, AssInsAdapter.OnCheckedChangeListener {

    lateinit var companyId: String
    lateinit var companyName: String
    lateinit var userAccountType: String
    lateinit var logoImageUrl: String
    lateinit var list: ArrayList<AssInsList>
    lateinit var adapter: AssInsAdapter

    lateinit var mEmptyView: View
    lateinit var mRefreshLayout: SwipeRefreshLayout
    lateinit var mListView: PullToRefreshListView
    lateinit var mLogoImage: ImageView
    lateinit var mAssuranceCheckCount: TextView
    lateinit var mAssuranceAccount: TextView
    lateinit var selectAllView: CheckBox

    private val URL_ASSURANCE = "${Constant.BASE_URL}/api/insure/deposit_withdraw_order_list"
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
        setContentView(R.layout.activity_ass_ins)

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
        find<TitleView>(R.id.ass_ins_title).setOnTitleBarClick(this)

        mRefreshLayout = find(R.id.ass_ins_refresh)
        mListView = find(R.id.ass_ins_company_list_view)

        initEmptyView()

        initRefreshView()

        initListView()

        selectAllView = find(R.id.ass_ins_select_all)
        selectAllView.setOnClickListener {
            selectAllOrder()
        }


        find<Button>(R.id.ass_ins_confirm).setOnClickListener {
            // 提交
            assuranceConfirm()
        }
        mAssuranceCheckCount = find(R.id.ass_ins_check_count)
        mAssuranceAccount = find(R.id.ass_ins_account)
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
            val totalDepositWithdraw = it.orderSaleDeposit
            if (selectAllView.isChecked) {
                // 如果全选，
                mApplyNum = BigDecimal(mApplyNum.toString())
                        .add(BigDecimal(totalDepositWithdraw.toString()))
                        .toDouble()
                checkedIDs.put(index, id)
            } else {
                checkedIDs.remove(index)
            }
            LogUtil.d("index is $index")
        }
        mAssuranceCheckCount.text = "已选择：$mCheckedCount"
        mAssuranceAccount.text = "金额：$mApplyNum 元"
        adapter.notifyDataSetChanged()
    }

    private fun initEmptyView() {
        mEmptyView = find(R.id.ass_ins_empty)
        val emptyText = mEmptyView.find<TextView>(R.id.empty_text)
        emptyText.text = getString(R.string.empty_view_assurance)
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.mipmap.ico_bond_factory_list_detailed), null, null)

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
        Picasso.with(this).load(logoImageUrl).placeholder(R.mipmap.ico_default_short_picture)
                .resize(DensityUtil.dp2px(this, 80f), DensityUtil.dp2px(this, 80f)).into(mLogoImage)


        mListView.addHeaderView(headView, null, false)
        mListView.setSwipeRefreshLayoutEnabled {
            enable ->  mRefreshLayout.isEnabled = enable
        }
        //上拉加载监听
        mListView.setOnRefreshingListener(this)
    }

    private fun initData() {
        onRefresh()
    }

    override fun onRefresh() {
        mCheckedCount = 0
        mApplyNum = 0.0
        mAssuranceCheckCount.text = "已选择：$mCheckedCount"
        mAssuranceAccount.text = "金额：$mApplyNum 元"

        getBuild(1).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                hideLoadingProgress()
                mRefreshLayout.isRefreshing = false
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, AssInsJson::class.java)?.let {
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
                            adapter = AssInsAdapter(list)
                            adapter.setOnCheckedChangeListener(this@AssInsActivity)
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
        val totalDepositWithdraw = temp.orderSaleDeposit
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
        mAssuranceCheckCount.text = "已选择：$mCheckedCount"
        mAssuranceAccount.text = "金额：$mApplyNum 元"

        selectAllView.isChecked = mCheckedCount == list.size
    }

    /**
     * 进入到提现申请确认界面
     */
    private fun assuranceConfirm() {
        //遍历Map，拼接订单id
        if (checkedIDs.size != 0) {
            val orderIds = StringBuilder("")
            val entrySet = checkedIDs.entries
            entrySet.map { it.value }
                    .forEach {
                        orderIds.append(",").append(it)
                    }
            //删除第一个逗号
            orderIds.delete(0, 1)
            val intent = Intent(this, AssInsCorActivity::class.java)
            intent.putExtra(USER_ACCOUNT_TYPE, userAccountType)
            intent.putExtra("orderIds", orderIds.toString())
            intent.putExtra("applyNum", mApplyNum)
            startActivity(intent)
        } else {
            ToastUtil.showShort(applicationContext, "请选择订单")
        }
    }

    override fun onLoadMore() {
        if (page <= totalPage) {
            getBuild(page).execute(object : StringCallback() {
                override fun onError(call: Call, e: Exception, id: Int) {
                    Util.showTimeOutNotice(currentContext)
                }

                override fun onResponse(response: String, id: Int) {
                    LogUtil.d("保证金可提现-对公账户：\n$response")
                    val errorBean = JSON.parseObject(response, ErrorBean::class.java)
                    mListView.onLoadMoreComplete()
                    when (errorBean!!.status) {
                        "success" -> {
                            page++
                            list.addAll(JSON.parseObject(response, AssInsJson::class.java)!!.pagedResult.pagedList)
                            adapter.notifyDataSetChanged()
                        }
                        "failure" -> Util.showError(currentContext, errorBean.reason)
                    }
                }
            })
        } else {
            mListView.noMore()
        }
    }

    private fun getBuild(page: Int): RequestCall {
        return OkHttpUtils.post().url(URL_ASSURANCE).addHeader(ACCESS_TOKEN, accessToken)
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
