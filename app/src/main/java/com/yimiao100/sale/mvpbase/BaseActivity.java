package com.yimiao100.sale.mvpbase;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.blankj.utilcode.util.LogUtils;
import com.yimiao100.sale.utils.ProgressDialogUtil;
import com.yimiao100.sale.utils.Util;

public abstract class BaseActivity<V extends IBaseView<P>,P extends IBasePresenter<V>> extends AppCompatActivity implements IBaseView<P>, PresenterProxyInterface<V, P> {

    private static final String PRESENTER_SAVE_KEY = "presenter_save_key";
    protected ProgressDialog mProgressDialog;
    /**
     * 创建被代理对象,传入默认Presenter的工厂
     */
    private BaseProxy<V, P> proxy = new BaseProxy<>(PresenterFactoryImpl.<V, P>createFactory(getClass()));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (savedInstanceState != null) {
            proxy.onRestoreInstanceState(savedInstanceState.getBundle(PRESENTER_SAVE_KEY));
        }
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        proxy.onResume((V) this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proxy.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_SAVE_KEY, proxy.onSaveInstanceState());
    }

    @Override
    public void setPresenterFactory(PresenterFactory<V, P> presenterFactory) {
        proxy.setPresenterFactory(presenterFactory);
    }

    @Override
    public PresenterFactory<V, P> getPresenterFactory() {
        return proxy.getPresenterFactory();
    }

    @Override
    public P getPresenter() {
        return proxy.getPresenter();
    }

    protected abstract int getLayoutId();

    protected void init() {
        // initProgressDialog
        initProgressDialog();
    }

    private void initProgressDialog() {
        mProgressDialog = ProgressDialogUtil.getLoadingProgress(this);
    }

    @Override
    public void showProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showFailureInfo(int reason) {
        Util.showError(this, reason);
    }

    @Override
    public void onError(String errorMsg) {
        LogUtils.d(getClass().getSimpleName(), errorMsg);
        Util.showTimeOutNotice(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // 根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }
}
