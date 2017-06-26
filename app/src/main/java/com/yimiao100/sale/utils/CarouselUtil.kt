package com.yimiao100.sale.utils

import android.app.Activity
import com.yimiao100.sale.bean.Carousel
import com.yimiao100.sale.bean.CarouselBean
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.ext.JSON
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import java.util.*

/**
 * 轮播图
 * Created by 亿苗通 on 2016/11/18.
 */

class CarouselUtil private constructor() {
    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    interface HandleCarouselListener {
        fun handleCarouselList(carouselList: ArrayList<Carousel>)
    }

    companion object {
        private val URL_CAROUSEL_LIST = Constant.BASE_URL + "/api/carousel/list"
        private val CAROUSEL_TYPE = "carouselType"


        /**
         * 获取轮播图列表
         * @param carouselType
         */
        @JvmStatic
        fun getCarouselList(activity: Activity, carouselType: String, listener: HandleCarouselListener?) {
            OkHttpUtils.post().url(URL_CAROUSEL_LIST).addParams(CAROUSEL_TYPE, carouselType)
                    .build().execute(object : StringCallback() {

                override fun onError(call: Call, e: Exception, id: Int) {
                    e.printStackTrace()
                    LogUtil.d("获取轮播图E：" + e.toString())
                }

                override fun onResponse(response: String, id: Int) {
                    LogUtil.d("获取轮播图：" + response)
                    val errorBean = JSON.parseObject(response, ErrorBean::class.java)
                    when (errorBean?.status) {
                        "success" -> {
                            val carouselList = JSON.parseObject(response, CarouselBean::class.java)?.carouselList
                            if (listener != null && carouselList != null) {

                                if (carouselList.size == 0) {
                                    //没有数据，设置假数据
                                    val carousel = Carousel()
                                    carousel.mediaUrl = "http://oduhua0b1.bkt.clouddn.com/banner_placeholder.png"
                                    carousel.objectTitle = "测试图片"
                                    carouselList.add(carousel)
                                }
                                listener.handleCarouselList(carouselList)
                            }
                        }
                        "failure" -> Util.showError(activity, errorBean.reason)
                    }
                }
            })
        }
    }
}
