package com.yimiao100.sale.mvpbase;

import com.blankj.utilcode.util.SPUtils;
import com.yimiao100.sale.utils.Constant;

/**
 * Created by Michel on 2018/3/6.
 */

public class BaseModel {

    protected String accessToken;

    public BaseModel() {
        accessToken = SPUtils.getInstance().getString(Constant.ACCESSTOKEN);
    }
}