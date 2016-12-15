package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Address;
import com.yimiao100.sale.bean.AddressBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Goods;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 确认订单
 */
public class CommodityConfirmActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.commodity_confirm_title)
    TitleView mCommodityConfirmTitle;
    @BindView(R.id.commodity_confirm_integral2)
    TextView mCommodityConfirmIntegral2;
    @BindView(R.id.commodity_confirm_name)
    TextView mCommodityConfirmName;
    @BindView(R.id.commodity_confirm_phone)
    TextView mCommodityConfirmPhone;
    @BindView(R.id.commodity_confirm_address)
    TextView mCommodityConfirmAddress;
    @BindView(R.id.commodity_confirm_image)
    ImageView mCommodityConfirmImage;
    @BindView(R.id.commodity_confirm_goods_name)
    TextView mCommodityConfirmGoodsName;
    @BindView(R.id.commodity_confirm_integral)
    TextView mCommodityConfirmIntegral;
    @BindView(R.id.commodity_confirm_unit_price)
    TextView mCommodityConfirmUnitPrice;

    private final String URL_ADDRESS_LIST = Constant.BASE_URL + "/api/user/address_list";
    private final int FROM_NEW_OK = 100;
    private final int FROM_NEW_CANCEL = 101;
    private final int FROM_ITEM_OK = 200;
    private final int FROM_ITEM_DELETE = 201;


    private final String URL_ORDER = Constant.BASE_URL + "/api/mall/exchange_goods";
    private final String ADDRESS_ID = "addressId";
    private final String GOODS_ID = "goodsId";


    private Goods mGoods;
    private ArrayList<Address> mAddressList;
    private int mAddressId = -1;            //默认值为-1
    private String mGoodsId;
    private AlertDialog mDialog;
    private int mPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_confirm);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mCommodityConfirmTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        initGoodsData();

        initAddressData();
    }

    /**
     * 初始化商品信息
     */
    private void initGoodsData() {
        mGoods = getIntent().getParcelableExtra("goods");
        //缩略图
        Picasso.with(this).load(mGoods.getImageUrl()).into(mCommodityConfirmImage);
        //商品名称
        mCommodityConfirmGoodsName.setText(mGoods.getGoodsName());
        //积分
        mCommodityConfirmIntegral.setText("积分：" + mGoods.getIntegralValue());
        mCommodityConfirmIntegral2.setText(mGoods.getIntegralValue() + "");
        //市场参考价
        mCommodityConfirmUnitPrice.setText("市场参考价格：￥" + FormatUtils.MoneyFormat(mGoods
                .getUnitPrice()));
        //商品ID
        mGoodsId = mGoods.getId() + "";
    }

    /**
     * 初始化地址数据
     */
    private void initAddressData() {

        OkHttpUtils.post().url(URL_ADDRESS_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("获取地址列表E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("获取地址列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mAddressList = JSON.parseObject(response, AddressBean.class)
                                .getAddresslist();
                        if (mAddressList == null || mAddressList.size() == 0) {
                            //没有数据，提示用户设置收货地址
                            showAddress("", "", "");
                            Util.showAddressDialog(CommodityConfirmActivity.this);
                        } else {
                            //有数据
                            Address address = mAddressList.get(mPosition);
                            mAddressId = address.getId();
                            showAddress(address.getCnName(), address.getPhoneNumber(),
                                    address.getProvinceName() + address.getCityName()
                                            + address.getAreaName() + address.getFullAddress());
                        }
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 显示收货地址
     *
     * @param name
     * @param phone
     * @param addressDetail
     */
    private void showAddress(String name, String phone, String addressDetail) {
        mCommodityConfirmName.setText("姓名：" + name);
        mCommodityConfirmPhone.setText("电话：" + phone);
        mCommodityConfirmAddress.setText(addressDetail);
    }

    @OnClick({R.id.commodity_confirm_exchange, R.id.commodity_confirm_select_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commodity_confirm_exchange:
                //提交兑换
                submitOrder();
                break;
            case R.id.commodity_confirm_select_address:
                //选择地址
                Intent intent = new Intent(this, PersonalAddressActivity.class);
                intent.putExtra("from", "integral");        //标识来自积分商城
                intent.putExtra("addressId", mAddressId);   //发给下一个界面，校验是否对该地址进行操作
                LogUtil.d("确定订单传给下一个界面的地址id：" + mAddressId);
                startActivityForResult(intent, FROM_ITEM_OK);
                break;
        }
    }

    /**
     * 提交积分兑换订单
     */
    private void submitOrder() {
        OkHttpUtils.post().url(URL_ORDER).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(ADDRESS_ID, mAddressId + "").addParams(GOODS_ID, mGoodsId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("提交积分兑换商品E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("提交积分兑换商品：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //本地同步扣除积分
                        int integral = (int) SharePreferenceUtil.get(getApplicationContext(),
                                Constant.INTEGRAL, -1);
                        integral -= mGoods.getIntegralValue();
                        SharePreferenceUtil.put(getApplicationContext(), Constant.INTEGRAL,
                                integral);
                        //显示提交订单成功
                        showSuccessDialog();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 显示提交订单成功Dialog
     */
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommodityConfirmActivity.this, R
                .style.dialog);
        builder.setCancelable(false);
        View dialogView = View.inflate(CommodityConfirmActivity.this, R.layout.dialog_order, null);
        dialogView.findViewById(R.id.dialog_order_continue).setOnClickListener(new View
                .OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
                startActivity(new Intent(getApplicationContext(), IntegralGoodsListActivity.class));
            }
        });
        builder.setView(dialogView);
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d("resultCode:" + resultCode);
        switch (resultCode) {
            case FROM_ITEM_OK:
                //打开地址列表返回本界面
                mPosition = data.getIntExtra("position", 0);
                initAddressData();
                break;
            case FROM_NEW_OK:
            case FROM_NEW_CANCEL:
            case FROM_ITEM_DELETE:
                //直接刷新即可
                initAddressData();
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
}
