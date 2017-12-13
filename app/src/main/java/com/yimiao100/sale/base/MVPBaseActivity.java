package com.yimiao100.sale.base;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.yimiao100.sale.utils.ProgressDialogUtil;
import com.yimiao100.sale.utils.Util;

public abstract class MVPBaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView {

    protected T mPresenter;
    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
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
        mProgressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showErrorInfo(int reason) {
        Util.showError(this, reason);
    }

    @Override
    public void timeOut(Exception e) {
        e.printStackTrace();
        Util.showTimeOutNotice(this);
    }
}
