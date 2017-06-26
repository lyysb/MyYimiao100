package com.yimiao100.sale.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.yimiao100.sale.bean.Area;
import com.yimiao100.sale.bean.City;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.Province;
import com.yimiao100.sale.bean.RegionListBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.yimiao100.sale.ext.JSON.parseObject;


/**
 * 刷新地域信息
 * Created by 亿苗通 on 2016/11/29.
 */

public class RegionUtil {

    private static final String URL_REGION_LIST = Constant.BASE_URL + "/api/region/all";

    private RegionUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取地域信息数据
     * @param activity
     * @param listListener
     */
    public static void getRegionList(final Activity activity, final HandleRegionListListener listListener) {
        String region_list = (String) SharePreferenceUtil.get(activity, Constant.REGION_LIST, "");
        if (TextUtils.isEmpty(region_list)) {
            //本地没有region数据，请求网络。重新进行本地缓存
            RefreshRegion(activity, listListener);
        } else {
            //本地有数据，直接解析本地数据获取数据
            ArrayList<Province> provinceList = parseObject(region_list, RegionListBean.class).getProvinceList();
            if (listListener != null) {
                List<City> cityList;
                List<Area> areaList;
                //在这里处理数据，然后直接返回三个集合
                for (Province province : provinceList) {
                    cityList = province.getCityList();
                    if (cityList.size() == 0) {
                        City cityTemp = new City();
                        cityTemp.setName("");
                        cityTemp.setAreaList(new ArrayList<Area>());
                        cityList.add(cityTemp);
                    }
                    for (City city : cityList) {
                        areaList = city.getAreaList();
                        if (areaList.size() == 0) {
                            Area areaTemp = new Area();
                            areaTemp.setName("");
                            areaList.add(areaTemp);
                        }
                    }
                }
                listListener.handleRegionList(provinceList);
            }
        }
    }
    private static void RefreshRegion(final Activity activity, final HandleRegionListListener listListener) {
        OkHttpUtils.get().url(URL_REGION_LIST).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("获取地域信息E：" + e.getLocalizedMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("获取地域信息：" + response);
                ErrorBean errorBean = parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //保存地域信息列表
                        SharePreferenceUtil.put(activity, Constant.REGION_LIST, response);
                        ArrayList<Province> provinceList = parseObject(response,
                                RegionListBean.class).getProvinceList();
                        if (listListener != null) {
                            List<City> cityList;
                            List<Area> areaList;
                            //在这里处理数据，然后直接返回三个集合
                            for (Province province : provinceList) {
                                cityList = province.getCityList();
                                if (cityList.size() == 0) {
                                    City cityTemp = new City();
                                    cityTemp.setName("");
                                    cityTemp.setAreaList(new ArrayList<Area>());
                                    cityList.add(cityTemp);
                                }
                                for (City city : cityList) {
                                    areaList = city.getAreaList();
                                    if (areaList.size() == 0) {
                                        Area areaTemp = new Area();
                                        areaTemp.setName("");
                                        areaList.add(areaTemp);
                                    }
                                }
                            }
                            listListener.handleRegionList(provinceList);
                        }
                        break;
                    case "failure":
                        Util.showError(activity, errorBean.getReason());
                        break;
                }
            }
        });
    }

    public interface HandleRegionListListener {
        void handleRegionList(ArrayList<Province> provinceList);
    }
}
