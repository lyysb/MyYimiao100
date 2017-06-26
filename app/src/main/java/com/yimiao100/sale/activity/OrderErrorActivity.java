package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.OnGetMessageListCallback;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Event;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 我的业务-订单所有失败状态界面
 */
public class OrderErrorActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.order_status_name)
    TextView mOrderStatusName;
    @BindView(R.id.order_error_service)
    ImageView mService;
    @BindView(R.id.order_error_reason)
    TextView mOrderErrorReason;
    @BindView(R.id.order_error_title)
    TitleView mOrderErrorTitle;
    private Badge mBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_error);
        ButterKnife.bind(this);

        initView();

        initData();

    }

    private void initView() {
        mOrderErrorTitle.setOnTitleBarClick(this);
        mBadge = new QBadgeView(this).bindTarget(mService)
                .setBadgePadding(4, true)
                .setGravityOffset(9, true)
                .setShowShadow(false);
        MQManager.getInstance(this).getUnreadMessages(new OnGetMessageListCallback() {
            @Override
            public void onSuccess(List<MQMessage> list) {
                if (list.size() != 0) {
                    mBadge.setBadgeNumber(-1);
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        ResourceListBean order = intent.getParcelableExtra("order");
        if (order != null) {
            String orderStatusName = order.getOrderStatusName();
            mOrderStatusName.setText(orderStatusName);
            String invalidReason = order.getInvalidReason();
            mOrderErrorReason.setText("\t\t" + invalidReason);
        }
    }

    @Override
    public void onEventMainThread(@NotNull Event event) {
        super.onEventMainThread(event);
        switch (Event.eventType) {
            case RECEIVE_MSG:
                // 收到客服消息，显示小圆点
                mBadge.setBadgeNumber(-1);
                break;
            case READ_MSG:
                // 设置小圆点为0
                mBadge.setBadgeNumber(0);
                break;
            default:
                LogUtil.d("unknown event type is " + Event.eventType);
                break;
        }
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }

    @OnClick({R.id.order_error_service, R.id.order_error_read})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_error_service:
                //联系客服
                Util.enterCustomerService(this);
                break;
            case R.id.order_error_read:
                finish();
                break;
        }
    }
}
