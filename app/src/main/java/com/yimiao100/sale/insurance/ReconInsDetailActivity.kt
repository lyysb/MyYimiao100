package com.yimiao100.sale.insurance

import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.adapter.listview.ReconInsDetailAdapter
import com.yimiao100.sale.base.BaseActivitySingleList
import com.yimiao100.sale.bean.*
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.FormatUtils
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import okhttp3.Call
import org.jetbrains.anko.find
import java.lang.Exception

class ReconInsDetailActivity : BaseActivitySingleList(), ReconInsDetailAdapter.onStatusClickListener {

    lateinit var customerId: String
    lateinit var orderId: String
    lateinit var list: ArrayList<ReconInsDetail>
    lateinit var adapter: ReconInsDetailAdapter

    lateinit var hCustomerName: TextView
    lateinit var hCount: TextView
    lateinit var hTotalAmount: TextView
    lateinit var hTotalReward: TextView

    val URL_ORDER_DETAIL = "${Constant.BASE_URL}/api/insure/balance_order_detail"
    val URL_CONFIRM = "${Constant.BASE_URL}/api/insure/confirm_policy"
    val CUSTOMER_ID = "customerId"
    val ORDER_ID = "orderId"

    override fun initVariate() {
        super.initVariate()
        customerId = intent.getStringExtra("customerId")
        orderId = intent.getStringExtra("orderId")
    }

    override fun initView() {
        super.initView()
        //添加头部布局
        val view = View.inflate(applicationContext, R.layout.head_recon_ins_detail, null)

        hCustomerName = view.find(R.id.head_recon_ins_customer_name)
        hCustomerName.text = intent.getStringExtra("customerName")
        hCount = view.find(R.id.head_recon_ins_count)
        hTotalAmount = view.find(R.id.head_recon_ins_total_amount)
        hTotalReward = view.find(R.id.head_recon_ins_total_reward)

        mEmptyView.visibility = View.GONE

        mListView.addHeaderView(view, null, false)
        mListView.setBackgroundResource(R.mipmap.ico_reconciliation_background)
    }

    override fun initData() {
        super.initData()
        showLoadingProgress()
    }

    override fun setTitle(titleView: TitleView) {
        titleView.setTitle("对账详情")
    }

    override fun onRefresh() {
        getBuild(1).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                hideLoadingProgress()
                mSwipeRefreshLayout.isRefreshing = false
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, ReconDetailJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            initHeaderData(it.stat)
                            page = 2
                            totalPage = it.pagedResult.totalPage
                            list = it.pagedResult.pagedList
                            adapter = ReconInsDetailAdapter(list)
                            adapter.setOnStatusClickListener(this@ReconInsDetailActivity)
                            mListView.adapter = adapter
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                mSwipeRefreshLayout.isRefreshing = false
                e.printStackTrace()
                hideLoadingProgress()
            }

        })
    }

    private fun initHeaderData(stat: InsuranceStat) {
        hCount.text = "保单数量：${stat.policyCounter}单"
        hTotalAmount.text = "保费总金额：${FormatUtils.MoneyFormat(stat.policyTotalAmount)}元"
        hTotalReward.text = "总奖励金额：${FormatUtils.MoneyFormat(stat.saleTotalAmount)}元"
    }


    override fun onConfirmClick(position: Int) {
        val builder = AlertDialog.Builder(this, R.style.dialog)
        val dialogView = View.inflate(this, R.layout.dialog_confirm_reconciliation, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        //有异议
        val disagree = dialogView.find<TextView>(R.id.dialog_disagree) 
        disagree.setOnClickListener {
            //进入客服
            Util.enterCustomerService(applicationContext)
            dialog.dismiss()
        }
        //同意
        val agree = dialogView.find<TextView>(R.id.dialog_agree)
        agree.setOnClickListener {
            confirmInsurance(position)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun confirmInsurance(position: Int) {
        val orderItemId = list[position].id
        OkHttpUtils.post().url(URL_CONFIRM).addHeader(ACCESS_TOKEN, accessToken)
                .addParams("orderItemId", orderItemId.toString())
                .build().execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, ErrorBean::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            list[position].confirmStatus = "confirmed"
                            list[position].confirmStatusName = "已确认"
                            adapter.notifyDataSetChanged()
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                e.printStackTrace()
                Util.showTimeOutNotice(currentContext)
            }

        })
    }


    private fun getBuild(page: Int): RequestCall {
        return OkHttpUtils.post().url(URL_ORDER_DETAIL).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(PAGE, page.toString()).addParams(PAGE_SIZE, pageSize)
                .addParams(ORDER_ID, orderId).addParams(CUSTOMER_ID, customerId)
                .build()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onLoadMore() {
        getBuild(page).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                mListView.onLoadMoreComplete()
                LogUtil.d("response is\n$response")
                JSON.parseObject(response, ReconDetailJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page++
                            list.addAll(it.pagedResult.pagedList)
                            adapter.notifyDataSetChanged()
                        }
                        "failure" -> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                mSwipeRefreshLayout.isRefreshing = false
                e.printStackTrace()
            }

        })

    }


}
