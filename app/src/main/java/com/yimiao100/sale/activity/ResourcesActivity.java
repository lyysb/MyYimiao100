package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.ResourcesAdapter;
import com.yimiao100.sale.adapter.peger.ResourceAdAdapter;
import com.yimiao100.sale.base.BaseActivitySingleList;
import com.yimiao100.sale.bean.Area;
import com.yimiao100.sale.bean.Carousel;
import com.yimiao100.sale.bean.City;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Province;
import com.yimiao100.sale.bean.ResourceBean;
import com.yimiao100.sale.bean.ResourceListBean;
import com.yimiao100.sale.bean.ResourceResultBean;
import com.yimiao100.sale.utils.CarouselUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DensityUtil;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.RegionUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jeesoft.widget.pickerview.CharacterPickerWindow;
import okhttp3.Call;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * 资源-资源列表
 */
public class ResourcesActivity extends BaseActivitySingleList implements CarouselUtil
        .HandleCarouselListener, View.OnClickListener, RegionUtil.HandleRegionListListener {


    private final String URL_RESOURCE_LIST = Constant.BASE_URL + "/api/resource/resource_list";
    private List<Province> mProvinceList;

    //省市县三级联动选择器
    private CharacterPickerWindow mOptions;
    private List<String> mOptions1Items;
    private List<List<String>> mOptions2Items;
    private List<List<List<String>>> mOptions3Items;
    //省份position
    private int mProvincePosition;
    //市position
    private int mCityPosition;
    //县position
    private int mCountyPosition;
    //区域集合
    private HashMap<String, String> region = new HashMap<>();


    private TextView mResourcesProvince;
    private TextView mResourcesCity;
    private TextView mResourcesCounty;
    private ArrayList<ResourceListBean> mResourcesList;

    /**
     * 显示下一页
     */
    public static final int SHOW_NEXT_PAGE = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NEXT_PAGE:
                    showNextPage();
                    break;
            }
        }
    };
    private ViewPager mResources_view_pager;
    private ResourcesAdapter mResourcesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEmptyView("好饭不怕晚，推广资源也是。", R.mipmap.ico_resources);
        //初始化滚轮联动器
        mOptions = new CharacterPickerWindow(this);
        initHeadView();
    }

    @Override
    protected void initView() {
        super.initView();
        //更改ListView分割线宽度
        mListView.setDividerHeight(DensityUtil.dp2px(this, 3));
    }

    @Override
    protected void setTitle(TitleView titleView) {
        titleView.setTitle("资源");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 3000);
    }

    /**
     * 初始化头部View
     */
    private void initHeadView() {
        View view = View.inflate(this, R.layout.head_resources, null);
        mListView.addHeaderView(view);
        mResources_view_pager = (ViewPager) view.findViewById(R.id.resources_view_pager);
        //获取轮播图数据
        CarouselUtil.Companion.getCarouselList(this, "resource", this);

        //省份信息
        mResourcesProvince = (TextView) view.findViewById(R.id.resources_province);
        mResourcesProvince.setOnClickListener(this);
        //市信息
        mResourcesCity = (TextView) view.findViewById(R.id.resources_city);
        mResourcesCity.setOnClickListener(this);
        //县信息
        mResourcesCounty = (TextView) view.findViewById(R.id.resources_county);
        mResourcesCounty.setOnClickListener(this);
        //默认市、县不可点击
        mResourcesCity.setClickable(false);
        mResourcesCounty.setClickable(false);
        //搜索
        ImageView resources_search = (ImageView) view.findViewById(R.id.resources_search);
        resources_search.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        //读取地域信息
        RegionUtil.getRegionList(this, this);
    }

    @Override
    public void handleRegionList(ArrayList<Province> provinceList) {
        mProvinceList = provinceList;
        handleRegionData();
    }


    /**
     * 进一步加工region数据
     */
    private void handleRegionData() {
        //省列表集合
        mOptions1Items = new ArrayList<>();
        //省<市>列表集合
        mOptions2Items = new ArrayList<>();
        //省<市<县>>列表集合
        mOptions3Items = new ArrayList<>();
        //重新填充集合数据
        for (Province province : mProvinceList) {
            String ProvinceName = province.getName();
            List<City> cityList = province.getCityList();
            if (cityList.size() == 0) {
                City city_temp = new City();
                city_temp.setAreaList(new ArrayList<Area>());
                cityList.add(city_temp);
            }
            List<String> mOptions2Items_temp = new ArrayList<>();
            List<List<String>> mOptions3Items_temp = new ArrayList<>();
            for (City city : cityList) {
                String CityName = city.getName();
                mOptions2Items_temp.add(CityName == null ? "" : CityName);
                List<Area> areaList = city.getAreaList();
                if (areaList.size() == 0) {
                    areaList.add(new Area());
                }
                List<String> mOptions3Items_temp_temp = new ArrayList<>();
                for (Area area : areaList) {
                    String AreaName = area.getName();
                    mOptions3Items_temp_temp.add(AreaName == null ? "" : AreaName);
                }
                mOptions3Items_temp.add(mOptions3Items_temp_temp);
            }
            mOptions1Items.add(ProvinceName);
            mOptions2Items.add(mOptions2Items_temp);
            mOptions3Items.add(mOptions3Items_temp);
        }
    }


    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(URL_RESOURCE_LIST).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(PAGE, page + "")
                .addParams(PAGE_SIZE, mPageSize)
                .params(region)
                .build();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获取Item的资源信息，携带resourceId进入资源详情界面
        int resourceID = mResourcesList.get(position - 1).getId();
        Intent intent = new Intent(this, ResourcesDetailActivity.class);
        //封装resourceID
        intent.putExtra("resourceID", resourceID);
        //进入详情页
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resources_province:
                //打开省份选择器
                showProvince(v);
                break;
            case R.id.resources_city:
                //打开市选择器
                showCity(v);
                break;
            case R.id.resources_county:
                //打开县选择器
                showCounty(v);
                break;
            case R.id.resources_search:
                //搜索
                mPage = 1;
                onRefresh();
                break;
        }
    }

    /**
     * 省份
     *
     * @param v
     */
    private void showProvince(View v) {
        //设置滚轮器联动数据
        mOptions.setPicker(mOptions1Items);
        //设置不循环
        mOptions.setCyclic(false);
        //获取焦点
        mOptions.setFocusable(true);
        //设置默认选择
        mOptions.setSelectOptions(0);
        //监听确定选择器
        mOptions.setOnoptionsSelectListener(new CharacterPickerWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //获取省份名字
                String province_name = mOptions1Items.get(options1);
                mResourcesProvince.setText(province_name);
                //记录省列表确定位置
                mProvincePosition = options1;
                //获得省ID
                int provinceID = mProvinceList.get(mProvincePosition).getId();
                if (provinceID != 0) {
                    //清空区域参数集合
                    region.clear();
                    //添加到区域参数集合中
                    region.put("provinceId", provinceID + "");
                } else {
                    //清空所有数据-虽然现在没必要
                    region.clear();
                }
                //重置市、县数据，设置市可点击，县不可点击
                mResourcesCity.setText("");
                mResourcesCity.setClickable(true);
                mResourcesCounty.setText("");
                mResourcesCounty.setClickable(false);
            }
        });
        //显示选择器
        mOptions.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //移除焦点
        mOptions.setFocusable(false);
    }

    /**
     * 市
     *
     * @param v
     */
    private void showCity(View v) {
        //设置滚轮器联动数据
        mOptions.setPicker(mOptions2Items.get(mProvincePosition));
        //设置不循环
        mOptions.setCyclic(false);
        //获取焦点
        mOptions.setFocusable(true);
        //设置默认选择
        mOptions.setSelectOptions(0);
        //监听确定选择器
        mOptions.setOnoptionsSelectListener(new CharacterPickerWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //获取市名
                String city_name = mOptions2Items.get(mProvincePosition).get(options1);
                mResourcesCity.setText(city_name);
                //记录市列表选中位置
                mCityPosition = options1;
                //记录市ID
                int cityID = mProvinceList.get(mProvincePosition).getCityList().get
                        (mCityPosition).getId();
                if (cityID != 0) {
                    //添加到区域属性集合中
                    region.put("cityId", cityID + "");
                } else {
                    //清空市属性集合
                    region.remove("cityId");
                }
                //移除县属性集合
                region.remove("areaId");
                //重置县数据，设置可点击
                mResourcesCounty.setText("");
                mResourcesCounty.setClickable(true);
            }
        });
        //显示选择器
        mOptions.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //移除焦点
        mOptions.setFocusable(false);
    }

    /**
     * 县
     *
     * @param v
     */
    private void showCounty(View v) {
        //设置滚轮器联动数据
        mOptions.setPicker(mOptions3Items.get(mProvincePosition).get(mCityPosition));
        //设置不循环
        mOptions.setCyclic(false);
        //获取焦点
        mOptions.setFocusable(true);
        //设置默认选择
        mOptions.setSelectOptions(0);
        //监听确定选择器
        mOptions.setOnoptionsSelectListener(new CharacterPickerWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //获取县名
                String county_name = mOptions3Items.get(mProvincePosition).get(mCityPosition).get
                        (options1);
                mResourcesCounty.setText(county_name);
                //记录县列表选中位置
                mCountyPosition = options1;
                //记录县ID
                int countyID = mProvinceList.get(mProvincePosition).getCityList().get(mCityPosition)
                        .getAreaList().get(mCountyPosition).getId();
                if (countyID != 0) {
                    //添加县ID到属性集合中
                    region.put("areaId", countyID + "");
                } else {
                    //移除县属性
                    region.remove("areaId");
                }
            }
        });
        //显示选择器
        mOptions.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //移除焦点
        mOptions.setFocusable(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //当对话框正在显示的时候，按下返回键，其实是让对话框消失
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mOptions.isShowing()) {
                mOptions.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 自动切换下一页图片
     */
    public void showNextPage() {
        mResources_view_pager.setCurrentItem(mResources_view_pager.getCurrentItem() + 1);
        mHandler.sendEmptyMessageDelayed(SHOW_NEXT_PAGE, 3000);
    }

    @Override
    public void onRefresh() {
        //刷新列表
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("资源列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (response.length() > 4000) {
                    for (int i = 0; i < response.length(); i += 4000) {
                        if (i + 4000 < response.length()) {
                            LogUtil.Companion.d(i + "资源列表：" + response.substring(i, i + 4000));
                        } else {
                            LogUtil.Companion.d(i + "资源列表：" + response.substring(i, response
                                    .length()));
                        }
                    }
                } else {
                    LogUtil.Companion.d("资源列表：" + response);
                }
                ErrorBean errorBean = parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage = 2;
                        ResourceResultBean resourceResult = parseObject(response,
                                ResourceBean.class).getResourceResult();
                        mTotalPage = resourceResult.getTotalPage();
                        //解析JSON，填充Adapter
                        //获取资源列表
                        mResourcesList = resourceResult.getResourcesList();
                        handleEmptyData(mResourcesList);
                        //填充Adapter
                        mResourcesAdapter = new ResourcesAdapter(getApplicationContext(),
                                mResourcesList);
                        mListView.setAdapter(mResourcesAdapter);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeMessages(SHOW_NEXT_PAGE);
    }


    @Override
    protected void onLoadMore() {
        getBuild(mPage).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("资源列表E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("资源列表：" + response);
                ErrorBean errorBean = parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mPage++;
                        mResourcesList.addAll(parseObject(response, ResourceBean
                                .class).getResourceResult().getResourcesList());
                        mResourcesAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
                mListView.onLoadMoreComplete();
            }
        });
    }

    /**
     * 处理轮播图数据
     *
     * @param carouselList
     */
    @Override
    public void handleCarouselList(ArrayList<Carousel> carouselList) {
        mResources_view_pager.setAdapter(new ResourceAdAdapter(carouselList));
        mResources_view_pager.setCurrentItem(mResources_view_pager.getAdapter().getCount() / 2);
        mResources_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LogUtil.Companion.d("资源position------" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}