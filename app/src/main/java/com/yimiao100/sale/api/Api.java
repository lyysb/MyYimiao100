package com.yimiao100.sale.api;

import com.yimiao100.sale.other.HttpLoggingInterceptor;
import com.yimiao100.sale.utils.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit封装
 * Created by Michel on 2017/12/14.
 */

public class Api {

    private final Retrofit retrofit;
    private final ApiService apiService;

    private static final int DEFAULT_TIMEOUT = 10;

    private Api() {
        // Log信息拦截器 -- 这里使用的自定义的拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 上线时取消拦截
                .addInterceptor(loggingInterceptor)
                // 错误重连--订阅前调用retryWhen方法
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        // BaseUrl必须以/结尾，更换完所有网络请求之后可以修改BaseUrl的值
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constant.BASE_URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static ApiService getInstance() {
        return ApiHolder.INSTANCE.apiService;
    }

    private static class ApiHolder{
        private static Api INSTANCE = new Api();
    }
}
