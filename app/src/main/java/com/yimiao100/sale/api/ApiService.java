package com.yimiao100.sale.api;

import com.yimiao100.sale.bean.SignUpBean;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 所有的接口请求都在这
 * Created by Michel on 2017/12/13.
 */

public interface ApiService {

    /**
     * 账号登录
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
     * @param account
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/signup")
    Observable<SignUpBean> register(@Field("accountNumber") String account,
                                    @Field("password") String password);

}
