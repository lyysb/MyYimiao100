package com.yimiao100.sale.insurance

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import com.bigkoo.pickerview.OptionsPickerView
import com.bigkoo.pickerview.lib.WheelView
import com.squareup.picasso.Picasso
import com.yimiao100.sale.R
import com.yimiao100.sale.activity.ShowWebImageActivity
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.InsuranceDetailJson
import com.yimiao100.sale.bean.InsuranceInfo
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.view.TextViewExpandableAnimation
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import okhttp3.Call
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

class InsuranceDetailActivity : BaseActivity(), OptionsPickerView.OnOptionsSelectListener, TitleView.TitleBarOnClickListener {


    val URL_INSURANCE_INFO = "${Constant.BASE_URL}/api/insure/resource_info"

    var insuranceId: Int = -1
    var promotionTypeItems: ArrayList<String> = ArrayList()
    lateinit var promotionType: OptionsPickerView<Any>
    lateinit var insuranceInfo: InsuranceInfo
    lateinit var insuranceDetailImage: ImageView
    lateinit var imageUrl: String
    lateinit var insuranceDetailVideo: JCVideoPlayerStandard


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insurance_detail)

        showLoadingProgress()

        initVariate()

        initView()

        initData()

    }

    private fun initVariate() {
        insuranceId = intent.getIntExtra("insuranceId", -1)
    }

    private fun initView() {
        find<TitleView>(R.id.insurance_detail_title).setOnTitleBarClick(this)
        insuranceDetailImage = find(R.id.insurance_detail_image)
        insuranceDetailImage.setOnClickListener {
            startActivity<ShowWebImageActivity>("image" to imageUrl)
        }
        insuranceDetailVideo = find(R.id.insurance_detail_video)
        promotionType = OptionsPickerView.Builder(this, this)
                .setLayoutRes(R.layout.pickerview_custom_options) {
                    it.find<Button>(R.id.picker_view_confirm).setOnClickListener {
                        promotionType.returnData()
                        promotionType.dismiss()
                    }
                }
                .setContentTextSize(15)
                .setTextColorCenter(resources.getColor(R.color.colorMain))
                .setDividerType(WheelView.DividerType.WRAP)
                .setDividerColor(resources.getColor(R.color.colorMain))
                .build()

        find<ImageButton>(R.id.insurance_promotion).setOnClickListener {
            promotionType.setPicker(promotionTypeItems as List<Any>)
            promotionType.show(it)
        }
    }

    private fun initData() {
        promotionTypeItems.add("个人推广")
        promotionTypeItems.add("公司推广")
        OkHttpUtils.post().url(URL_INSURANCE_INFO).addHeader(ACCESS_TOKEN, accessToken)
                .addParams("resourceId", insuranceId.toString())
                .build().execute(object : StringCallback(){
            override fun onResponse(response: String, id: Int) {
                hideLoadingProgress()
                LogUtil.d("insuranceDetail is \n $response")
                JSON.parseObject(response, InsuranceDetailJson::class.java)?.let {
                    when (it.status) {
                        "success" -> {
                            showDetail(it.resourceInfo)
                        }
                        "failure"-> Util.showError(currentContext, it.reason)
                    }
                }
            }

            override fun onError(call: Call, e: Exception, id: Int) {
                LogUtil.d("insuranceInfo error")
                e.printStackTrace()
                hideLoadingProgress()
                Util.showTimeOutNotice(currentContext)
            }

        })
    }

    private fun showDetail(insuranceInfo: InsuranceInfo) {
        this.insuranceInfo = insuranceInfo
        find<TextView>(R.id.insurance_detail_company_name).text = insuranceInfo.companyName
        find<TextView>(R.id.insurance_detail_product_name).text = insuranceInfo.productName
        find<TextView>(R.id.insurance_detail_region).text =
                "${insuranceInfo.provinceName}\t${insuranceInfo.cityName}\t${insuranceInfo.areaName}"

        val startAt = TimeUtil.timeStamp2Date(insuranceInfo.startAt.toString(), "yyyy年MM月dd日")
        val endAt = TimeUtil.timeStamp2Date(insuranceInfo.endAt.toString(), "yyyy年MM月dd日")
        find<TextView>(R.id.insurance_detail_time).text = "$startAt - $endAt"
        find<TextView>(R.id.insurance_detail_quota).text = "${FormatUtils.MoneyFormat(insuranceInfo.quota)}元"
        find<TextView>(R.id.insurance_detail_scale).text = "${insuranceInfo.saleDepositPercent}%"

        find<TextViewExpandableAnimation>(R.id.insurance_detail_policy_content).setText(insuranceInfo.policyContent)
        val bidExpiredAt = TimeUtil.timeStamp2Date(insuranceInfo.bidExpiredAt.toString(), "yyyy年MM月dd日")
        find<TextView>(R.id.insurance_detail_expired_at).text = Html.fromHtml("（<font " +
                "color=\"#4188d2\">注意：</font>本保险竞标时间截止日为\t" + bidExpiredAt + "）")
        if (insuranceInfo.productVideoUrl != null) {
            // 显示视频，隐藏图片
            insuranceDetailVideo.visibility = View.VISIBLE
            insuranceDetailImage.visibility = View.GONE
            //播放视频
            insuranceDetailVideo.setUp(insuranceInfo.productVideoUrl, JCVideoPlayer.SCREEN_LAYOUT_LIST, "产品介绍")
            //设置预览图
            Picasso.with(this)
                    .load(insuranceInfo.productVideoUrl + "?vframe/png/offset/10")
                    .into(insuranceDetailVideo.thumbImageView)

        } else {
            // 显示图片，隐藏视频
            insuranceDetailVideo.visibility = View.GONE
            insuranceDetailImage.visibility = View.VISIBLE
            if (insuranceInfo.productImageUrl.isNotEmpty()) {
                imageUrl = insuranceInfo.productImageUrl
                Picasso.with(this).load(imageUrl)
                        .placeholder(R.mipmap.ico_default_bannner)
                        .into(insuranceDetailImage)
            }
        }


    }

    override fun onOptionsSelect(options1: Int, options2: Int, options3: Int, v: View) {
        when (v.id) {
            R.id.insurance_promotion -> {
                var userAccountType = ""
                when (options1) {
                    0 -> userAccountType = "personal"
                    1 -> userAccountType = "corporate"
                }
                startActivity<InsurancePromotionActivity>(
                        "insuranceInfo" to insuranceInfo,
                        "userAccountType" to userAccountType)
            }
            else -> ToastUtil.showShort(this, "error")
        }
    }

    override fun onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        JCVideoPlayer.releaseAllVideos()
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {
    }

}
