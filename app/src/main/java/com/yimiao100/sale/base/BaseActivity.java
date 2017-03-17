package com.yimiao100.sale.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ProgressDialogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * 这个公司没有年终奖的,兄弟别指望了,也别来了,我准备辞职了
 * 另外这个项目有很多*Bug* 你坚持不了多久的,拜拜!
 */
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
    protected ProgressDialog mLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity = this;
        BaseActivity.setActivityState(this);

        mAccessToken = (String) SharePreferenceUtil.get(this, Constant.ACCESSTOKEN, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCollector.setTopActivity(this);
        if (JPushInterface.isPushStopped(this)) {
            LogUtil.Companion.d("极光推送服务被停止，重启中");
            //如果极光服务被停止，则恢复服务
            JPushInterface.resumePush(this);
        }
    }

    public static void setActivityState(Activity activity) {
        currentContext = activity;
        // 设置App只能竖屏显示
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 向自身维护的Activity栈（数据类型为List）中添加Activity
        ActivityCollector.addActivity(activity);
    }

    protected void showLoadingProgress() {
        mLoadingProgress = ProgressDialogUtil.getLoadingProgress(this);
        mLoadingProgress.show();
    }

    protected void hideLoadingProgress() {
        if (mLoadingProgress.isShowing()) {
            mLoadingProgress.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将Activity从列表中移除
        ActivityCollector.removeActivity(this);
    }

}
