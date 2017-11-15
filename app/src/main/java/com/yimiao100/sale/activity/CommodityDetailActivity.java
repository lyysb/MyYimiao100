package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Goods;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.view.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 商品详情
 */
public class CommodityDetailActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.commodity_detail_detail)
    WebView mCommodityDetailDetail;
    @BindView(R.id.commodity_detail_title)
    TitleView mCommodityDetailTitle;
    @BindView(R.id.commodity_detail_integral_value2)
    TextView mCommodityDetailIntegralValue2;
    @BindView(R.id.commodity_detail_image)
    ImageView mCommodityDetailImage;
    @BindView(R.id.commodity_detail_goods_name)
    TextView mCommodityDetailGoodsName;
    @BindView(R.id.commodity_detail_integral_value)
    TextView mCommodityDetailIntegralValue;
    @BindView(R.id.commodity_detail_unit_price)
    TextView mCommodityDetailUnitPrice;

    private final String URL_GOODS_DETAIL = Constant.BASE_URL + "/api/mall/goods_content?goodsId=";
    private Goods mGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_detail);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mCommodityDetailTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        mGoods = getIntent().getParcelableExtra("goods");

        //产品图片
        Picasso.with(this).load(mGoods.getImageUrl()).into(mCommodityDetailImage);
        //产品名称
        mCommodityDetailGoodsName.setText(mGoods.getGoodsName());
        //产品积分
        mCommodityDetailIntegralValue.setText(mGoods.getIntegralValue() + "");
        mCommodityDetailIntegralValue2.setText(mGoods.getIntegralValue() + "");
        //市场参考价格
        mCommodityDetailUnitPrice.setText("市场参考价格：" + FormatUtils.RMBFormat(mGoods.getUnitPrice()));


        initWeb();
    }
    private void initWeb() {
        WebSettings settings = mCommodityDetailDetail.getSettings();
        settings.setJavaScriptEnabled(true);

        //自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        mCommodityDetailDetail.loadUrl(URL_GOODS_DETAIL + mGoods.getId());
    }

    @OnClick(R.id.commodity_detail_exchange)
    public void onClick() {
        Intent intent = new Intent(this, CommodityConfirmActivity.class);
        intent.putExtra("goods", mGoods);
        startActivity(intent);
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
