package com.yimiao100.sale.insurance

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.yimiao100.sale.R
import com.yimiao100.sale.adapter.listview.PromoInsDetailAdapter
import com.yimiao100.sale.adapter.listview.ReconInsDetailAdapter
import com.yimiao100.sale.base.BaseActivitySingleList
import com.yimiao100.sale.bean.ReconDetailJson
import com.yimiao100.sale.bean.ReconInsDetail
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.Constant
import com.yimiao100.sale.utils.DensityUtil
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.Util
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.Call
import org.jetbrains.anko.find
import java.lang.Exception

class PromoInsDetailActivity : BaseActivitySingleList() {

    lateinit var orderId: String
    lateinit var logoImageUrl: String
    lateinit var companyName: String
    lateinit var productName: String
    lateinit var region: String

    lateinit var list: ArrayList<ReconInsDetail>
    lateinit var adapter: PromoInsDetailAdapter

    private val URL_ORDER_DETAIL = "${Constant.BASE_URL}/api/insure/sale_withdraw_order_detail"
    private val ORDER_ID = "orderId"

    override fun initVariate() {
        super.initVariate()
        orderId = intent.getStringExtra("orderId")
        logoImageUrl = intent.getStringExtra("logoImageUrl")
        companyName = intent.getStringExtra("companyName")
        productName = intent.getStringExtra("productName")
        region = intent.getStringExtra("region")
    }

    override fun initView() {
        super.initView()
        val v = LayoutInflater.from(this).inflate(R.layout.head_promo_ins_detail, null)
        val logoView = v.find<CircleImageView>(R.id.promo_ins_detail_logo)
        Picasso.with(this).load(logoImageUrl)
                .resize(DensityUtil.dp2px(this, 78f), DensityUtil.dp2px(this, 78f)).into(logoView)

        v.find<TextView>(R.id.promo_ins_detail_company_name).text = companyName
        v.find<TextView>(R.id.promo_ins_detail_product_name).text = productName
        v.find<TextView>(R.id.promo_ins_detail_region).text = region

        mEmptyView.visibility = View.GONE

        mListView.addHeaderView(v)
        mListView.setBackgroundResource(R.mipmap.ico_reconciliation_background)
    }

    override fun initData() {
        super.initData()
        showLoadingProgress()
    }

    override fun onRefresh() {
        requestCall(1).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                hideLoadingProgress()
                LogUtil.d("response is\n$response\n混蛋！别忘了这里的Adapter")
                JSON.parseObject(response, ReconDetailJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page = 2
                            totalPage = it.pagedResult.totalPage
                            list = it.pagedResult.pagedList
                            adapter = PromoInsDetailAdapter(list)
                            mListView.adapter = adapter
                        }
                        else -> Util.showError(currentContext, it.reason)
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

    private fun requestCall(page: Int): RequestCall {
        return OkHttpUtils.post().url(URL_ORDER_DETAIL).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(PAGE, page.toString()).addParams(PAGE_SIZE, pageSize)
                .addParams(ORDER_ID, orderId)
                .build()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onLoadMore() {
        requestCall(page).execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                LogUtil.d("response is\n$response\n混蛋！别忘了这里的Adapter")
                JSON.parseObject(response, ReconDetailJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            page++
                            list.addAll(it.pagedResult.pagedList)
                            adapter.notifyDataSetChanged()
                        }
                        else -> Util.showError(currentContext, it.reason)
                    }
                }
                mListView.onLoadMoreComplete()
            }

            override fun onError(call: Call?, e: Exception, id: Int) {
                e.printStackTrace()
                Util.showTimeOutNotice(currentContext)
            }

        })
    }

    override fun setTitle(titleView: TitleView) {
        titleView.setTitle("提现详情")
    }


}
