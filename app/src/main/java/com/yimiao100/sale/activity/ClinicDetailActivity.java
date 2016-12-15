package com.yimiao100.sale.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.view.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 门诊详情
 */
public class ClinicDetailActivity extends BaseActivity implements TitleView.TitleBarOnClickListener {

    @BindView(R.id.clinic_detail_title)
    TitleView mClinicDetailTitle;
    @BindView(R.id.clinic_detail_clinic_name)
    TextView mClinicDetailClinicName;
    @BindView(R.id.clinic_detail_card_account)
    TextView mClinicDetailCardAccount;
    @BindView(R.id.clinic_detail_use_account)
    TextView mClinicDetailUseAccount;
    @BindView(R.id.clinic_detail_map_toggle)
    ImageButton mClinicDetailMapToggle;
    @BindView(R.id.clinic_detail_baidu_map)
    MapView mClinicDetailBaiduMap;
    @BindView(R.id.clinic_detail_map)
    RelativeLayout mClinicDetailMap;
    @BindView(R.id.clinic_detail_address)
    TextView mClinicDetailAddress;
    @BindView(R.id.clinic_detail_phone)
    TextView mClinicDetailPhone;
    @BindView(R.id.clinic_detail_service)
    FrameLayout mClinicDetailService;

    private BaiduMap mBaiduMap;
    private GeoCoder mGeoCoder;

    private boolean mMapToggle = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化百度SDK
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_clinic_detail);
        ButterKnife.bind(this);

        initView();

        initData();
    }
    private void initView() {
        mClinicDetailTitle.setOnTitleBarClick(this);
        //获取普通地图
        mBaiduMap = mClinicDetailBaiduMap.getMap();
    }

    private void initData() {
        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();
        //门诊名称
        String clinicName = bundle.getString("clinicName");
        mClinicDetailClinicName.setText(clinicName);
        //新生儿建卡数
        String cardAmount = bundle.getString("cardAmount", "0");
        mClinicDetailCardAccount.setText("新生儿建卡数：         " + cardAmount + " 张/年");
        //狂犬疫苗使用数量
        String useAmount = bundle.getString("useAmount", "0");
        mClinicDetailUseAccount.setText("狂犬疫苗使用数量： " + useAmount + " 支/年");
        //地址
        String address = bundle.getString("address");
        mClinicDetailAddress.setText(address);
        //电话
        String phoneNumber = bundle.getString("phoneNumber");
        mClinicDetailPhone.setText(phoneNumber);

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
                    ToastUtil.showLong(getApplicationContext(), "暂时没有搜索到目的地");
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
    @OnClick({R.id.clinic_detail_map_toggle, R.id.clinic_detail_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clinic_detail_map_toggle:
                //显示地图控件
                showMap();
                break;
            case R.id.clinic_detail_service:
                //拨打服务号码
                callService();
                break;
        }
    }
    private void showMap() {
        mMapToggle = !mMapToggle;
        mClinicDetailMapToggle.setImageResource(mMapToggle ? R.mipmap.ico_customer_details_map_activation : R.mipmap.ico_customer_details_map_default);
        mClinicDetailMap.setVisibility(mMapToggle ? View.VISIBLE : View.GONE);
        //发起地理编码检索
        //TODO 逻辑有待调整
        mGeoCoder.geocode(new GeoCodeOption()
                .city("")
                .address(mClinicDetailAddress.getText().toString()));
    }
    private void callService() {
        String phoneNumber = mClinicDetailPhone.getText().toString();
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
        mClinicDetailBaiduMap.onDestroy();
        //释放地理编码检索实例
        mGeoCoder.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mClinicDetailBaiduMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mClinicDetailBaiduMap.onPause();
    }
}
