package com.yimiao100.sale.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.bigkoo.pickerview.OptionsPickerView
import com.yimiao100.sale.R
import com.yimiao100.sale.bean.Area
import com.yimiao100.sale.bean.City
import com.yimiao100.sale.bean.Province
import com.yimiao100.sale.utils.LogUtil
import com.yimiao100.sale.utils.RegionUtil
import com.yimiao100.sale.utils.ToastUtil
import java.util.*
import kotlin.collections.HashMap

/**
 * 地域搜索View
 * Created by Michel on 2017/5/24.
 */
class RegionSearchView @JvmOverloads  constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), OptionsPickerView.OnOptionsSelectListener, RegionUtil.HandleRegionListListener {

    lateinit var regionPicker: OptionsPickerView<Any>
    lateinit var regionIds: HashMap<String, String>    // 返回地域id集合参数给外部
    lateinit var tvProvince: TextView
    lateinit var tvCity: TextView
    lateinit var tvArea: TextView

    var provincePicker: ArrayList<Province>? = null
    var cityPicker: ArrayList<City>? = null
    var areaPicker: ArrayList<Area>? = null

    var searchClickListener: onSearchClickListener? = null

    init {
        // 初始化View
        initView(context)

        // 初始化数据
        initData(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.region_search, this)

        tvProvince = findViewById(R.id.region_province) as TextView
        tvProvince.setOnClickListener {
            regionPicker.setPicker(provincePicker as List<Any>?)
            regionPicker.show(it)
        }

        tvCity = findViewById(R.id.region_city) as TextView
        tvCity.setOnClickListener {
            if (tvProvince.text.isEmpty()) {
                ToastUtil.showShort(context, "请先选择省")
                return@setOnClickListener
            }
            regionPicker.setPicker(cityPicker as List<Any>?)
            regionPicker.show(it)
        }

        tvArea = findViewById(R.id.region_county) as TextView
        tvArea.setOnClickListener {
            if (tvCity.text.isEmpty()) {
                ToastUtil.showShort(context, "请先选择市")
                return@setOnClickListener
            }
            regionPicker.setPicker(areaPicker as List<Any>?)
            regionPicker.show(it)
        }

        findViewById(R.id.region_search).setOnClickListener {
            searchClickListener?.let {
                LogUtil.d("search region is ${regionIds.entries}")
                it.regionSearch(regionIds)
            }
        }

        // 初始化选择器
        regionPicker = OptionsPickerView.Builder(context, this)
                .setContentTextSize(16)
                .setSubCalSize(14)
                .setSubmitColor(resources.getColor(R.color.colorMain))
                .setCancelColor(resources.getColor(R.color.colorMain))
                .setOutSideCancelable(false)
                .build()

    }

    private fun initData(context: Context) {
        RegionUtil.getRegionList(context as Activity, this)

        regionIds = HashMap()
    }

    /**
     * 搜索监听
     */
    fun setOnSearchClickListener(listener: onSearchClickListener) {
        searchClickListener = listener
    }

    interface onSearchClickListener {
        fun regionSearch(regionIDs: HashMap<String, String>)
    }


    override fun onOptionsSelect(options1: Int, options2: Int, options3: Int, v: View?) {
        when (v?.id) {
            R.id.region_province -> {
                tvProvince.text = provincePicker?.get(options1)?.name
                tvCity.text = ""
                tvArea.text = ""
                cityPicker = provincePicker?.get(options1)?.cityList as ArrayList<City>?
                regionIds.clear()
                regionIds.put("provinceId", provincePicker?.get(options1)!!.id.toString())
            }
            R.id.region_city -> {
                tvCity.text = cityPicker?.get(options1)?.name
                tvArea.text = ""
                areaPicker = cityPicker?.get(options1)?.areaList as ArrayList<Area>?
                regionIds.remove("areaId")
                regionIds.put("cityId", cityPicker?.get(options1)!!.id.toString())
            }
            R.id.region_county -> {
                tvArea.text = areaPicker?.get(options1)?.name
                regionIds.put("areaId", areaPicker?.get(options1)!!.id.toString())
            }
        }
    }

    override fun handleRegionList(provinceList: ArrayList<Province>) {
        provincePicker = provinceList
    }

}
