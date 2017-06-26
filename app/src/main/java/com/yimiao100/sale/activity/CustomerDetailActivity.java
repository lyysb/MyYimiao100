package com.yimiao100.sale.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.CDCListBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 客户详情页
 */
public class CustomerDetailActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.customer_detail_collection)
    ImageView mCustomerDetailCollection;
    @BindView(R.id.customer_detail_title)
    TitleView mCustomerDetailTitle;
    @BindView(R.id.customer_detail_map_toggle)
    ImageButton mCustomerDetailMapToggle;
    @BindView(R.id.customer_detail_opd)
    LinearLayout mCustomerDetailOpd;
    @BindView(R.id.customer_detail_service)
    FrameLayout mCustomerDetailService;
    @BindView(R.id.customer_detail_map)
    RelativeLayout mCustomerDetailMap;
    @BindView(R.id.customer_detail_baidu_map)
    MapView mCustomerDetailBaiduMap;
    @BindView(R.id.customer_detail_cdc_name)
    TextView mCustomerDetailCdcName;
    @BindView(R.id.customer_detail_card_account)
    TextView mCustomerDetailCardAccount;
    @BindView(R.id.customer_detail_use_account)
    TextView mCustomerDetailUseAccount;
    @BindView(R.id.customer_detail_address)
    TextView mCustomerDetailAddress;
    @BindView(R.id.customer_detail_cdc)
    TextView mCustomerDetailCdc;
    @BindView(R.id.customer_detail_phone)
    TextView mCustomerDetailPhone;
    @BindView(R.id.customer_detail_information)
    TextView mCustomerDetailInformation;

    private boolean mMapToggle = false;
    private BaiduMap mBaiduMap;
    private GeoCoder mGeoCoder;
    private String mCdcId;
    private int mUserAddStatus;

    private final int RESULT_CDC_COLLECTION = 111;
    private final String URL_ADD_USER_CDC = Constant.BASE_URL + "/api/cdc/add_user_cdc";
    private final String URL_CANCEL_USER_CDC = Constant.BASE_URL + "/api/cdc/cancel_user_cdc";
    private CDCListBean cdc;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化百度SDK
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_customer_detail);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mCustomerDetailTitle.setOnTitleBarClick(this);
        //获取普通地图
        mBaiduMap = mCustomerDetailBaiduMap.getMap();
    }

    private void initData() {
        Intent intent = getIntent();
        cdc = intent.getParcelableExtra("cdc");
        position = intent.getIntExtra("position", -1);
        //CDC名称
        String cdcName = cdc.getCdcName();
        mCustomerDetailCdcName.setText(cdcName);
        mCustomerDetailCdc.setText(cdcName);
        //新生儿建卡数
        int cardAmount = cdc.getCardAmount();
        //狂犬疫苗使用数量
        int useAmount = cdc.getUseAmount();
        //如果二者都是0，则证明没有数据，隐藏客户信息内容
        if (cardAmount == 0 && useAmount == 0) {
            mCustomerDetailInformation.setVisibility(View.GONE);
            mCustomerDetailCardAccount.setVisibility(View.GONE);
            mCustomerDetailUseAccount.setVisibility(View.GONE);
        } else {
            mCustomerDetailInformation.setVisibility(View.VISIBLE);
            mCustomerDetailCardAccount.setVisibility(View.VISIBLE);
            mCustomerDetailUseAccount.setVisibility(View.VISIBLE);
        }
        mCustomerDetailCardAccount.setText("新生儿建卡数：         " + cardAmount + " 张/年");
        mCustomerDetailUseAccount.setText("狂犬疫苗使用数量： " + useAmount + " 支/年");
        //地址
        String address = cdc.getAddress();
        mCustomerDetailAddress.setText(address);
        //电话
        String phoneNumber = cdc.getPhoneNumber();
        mCustomerDetailPhone.setText(phoneNumber);
        //和用户关系0-未收藏，1-已收藏
        mUserAddStatus = cdc.getUserAddStatus();
        LogUtil.d("mUserAddStatus-init：" + mUserAddStatus);
        if (mUserAddStatus == 1) {
            //表示是用户已经收藏的客户，更换图标显示
            mCustomerDetailCollection.setImageResource(R.mipmap.ico_customer_details_cancel_collection);
        } else {
            mCustomerDetailCollection.setImageResource(R.mipmap.ico_customer_details_collection);
        }
        //CDCId
        mCdcId = cdc.getId() + "";
        //初始化百度地图
        initBaiduMap();
    }

    /**
     * 初始化百度地图
     */
    private void initBaiduMap() {
        //-根据地址查询经纬度
        //创建地理编码检索实例
        mGeoCoder = GeoCoder.newInstance();
        //创建地理编码检索监听者
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    ToastUtil.showLong(getApplicationContext(), "暂时没有检索到结果");
                    return;
                }
                //获取地理编码结果
                LatLng location = geoCodeResult.getLocation();
                double latitude = location.latitude;    //纬度
                double longitude = location.longitude;  //经度
                LogUtil.d("纬度:" + latitude);
                LogUtil.d("经度:" + longitude);
                //设置标注物
                LatLng point = new LatLng(latitude, longitude);
                BitmapDescriptor bitmapDescriptor =
                        BitmapDescriptorFactory.fromResource(R.mipmap.ico_customer_coordinate);
                MarkerOptions icon = new MarkerOptions().position(point).icon(bitmapDescriptor);
                mBaiduMap.addOverlay(icon);
                //设置焦点以及缩放级别
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(point, 16);
                mBaiduMap.setMapStatus(mapStatusUpdate);
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    Toast.makeText(getApplicationContext(), "E", Toast.LENGTH_LONG).show();
                }
                //获取反向地理编码结果
            }
        };
        //设置地理编码检索监听者
        mGeoCoder.setOnGetGeoCodeResultListener(listener);
    }

    @OnClick({R.id.customer_detail_collection, R.id.customer_detail_map_toggle, R.id.customer_detail_opd, R.id.customer_detail_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.customer_detail_collection:
                //收藏CDC
                collectCustomer();
                break;
            case R.id.customer_detail_map_toggle:
                //显示地图控件
                showMap();
                break;
            case R.id.customer_detail_opd:
                //进入门诊列表
                Intent intent = new Intent(this, ClinicActivity.class);
                intent.putExtra("cdcId", mCdcId);
                startActivity(intent);
                break;
            case R.id.customer_detail_service:
                //拨打服务号码
                callService();
                break;
        }
    }

    /**
     * 收藏CDC
     */
    private void collectCustomer() {
        mCustomerDetailCollection.setClickable(false);
        //根据状态值不同，请求不同接口
        OkHttpUtils.post().url(mUserAddStatus == 0 ? URL_ADD_USER_CDC : URL_CANCEL_USER_CDC)
                .addHeader(ACCESS_TOKEN, accessToken).addParams("cdcId", mCdcId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mCustomerDetailCollection.setClickable(true);
                LogUtil.d("客户详情E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                mCustomerDetailCollection.setClickable(true);
                LogUtil.d("客户详情：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //收藏或取消收藏成功
                        //更换收藏状态值
                        mUserAddStatus = mUserAddStatus == 0 ? 1 : 0;
                        LogUtil.d("user collect status is " + mUserAddStatus);
                        cdc.setUserAddStatus(mUserAddStatus);
                        //告诉列表页我这一页进行了刷新操作--我就是不用event_bus
                        // Intent只是给全部客户用来刷新对应数据
                        Intent intent = new Intent();
                        intent.putExtra("cdc", cdc);
                        intent.putExtra("position", position);
                        setResult(RESULT_CDC_COLLECTION, intent);
                        //更换收藏图标
                        mCustomerDetailCollection.setImageResource(mUserAddStatus == 0 ? R.mipmap.ico_customer_details_collection : R.mipmap.ico_customer_details_cancel_collection);
                        ToastUtil.showShort(getApplicationContext(), mUserAddStatus == 0 ? "取消收藏成功" : "收藏成功");
                        break;
                    case "failure":
                        //显示错误信息
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 控制地图显示
     */
    private void showMap() {
        mMapToggle = !mMapToggle;
        mCustomerDetailMapToggle.setImageResource(mMapToggle ? R.mipmap.ico_customer_details_map_activation : R.mipmap.ico_customer_details_map_default);
        mCustomerDetailMap.setVisibility(mMapToggle ? View.VISIBLE : View.GONE);
        //发起地理编码检索
        //TODO 逻辑有待调整
        mGeoCoder.geocode(new GeoCodeOption()
                .city("")
                .address(mCustomerDetailAddress.getText().toString()));
    }

    /**
     * 拨号联系
     */
    private void callService() {
        String phoneNumber = mCustomerDetailPhone.getText().toString();
        //跳转到拨号界面
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomerDetailBaiduMap.onDestroy();
        //释放地理编码检索实例
        mGeoCoder.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCustomerDetailBaiduMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCustomerDetailBaiduMap.onPause();
    }
}
