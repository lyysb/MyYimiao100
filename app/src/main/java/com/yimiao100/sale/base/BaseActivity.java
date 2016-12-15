package com.yimiao100.sale.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.SharePreferenceUtil;

public class BaseActivity extends AppCompatActivity {

    protected static Activity currentContext;
    private BaseActivity mBaseActivity;


    protected final String ACCESS_TOKEN = "X-Authorization-Token";
    protected final String PAGE = "page";
    protected final String PAGE_SIZE = "pageSize";

    protected String mAccessToken;
    protected int mPage;
    protected int mTotalPage;
    protected final String mPageSize = "10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity = this;
        BaseActivity.setActivityState(this);

        mAccessToken = (String) SharePreferenceUtil.get(this, Constant.ACCESSTOKEN, "");
    }

    public static void setActivityState(Activity activity) {
        currentContext = activity;
        // 设置App只能竖屏显示
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 向自身维护的Activity栈（数据类型为List）中添加Activity
        ActivityCollector.addActivity(activity);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将Activity从列表中移除
        ActivityCollector.removeActivity(this);
    }

}
