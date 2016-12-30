package com.yimiao100.sale.utils;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.CarouselBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 轮播图
 * Created by 亿苗通 on 2016/11/18.
 */

public class CarouselUtil {
    private static String URL_CAROUSEL_LIST = Constant.BASE_URL + "/api/carousel/list";
    private static String CAROUSEL_TYPE = "carouselType";
    private CarouselUtil() {
         /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取轮播图列表
     * @param carouselType
     */
    public static void getCarouselList(final Activity activity, String carouselType, final HandleCarouselListener listener) {
        OkHttpUtils.post().url(URL_CAROUSEL_LIST).addParams(CAROUSEL_TYPE, carouselType).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                LogUtil.Companion.d("获取轮播图E：" + e.toString() );
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("获取轮播图：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ArrayList<Carousel> carouselList = JSON.parseObject(response,
                                CarouselBean.class).getCarouselList();
                        if (listener != null && carouselList != null) {

                            if (carouselList.size() == 0) {
                                //没有数据，设置假数据
                                Carousel carousel = new Carousel();
                                carousel.setMediaUrl("http://oduhua0b1.bkt.clouddn.com/banner_placeholder.png");
                                carouselList.add(carousel);
                            }
                            listener.handleCarouselList(carouselList);
                        }
                        break;
                    case "failure":
                        Util.showError(activity, errorBean.getReason());
                        break;
                }
            }
        });
    }

    public interface HandleCarouselListener {
        void handleCarouselList(ArrayList<Carousel> carouselList);
    }
}
