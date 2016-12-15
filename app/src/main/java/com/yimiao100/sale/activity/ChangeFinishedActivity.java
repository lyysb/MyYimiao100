package com.yimiao100.sale.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 修改完成
 */
public class ChangeFinishedActivity extends BaseActivity {

    @BindView(R.id.change_finished)
    ImageView mChangeFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_finished);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.change_finished)
    public void onClick() {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
