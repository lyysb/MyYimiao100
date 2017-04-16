package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.AddressAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Address;
import com.yimiao100.sale.bean.AddressBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 常用邮寄地址管理
 */
public class PersonalAddressActivity extends BaseActivity implements AddressAdapter
        .OnClickListener, SwipeRefreshLayout.OnRefreshListener, TitleView.TitleBarOnClickListener {


    @BindView(R.id.personal_address_title)
    TitleView mTitle;
    @BindView(R.id.personal_address_add)
    TextView mAddressAdd;
    @BindView(R.id.personal_address_list_view)
    ListView mListView;
    @BindView(R.id.personal_address_swipe)
    SwipeRefreshLayout mSwipe;
    private static final int REQUEST_ADD_NEW = 100;//添加收货地址
    private static final int REQUEST_EDIT = 200;//编辑收货地址
    private final int FROM_NEW_OK = 100;
    private final int FROM_ITEM_OK = 200;
    private final int FROM_ITEM_DELETE = 201;
    private final String URL_ADDRESS_LIST = Constant.BASE_URL + "/api/user/address_list";
    private final String URL_ADDRESS_DELETE = Constant.BASE_URL + "/api/user/delete_address";
    private final String URL_ADDRESS_SET_DEFAULT = Constant.BASE_URL + "/api/user/update_address_is_default";

    private AddressAdapter mAddressAdapter;
    private ArrayList<Address> mAddressList;
    private String mFrom;
    private int mAddressId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_address);
        ButterKnife.bind(this);

        mFrom = getIntent().getStringExtra("from");
        mAddressId = getIntent().getIntExtra("addressId", -1);
        LogUtil.Companion.d("from ：" + mFrom);
        LogUtil.Companion.d("返回的地址id：" + mAddressId);

        showLoadingProgress();

        initView();

        initData();
    }

    private void initView() {
        mTitle.setOnTitleBarClick(this);
        initRefresh();
    }

    private void initRefresh() {
        //设置刷新监听
        mSwipe.setOnRefreshListener(this);
        //设置下拉圆圈的颜色
        mSwipe.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color
                        .holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipe.setDistanceToTriggerSync(400);
    }

    private void initData() {
        getBuild().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("常用地址列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
                hideLoadingProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("常用地址列表：" + response);
                hideLoadingProgress();
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                assert errorBean != null;
                switch (errorBean.getStatus()) {
                    case "success":
                        mAddressList = JSON.parseObject(response, AddressBean
                                .class).getAddressList();
                        mAddressAdapter = new AddressAdapter(mAddressList);
                        mAddressAdapter.setOnClickListener(PersonalAddressActivity.this);
                        mListView.setAdapter(mAddressAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });

    }

    private RequestCall getBuild() {
        return OkHttpUtils.post().url(URL_ADDRESS_LIST).addHeader(ACCESS_TOKEN, mAccessToken).build();
    }


    /**
     * 编辑
     *
     * @param position
     */
    @Override
    public void editAddress(int position) {
        LogUtil.Companion.d("editAddress position is " + position);
        Address address = mAddressList.get(position);

        if (TextUtils.equals(mFrom, "integral")) {
            //来自积分商城
            Intent intent = new Intent();
            //携带选中位置返回
            intent.putExtra("position", position);
            setResult(FROM_ITEM_OK, intent);
            finish();
        } else if (TextUtils.equals(mFrom, "authorization")) {
            // 来自申请授权书
            Intent intent = new Intent();
            intent.putExtra("address", address);
            setResult(FROM_ITEM_OK, intent);
            finish();
        } else {
            //不是来自积分商城
            Intent intent = new Intent(this, PersonalAddressAddActivity.class);
            intent.putExtra("address", address);
            startActivityForResult(intent, REQUEST_EDIT);
        }
    }

    /**
     * 删除
     *
     * @param position
     */
    @Override
    public void deleteAddress(final int position) {
        OkHttpUtils.post().url(URL_ADDRESS_DELETE)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams("addressId", mAddressList.get(position).getId() + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("删除地址E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("删除地址：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //如果是从确定订单进入的界面，执行了删除操作
                        LogUtil.Companion.d("被删除的地址ID：" + mAddressList.get(position).getId());
                        if (TextUtils.equals(mFrom, "integral")) {
                            //-比对删除的地址id和上个界面传进来的id
                            if (mAddressList.get(position).getId() == mAddressId) {
                                //-一样，说明该地址被删除，setResult
                                setResult(FROM_ITEM_DELETE);
                            }
                        }
                        mAddressList.remove(position);
                        mAddressAdapter.notifyDataSetChanged();
                        ToastUtil.showShort(getApplicationContext(), "删除成功");
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 设为默认
     *
     * @param position
     */
    @Override
    public void setDefault(final int position) {
        OkHttpUtils.post().url(URL_ADDRESS_SET_DEFAULT)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams("addressId", mAddressList.get(position).getId() + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("设置地址为默认地址E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("设置地址为默认地址：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        for (int i = 0; i < mAddressList.size(); i++) {
                            Address address = mAddressList.get(i);
                            if (i == position) {
                                address.setDefault(1);
                            } else {
                                address.setDefault(0);
                            }
                        }
                        mAddressAdapter.notifyDataSetChanged();
                        ToastUtil.showShort(getApplicationContext(), "设置成功");
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @OnClick(R.id.personal_address_add)
    public void onClick() {
        startActivityForResult(new Intent(this, PersonalAddressAddActivity.class), REQUEST_ADD_NEW);
    }

    @Override
    public void onRefresh() {
        getBuild().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("常用地址列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("常用地址列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mSwipe.setRefreshing(false);
                        mAddressList = JSON.parseObject(response, AddressBean
                                .class).getAddressList();
                        mAddressAdapter = new AddressAdapter(mAddressList);
                        mAddressAdapter.setOnClickListener(PersonalAddressActivity.this);
                        mListView.setAdapter(mAddressAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || resultCode == FROM_NEW_OK) {
            LogUtil.Companion.d("OK");
            //重新刷新数据
            onRefresh();
        }
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void rightOnClick() {

    }
}
