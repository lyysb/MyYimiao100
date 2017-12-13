package com.yimiao100.sale.insurance

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
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
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.bean.Event
import com.yimiao100.sale.bean.EventType
import com.yimiao100.sale.callback.ProtocolFileCallBack
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView
import java.io.File

class BusinessCompletedActivity : BaseActivity(), TitleView.TitleBarOnClickListener {


    lateinit var mOrder: Business
    lateinit var mBadge: Badge
    lateinit var mService: ImageView
    lateinit var mOrderId: String
    lateinit var mProgressDownloadDialog: ProgressDialog

    private val URL_DOWNLOAD_FILE = "${Constant.BASE_URL}/api/order/fetch_protocol"
    private val ORDER_ID = "orderId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_completed)

        initVariate()

        initView()

        initData()
    }

    private fun initVariate() {
        mOrder = intent.getParcelableExtra("order")
        mOrderId = mOrder.id.toString()
    }

    private fun initView() {
        find<TitleView>(R.id.business_complete_title).setOnTitleBarClick(this)
        mService = find(R.id.business_complete_service)
        mService.setOnClickListener {
            //联系客服
            Util.enterCustomerService(this)
        }
        find<TextView>(R.id.business_complete_into).setOnClickListener {
            ToastUtil.showShort(this, "施工中")
//            enterReconInsDetail()
        }
        find<ImageView>(R.id.business_complete_download).setOnClickListener {
            //下载电子协议
//            downloadAgreement()
            ToastUtil.showShort(this, "敬请期待")
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

    private fun enterReconInsDetail() {
        startActivity<ReconInsDetailActivity>(
                "orderId" to mOrderId,
                "bizId" to mOrder.companyId.toString(),
                "companyName" to mOrder.companyName,
                "productName" to mOrder.productName,
                "customerName" to mOrder.customerName,
                "serialNo" to mOrder.serialNo
        )
    }

    private fun initData() {
        val submit_time = TimeUtil.timeStamp2Date(mOrder.createdAt.toString(), "yyyy年MM月dd日")
        find<TextView>(R.id.business_complete_submit_time).text = submit_time + getString(R.string.order_complete_notice)
        find<TextView>(R.id.business_complete_company_name).text = mOrder.companyName
        find<TextView>(R.id.business_complete_common_name).text = "保险名称：${mOrder.productName}"
        find<TextView>(R.id.business_complete_region).text =
                "区域：${mOrder.provinceName}\t${mOrder.cityName}\t${mOrder.areaName}"
        find<TextView>(R.id.business_complete_time).text = "时间：$submit_time"
        val totalDeposit = FormatUtils.MoneyFormat(mOrder.saleDeposit)
        val totalMoney = Html.fromHtml("推广保证金：<font color=\"#4188d2\">$totalDeposit</font>(人民币)")
        find<TextView>(R.id.business_complete_total_money).text = totalMoney
        val customerView = find<TextView>(R.id.business_complete_customer)
        customerView.text = "客户：${mOrder.customerName}"
        customerView.visibility = if (mOrder.customerName == null) View.GONE else View.VISIBLE
        find<TextView>(R.id.business_complete_serial_no).text = "协议单号：${mOrder.serialNo}"
    }

    /**
     * 下载电子协议
     */
    private fun downloadAgreement() {
        mProgressDownloadDialog = ProgressDialog(this)
        mProgressDownloadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDownloadDialog.setTitle(getString(R.string.download_progress_dialog_title))
        mProgressDownloadDialog.setCancelable(false)
        val fileHead = "推广协议" + mOrder.productName
        OkHttpUtils.post().url(URL_DOWNLOAD_FILE).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(ORDER_ID, mOrderId).build().connTimeOut((1000 * 60 * 10).toLong())
                .readTimeOut((1000 * 60 * 10).toLong()).execute(object : ProtocolFileCallBack(fileHead) {
            override fun onBefore(request: Request?, id: Int) {
                super.onBefore(request, id)
                mProgressDownloadDialog.show()
            }

            override fun onAfter(id: Int) {
                super.onAfter(id)
                mProgressDownloadDialog.dismiss()
            }


            override fun onError(call: Call, e: Exception, id: Int) {
                Util.showTimeOutNotice(currentContext)
            }

            @Throws(Exception::class)
            override fun parseNetworkResponse(response: Response, id: Int): File? {
                if (200 == response.code()) {
                    //协议下载成功
                    LogUtil.d("下载成功")
                    return super.parseNetworkResponse(response, id)
                } else if (400 == response.code()) {
                    //解析显示错误信息
                    LogUtil.d("下载失败")
                    val errorBean = JSON.parseObject(response.body().toString(),
                            ErrorBean::class.java)
                    Util.showError(currentContext, errorBean!!.reason)
                    return null
                }
                return super.parseNetworkResponse(response, id)
            }

            override fun onResponse(response: File?, id: Int) {
                if (response == null) {
                    //如果返回来null证明下载错误
                    return
                }
                LogUtil.d("onResponse：" + response.absolutePath)
                LogUtil.d("response.name：" + response.name)
                //下载成功，显示分享或者发送出去对话框
                showSuccessDialog(response)
            }
        })
    }

    /**
     * 文件下载成功，选择文件处理方式

     * @param response
     */
    private fun showSuccessDialog(response: File) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.download_complete_dialog_title))
        builder.setMessage(getString(R.string.download_complete_dialog_msg))
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.download_complete_dialog_pb)) { dialog, which ->
            //打开邮箱
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra("subject", response.name) //
            intent.putExtra("body", "Email from CodePad") //正文
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(response))
            //添加附件，附件为file对象
            if (response.name.endsWith(".gz")) {
                intent.type = "application/x-gzip" //如果是gz使用gzip的mime
            } else if (response.name.endsWith(".txt")) {
                intent.type = "text/plain" //纯文本则用text/plain的mime
            } else {
                intent.type = "application/octet-stream" //其他的均使用流当做二进制数据来发送
            }
            startActivity(intent) //调用系统的mail客户端进行发送
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.download_complete_dialog_nb)) { dialog, which ->
            val intent = Intent("android.intent.action.VIEW")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri: Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                uri = FileProvider.getUriForFile(currentContext, "com.yimiao100.sale.fileprovider", response)
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                uri = Uri.fromFile(response)
            }
            intent.setDataAndType(uri, "application/msword")
            startActivity(intent)
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
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
