package com.yimiao100.sale.api;

import com.yimiao100.sale.bean.PayResult;
import com.yimiao100.sale.bean.ResourceBean;
import com.yimiao100.sale.bean.ResourceDetailBean;
import com.yimiao100.sale.bean.SignUpBean;
import com.yimiao100.sale.bean.VendorFilter;


import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * 所有的接口请求都在这
 * Created by Michel on 2017/12/13.
 */

public interface ApiService {

    /**
     * 账号登录
     *
     * @param account
     * @param pwd
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/login")
    Observable<SignUpBean> login(@Field("accountNumber") String account,
                                 @Field("password") String pwd);

    /**
     * 账号注册
     *
     * @param account
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/signup")
    Observable<SignUpBean> register(@Field("accountNumber") String account,
                                    @Field("password") String password);

    /**
     * 请求资源列表
     */
    @FormUrlEncoded
    @POST("api/resource/resource_list")
    Observable<ResourceBean> requestVaccineList(
            @Header("X-Authorization-Token") String accessToken,
            @FieldMap HashMap<String, String> searchIds,
            @Field("page") int page,
            @Field("pageSize") String pageSize);

    /**
     * @return 厂家和分类列表
     */
    @POST("api/vendor/vendor_with_categories")
    Observable<ArrayList<VendorFilter>> requestVaccineFilter(
            @Header("X-Authorization-Token") String accessToken);

    /**
     * 获取资源详情-暂只用来获取推广协议
     */
    @FormUrlEncoded
    @POST("api/resource/resource_info")
    Observable<ResourceDetailBean> requestVaccineDetail(
            @Header("X-Authorization-Token") String accessToken,
            @Field("resourceId") int resourceId
    );

    /**
     * 请求支付
     */
    @FormUrlEncoded
    @POST("api/order/batch_place_order")
    Observable<PayResult> requestPay(
            @Header("X-Authorization-Token") String accessToken,
            @Field("bizData") String bizData
            );

}
