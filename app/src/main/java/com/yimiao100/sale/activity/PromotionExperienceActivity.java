package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Area;
import com.yimiao100.sale.bean.City;
import com.yimiao100.sale.bean.Experience;
import com.yimiao100.sale.bean.Province;
import com.yimiao100.sale.utils.RegionUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jeesoft.widget.pickerview.CharacterPickerWindow;

import static com.yimiao100.sale.utils.TimeUtil.getTime;

/**
 * 绑定对公主体-添加推广经历
 */
public class PromotionExperienceActivity extends BaseActivity implements TitleView.TitleBarOnClickListener, RegionUtil.HandleRegionListListener, TimePickerView.OnTimeSelectListener {

    @BindView(R.id.promotion_experience_title)
    TitleView mPromotionExperienceTitle;
    @BindView(R.id.promotion_experience_begin)
    TextView mPromotionExperienceBegin;
    @BindView(R.id.promotion_experience_end)
    TextView mPromotionExperienceEnd;
    @BindView(R.id.promotion_experience_provence)
    TextView mPromotionExperienceProvence;
    @BindView(R.id.promotion_experience_city)
    TextView mPromotionExperienceCity;
    @BindView(R.id.promotion_experience_vaccine)
    EditText mPromotionExperienceVaccine;

    private static int TYPE;
    private static final int ADD_EXPERIENCE = 101;
    private static final int EDIT_EXPERIENCE = 102;

    private TimePickerView mTimePicker;
    private CharacterPickerWindow mCharacterPickerView;
    private ArrayList<String> mOptions1Items;
    private List<List<String>> mOptions2Items;
    private ArrayList<Province> mProvinceList;
    private Experience mExperience;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_experience);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mPromotionExperienceTitle.setOnTitleBarClick(this);

        initTimeView();

        initRegionView();
    }

    private void initTimeView() {
        Calendar startDate = Calendar.getInstance();
        startDate.set(1949, 1, 1);
        mTimePicker = new TimePickerView.Builder(this, this)
                .setType(TimePickerView.Type.YEAR_MONTH)
                .setContentSize(14)
                .setSubCalSize(14)
                .setSubmitColor(getResources().getColor(R.color.colorMain))
                .setCancelColor(getResources().getColor(R.color.colorMain))
                .setOutSideCancelable(false)
                .setRangDate(startDate, Calendar.getInstance())
                .setDate(Calendar.getInstance()).build();
    }

    private void initRegionView() {
        mCharacterPickerView = new CharacterPickerWindow(this);
    }

    private void initData() {
        TYPE = getIntent().getIntExtra("type", -1);
        if (TYPE == ADD_EXPERIENCE) {
            // 新增推广经历
            mExperience = new Experience();
            mExperience.setSerialNo(Util.generateSeriaNo());
        }
        if (TYPE == EDIT_EXPERIENCE) {
            // 编辑推广经历
            mExperience = getIntent().getParcelableExtra("experience");
            mPosition = getIntent().getIntExtra("position", -1);
            mPromotionExperienceBegin.setText(mExperience.getStartAtFormat());
            mPromotionExperienceEnd.setText(mExperience.getEndAtFormat());
            mPromotionExperienceProvence.setText(mExperience.getProvinceName());
            mPromotionExperienceCity.setText(mExperience.getCityName());
            mPromotionExperienceVaccine.setText(mExperience.getProductName());
        }
        //读取地域信息
        RegionUtil.getRegionList(this, this);
    }



    @Override
    public void handleRegionList(ArrayList<Province> provinceList) {
        mProvinceList = provinceList;
        handleRegionData();
    }

    @OnClick({R.id.promotion_experience_begin, R.id.promotion_experience_end, R.id
            .promotion_experience_provence, R.id.promotion_experience_city})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.promotion_experience_begin:
                mTimePicker.show(view);
                break;
            case R.id.promotion_experience_end:
                mTimePicker.show(view);
                break;
            case R.id.promotion_experience_provence:
            case R.id.promotion_experience_city:
                showRegion(view);
                break;
        }
    }



    @Override
    public void onTimeSelect(Date date, View v) {
        switch (v.getId()) {
            case R.id.promotion_experience_begin:
                mPromotionExperienceBegin.setText(getTime(date, "yyyy-MM"));
                mExperience.setStartAtFormat(getTime(date, "yyyy-MM"));
                break;
            case R.id.promotion_experience_end:
                mPromotionExperienceEnd.setText(getTime(date, "yyyy-MM"));
                mExperience.setEndAtFormat(getTime(date, "yyyy-MM"));
                break;
        }
    }
    private void showRegion(View view) {
        //设置三级联动
        mCharacterPickerView.setPicker(mOptions1Items, mOptions2Items);
        //开始时获取焦点
        mCharacterPickerView.setFocusable(true);
        //设置不循环
        mCharacterPickerView.setCyclic(false);
        //设置默认选中的三级项目
        mCharacterPickerView.setSelectOptions(0, 1);
        //监听确定选择按钮
        mCharacterPickerView.setOnoptionsSelectListener(new CharacterPickerWindow.OnOptionsSelectListener() {


            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String provenceName = mOptions1Items.get(options1);
                String cityName = mOptions2Items.get(options1).get(option2);
                int provinceId = mProvinceList.get(options1).getId();
                int cityId = mProvinceList.get(options1).getCityList().get(option2).getId();
                mPromotionExperienceProvence.setText(provenceName);
                mPromotionExperienceCity.setText(cityName);
                mExperience.setProvinceId(provinceId);
                mExperience.setProvinceName(provenceName);
                mExperience.setCityName(cityName);
                mExperience.setCityId(cityId);

            }
        });
        //点击弹出选项选择器
        mCharacterPickerView.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        //弹出后失去焦点
        mCharacterPickerView.setFocusable(false);
    }

    /**
     * 加工处理地域数据
     */
    private void handleRegionData() {
        //省列表集合
        mOptions1Items = new ArrayList<>();
        //省<市>列表集合
        mOptions2Items = new ArrayList<>();

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
        }
    }

    @Override
    public void rightOnClick() {
        if (TextUtils.equals(mPromotionExperienceBegin.getText().toString(), "开始日期")
                || TextUtils.equals(mPromotionExperienceEnd.getText().toString(), "结束日期")
                || TextUtils.equals(mPromotionExperienceProvence.getText().toString(), "省")
                || TextUtils.equals(mPromotionExperienceVaccine.getText().toString(), "")) {
            ToastUtil.showShort(this, "请填写完整信息");
        } else {
            mExperience.setProductName(mPromotionExperienceVaccine.getText().toString());
            Intent intent = new Intent();
            intent.putExtra("experience", mExperience);
            switch (TYPE) {
                case ADD_EXPERIENCE:
                    setResult(ADD_EXPERIENCE, intent);
                    break;
                case EDIT_EXPERIENCE:
                    intent.putExtra("position", mPosition);
                    setResult(EDIT_EXPERIENCE, intent);
                    break;
            }
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //当三级联动对话框正在显示的时候，按下返回键，其实是让对话框消失
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mTimePicker.isShowing()
                    || mCharacterPickerView.isShowing()) {
                mTimePicker.dismiss();
                mCharacterPickerView.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void leftOnClick() {
        finish();
    }
}
