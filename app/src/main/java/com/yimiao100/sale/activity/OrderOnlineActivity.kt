package com.yimiao100.sale.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bigkoo.pickerview.OptionsPickerView
import com.bigkoo.pickerview.TimePickerView
import com.squareup.picasso.Picasso
import com.yimiao100.sale.R
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.bean.*
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.utils.*
import com.yimiao100.sale.utils.TimeUtil.timeStamp2Date
import com.yimiao100.sale.view.TitleView
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import de.hdodenhof.circleimageview.CircleImageView
import me.iwf.photopicker.PhotoPicker
import me.iwf.photopicker.PhotoPreview
import okhttp3.Call
import okhttp3.Request
import org.jetbrains.anko.find
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 在线下单
 */
class OrderOnlineActivity : BaseActivity(), TitleView.TitleBarOnClickListener, View.OnClickListener, OptionsPickerView.OnOptionsSelectListener, TimePickerView.OnTimeSelectListener {

    lateinit var vendor: Vendor
    lateinit var tvCategoryName: TextView
    lateinit var tvProductName: TextView
    lateinit var tvSpec: TextView
    lateinit var tvDosageForm: TextView
    lateinit var tvProvince: TextView
    lateinit var tvCity: TextView
    lateinit var tvArea: TextView
    lateinit var tvCustomer: TextView
    lateinit var tvApplyDeliveryAt: TextView
    lateinit var evDeliveryQty: EditText
    lateinit var tvUnits: TextView
    lateinit var ivProtocol: ImageView
    lateinit var evRemark: EditText
    lateinit var evConsigneeName: EditText
    lateinit var evConsigneePhone: EditText
    lateinit var evConsigneeAddress: EditText
    lateinit var evBizName: EditText
    lateinit var evBizPhone: EditText
    lateinit var btnSubmit: Button
    lateinit var optionsPicker: OptionsPickerView<Any>
    lateinit var timePicker: TimePickerView
    lateinit var bizSelect: List<CategoryList>
    lateinit var productList: List<ProductList>
    lateinit var provinceList: List<Province>
    lateinit var unitsList: List<String>
    lateinit var cityList: List<City>
    lateinit var areaList: List<Area>
    lateinit var customerList: List<Customer>
    lateinit var progressDialog: ProgressDialog

    val URL_ORDER_ONLINE = Constant.BASE_URL + "/api/order_online/order_online_apply"
    val URL_ORDER_SUBMIT = Constant.BASE_URL + "/api/order_online/order_online_submit"
    val params: HashMap<String, String> = HashMap()
    val VENDOR_ID = "vendorId"
    val CATEGORY_ID = "categoryId"
    val PRODUCT_ID = "productId"
    val SPEC_ID = "specId"
    val DOSAGE_FORM_ID = "dosageFormId"
    val PROVINCE_ID = "provinceId"
    val CITY_ID = "cityId"
    val AREA_ID = "areaId"
    val CUSTOMER_ID = "customerId"
    val APPLY_DELIVERY_AT = "applyDeliveryAt"
    val DELIVERY_QTY = "deliveryQty"
    val DELIVERY_UNITS = "deliveryUnits"
    val PROTOCOL_FILE = "protocolFile"
    val REMARK = "remark"
    val CONSIGNEE_NAME = "consigneeName"
    val CONSIGNEE_PHONE_NUMBER = "consigneePhoneNumber"
    val CONSIGNEE_ADDRESS = "consigneeAddress"
    val BIZ_NAME = "bizName"
    val BIZ_PHONE_NUMBER = "bizPhoneNumber"

    // 提交参数
    var vendorId: Int = 0
    var categoryId: Int = 0
    var productId: Int = 0
    var specId: Int = 0
    var dosageFormId: Int = 0
    var provinceId: Int = 0
    var cityId: Int = 0
    var areaId: Int = 0
    var customerId: Int = 0
    var applyDeliveryAt: String? = null
    var deliveryQty: String? = null
    var deliveryUnits: String? = null
    var protocolFile: File? = null
    var remark: String? = null
    var consigneeName: String? = null
    var consigneePhoneNumber: String? = null
    var consigneeAddress: String? = null
    var bizName: String? = null
    var bizPhoneNumber: String? = null
    var protocolFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_online)

        showLoadingProgress()

        initVariate()

        initView()

        initData()
    }

    private fun initVariate() {
        vendor = intent.getParcelableExtra<Vendor>("vendor")
        vendorId = vendor.id
        val orderId = intent.getIntExtra("orderId", -1)
        if (orderId != -1) {
            params.put("orderId", orderId.toString())
        }
    }

    private fun initView() {
        find<TitleView>(R.id.order_online_title).setOnTitleBarClick(this)
        find<ImageView>(R.id.order_online_note).setOnClickListener(this)
        val logo = find<CircleImageView>(R.id.order_online_logo)
        Picasso.with(this).load(vendor.logoImageUrl)
                .resize(DensityUtil.dp2px(this, 32F), DensityUtil.dp2px(this, 32F))
                .into(logo)
        find<TextView>(R.id.order_online_vendor_name).text = vendor.vendorName

        tvCategoryName = find(R.id.order_online_category_name)
        tvCategoryName.setOnClickListener(this)
        tvProductName = find(R.id.order_online_product_name)
        tvProductName.setOnClickListener(this)
        tvSpec = find(R.id.order_online_spec)
        tvSpec.setOnClickListener(this)
        tvDosageForm = find(R.id.order_online_dosage_form)
        tvDosageForm.setOnClickListener(this)
        tvProvince = find(R.id.order_online_province)
        tvProvince.setOnClickListener(this)
        tvCity = find(R.id.order_online_city)
        tvCity.setOnClickListener(this)
        tvArea = find(R.id.order_online_area)
        tvArea.setOnClickListener(this)
        tvCustomer = find(R.id.order_online_customer)
        tvCustomer.setOnClickListener(this)
        tvApplyDeliveryAt = find(R.id.order_online_apply_delivery_at)
        tvApplyDeliveryAt.setOnClickListener(this)
        evDeliveryQty = find(R.id.order_online_delivery_qty)
        val filters = arrayOf(FilterUtils())
        evDeliveryQty.filters = filters
        tvUnits = find(R.id.order_online_units)
        tvUnits.setOnClickListener(this)
        ivProtocol = find(R.id.order_online_protocol)
        ivProtocol.setOnClickListener(this)
        evRemark = find(R.id.order_online_remark)
        evConsigneeName = find(R.id.order_online_consignee_name)
        evConsigneePhone = find(R.id.order_online_consignee_phone)
        evConsigneeAddress = find(R.id.order_online_consignee_address)
        evBizName = find(R.id.order_online_biz_name)
        evBizPhone = find(R.id.order_online_biz_phone)
        btnSubmit = find(R.id.order_online_submit)
        btnSubmit.setOnClickListener(this)

        initOptionsPickerView()
        initTimePickerView()

    }

    private fun initOptionsPickerView() {
        optionsPicker = OptionsPickerView.Builder(this, this)
                .setContentTextSize(16)
                .setSubCalSize(14)
                .setSubmitColor(resources.getColor(R.color.colorMain))
                .setCancelColor(resources.getColor(R.color.colorMain))
                .setOutSideCancelable(false)
                .build()
    }

    private fun initTimePickerView() {
        val endDate = Calendar.getInstance()
        endDate.set(2100, 1, 1)
        timePicker = TimePickerView.Builder(this, this)
                .setType(booleanArrayOf(true, true, true, false, false, false))
                .setContentSize(16)
                .setSubCalSize(14)
                .setSubmitColor(resources.getColor(R.color.colorMain))
                .setCancelColor(resources.getColor(R.color.colorMain))
                .setOutSideCancelable(false)
                .setRangDate(Calendar.getInstance(), endDate)
                .setDate(Calendar.getInstance()).build()
    }

    private fun initData() {
        OkHttpUtils.post().url(URL_ORDER_ONLINE).addHeader(ACCESS_TOKEN, accessToken)
                .params(params).addParams(VENDOR_ID, vendor.id.toString())
                .build().execute(object : StringCallback() {
            override fun onResponse(response: String, id: Int) {
                hideLoadingProgress()
                LogUtil.d("init data success is $response")
                val orderOnlineBean = JSON.parseObject(response, OrderOnlineBean::class.java)
                when (orderOnlineBean?.status) {
                    "success" -> {
                        // 初始化数据源
                        bizSelect = orderOnlineBean.bizSelect
                        if (bizSelect.isEmpty()) {
                            val temp = CategoryList(0, "", null, 0, 0, 0)
                            (bizSelect as ArrayList<CategoryList>).add(temp)
                        }
                        // 如果不为空，回显数据，并且禁止点击
                        orderOnlineBean.orderOnline?.let {
                            forbidButton()
                            echoOrderData(it)
                        }

                    }
                    "failure" -> {
                        bizSelect = ArrayList<CategoryList>()
                        if (bizSelect.isEmpty()) {
                            val temp = CategoryList(0, "", null, 0, 0, 0)
                            (bizSelect as ArrayList<CategoryList>).add(temp)
                        }
                        Util.showError(currentContext, orderOnlineBean.reason)
                    }
                }
            }

            override fun onError(call: Call?, e: Exception?, id: Int) {
                hideLoadingProgress()
                LogUtil.d("init data error")
                e?.printStackTrace()
                Util.showTimeOutNotice(currentContext)
                bizSelect = ArrayList<CategoryList>()
                if (bizSelect.isEmpty()) {
                    val temp = CategoryList(0, "", null, 0, 0, 0)
                    (bizSelect as ArrayList<CategoryList>).add(temp)
                }
            }

        })
    }

    private fun forbidButton() {
        tvCategoryName.isEnabled = false
        tvProductName.isEnabled = false
        tvSpec.isEnabled = false
        tvDosageForm.isEnabled = false
        tvProvince.isEnabled = false
        tvCity.isEnabled = false
        tvArea.isEnabled = false
        tvCustomer.isEnabled = false
        tvApplyDeliveryAt.isEnabled = false
        evDeliveryQty.isEnabled = false
        tvUnits.isEnabled = false
        ivProtocol.isEnabled = false
        evRemark.isEnabled = false
        evConsigneeName.isEnabled = false
        evConsigneePhone.isEnabled = false
        evConsigneeAddress.isEnabled = false
        evBizName.isEnabled = false
        evBizPhone.isEnabled = false
        btnSubmit.isEnabled = false
        btnSubmit.text = "已提交"
        btnSubmit.setBackgroundResource(R.drawable.shape_button_forbid)
        btnSubmit.setTextColor(Color.GRAY)
    }

    private fun echoOrderData(orderOnline: OrderOnline) {
        tvCategoryName.text = orderOnline.categoryName
        tvProductName.text = orderOnline.productName
        tvSpec.text = orderOnline.spec
        tvDosageForm.text = orderOnline.dosageForm
        tvProvince.text = orderOnline.provinceName
        tvCity.text = orderOnline.cityName
        tvArea.text = orderOnline.areaName
        tvCustomer.text = orderOnline.customerName
        tvApplyDeliveryAt.text = TimeUtil.timeStamp2Date(orderOnline.applyDeliveryAt.toString(), "yyyy年MM月dd日")
        evDeliveryQty.setText(orderOnline.deliveryQty)
        tvUnits.text = orderOnline.deliveryUnits
        if (orderOnline.protocolFileUrl.isEmpty()) {
            ivProtocol.imageResource = R.mipmap.ico_application_authorization_upload
        } else {
            ivProtocol.imageResource = R.mipmap.ico_application_authorization_success
        }
        evRemark.setText(orderOnline.remark)
        evConsigneeName.setText(orderOnline.consigneeName)
        evConsigneePhone.setText(orderOnline.consigneePhoneNumber)
        evConsigneeAddress.setText(orderOnline.consigneeAddress)
        evBizName.setText(orderOnline.bizName)
        evBizPhone.setText(orderOnline.bizPhoneNumber)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.order_online_note -> enterOrderOnlineNote()         // 进入下单记录界面
            R.id.order_online_category_name -> selectCategoryName(v) // 选择产品
            R.id.order_online_product_name -> selectProductName(v)   // 选择商品以及规格剂型
            R.id.order_online_spec, R.id.order_online_dosage_form -> selectSpecAndDosageForm()
            R.id.order_online_province -> selectProvince(v)          // 选择省
            R.id.order_online_city -> selectCity(v)                  // 选择市
            R.id.order_online_area -> selectArea(v)                  // 选择县
            R.id.order_online_customer -> selectCustomer(v)          // 选择客户
            R.id.order_online_apply_delivery_at -> selectDeliveryAt(v)   // 选择发货时间
            R.id.order_online_units -> selectUnits(v)                // 选择单位
            R.id.order_online_protocol -> selectProtocol()          // 选择协议
            R.id.order_online_submit -> verifyData()                // 校验数据

        }
    }

    private fun enterOrderOnlineNote() {
        startActivity<OrderOnlineNoteActivity>("vendor" to vendor)
        finish()
    }

    private fun selectCategoryName(v: View) {
        optionsPicker.setPicker(bizSelect)
        optionsPicker.show(v)
    }

    private fun selectProductName(v: View) {
        if (tvCategoryName.text.isEmpty()) {
            toast("请先选择产品")
            return
        }
        optionsPicker.setPicker(productList)
        optionsPicker.show(v)
    }

    private fun selectSpecAndDosageForm() {
        if (tvProductName.text.isEmpty()) {
            toast("请先选择商品名")
            return
        }
    }

    private fun selectProvince(v: View) {
        if (tvProductName.text.isEmpty()) {
            toast("请先选择商品名")
            return
        }
        optionsPicker.setPicker(provinceList)
        optionsPicker.show(v)
    }

    private fun selectCity(v: View) {
        if (tvProvince.text.isEmpty()) {
            toast("请先选择省")
            return
        }
        optionsPicker.setPicker(cityList)
        optionsPicker.show(v)
    }

    private fun selectArea(v: View) {
        if (tvCity.text.isEmpty()) {
            toast("请先选择市")
            return
        }
        optionsPicker.setPicker(areaList)
        optionsPicker.show(v)
    }

    private fun selectCustomer(v: View) {
        if (tvArea.text.isEmpty()) {
            toast("请先选择县")
            return
        }
        optionsPicker.setPicker(customerList)
        optionsPicker.show(v)
    }

    private fun selectUnits(v: View) {
        if (tvProductName.text.isEmpty()) {
            toast("请先选择商品名")
            return
        }
        optionsPicker.setPicker(unitsList)
        optionsPicker.show(v)
    }

    override fun onOptionsSelect(options1: Int, options2: Int, options3: Int, v: View) {
        when (v.id) {
            R.id.order_online_category_name -> {
                // 选择产品
                bizSelect[options1].let {
                    LogUtil.d("the category name is ${it.categoryName}")
                    categoryId = it.categoryId
                    productList = it.productList!!
                    tvCategoryName.text = it.categoryName
                    tvProductName.text = ""
                    tvSpec.text = ""
                    tvDosageForm.text = ""
                    tvProvince.text = ""
                    tvCity.text = ""
                    tvArea.text = ""
                    tvCustomer.text = ""
                    tvUnits.text = ""
                }
            }
            R.id.order_online_product_name -> {
                // 选择商品
                productList[options1].let {
                    LogUtil.d("the product name is ${it.productName}")
                    productId = it.productId
                    provinceList = it.provinceList
                    unitsList = it.unitsList
                    specId = it.specId
                    dosageFormId = it.dosageFormId
                    tvProductName.text = it.productName
                    tvSpec.text = it.spec
                    tvDosageForm.text = it.dosageForm
                    tvProvince.text = ""
                    tvCity.text = ""
                    tvArea.text = ""
                    tvCustomer.text = ""
                    tvUnits.text = ""
                }
            }
            R.id.order_online_province -> {
                // 选择省
                provinceList[options1].let {
                    LogUtil.d("the province name is ${it.provinceName}")
                    provinceId = it.provinceId
                    cityList = it.cityList
                    tvProvince.text = it.provinceName
                    tvCity.text = ""
                    tvArea.text = ""
                    tvCustomer.text = ""
                }
            }
            R.id.order_online_city -> {
                // 选择市
                cityList[options1].let {
                    LogUtil.d("the city name is ${it.cityName}")
                    cityId = it.cityId
                    areaList = it.areaList
                    tvCity.text = it.cityName
                    tvArea.text = ""
                    tvCustomer.text = ""
                }
            }
            R.id.order_online_area -> {
                // 选择县
                areaList[options1].let {
                    LogUtil.d("the area name is ${it.areaName}")
                    areaId = it.areaId
                    customerList = it.customerList
                    tvArea.text = it.areaName
                    tvCustomer.text = ""
                }
            }
            R.id.order_online_customer -> {
                // 选择客户
                customerList[options1].let {
                    LogUtil.d("the customer name is ${it.customerName}")
                    customerId = it.customerId
                    tvCustomer.text = it.customerName
                }
            }
            R.id.order_online_units ->{
                // 选择单位
                unitsList[options1].let {
                    LogUtil.d("单位是$it")
                    tvUnits.text = it
                    deliveryUnits = it
                }
            }
        }
    }

    private fun selectDeliveryAt(v: View) {
        timePicker.show(v)
    }

    override fun onTimeSelect(date: Date?, v: View) {
        when (v.id) {
            R.id.order_online_apply_delivery_at -> {
                tvApplyDeliveryAt.text = TimeUtil.getTime(date)
                applyDeliveryAt = TimeUtil.getTime(date, "yyyy-MM-dd")
            }
        }
    }


    private fun selectProtocol() {
        PhotoPicker.builder()
                .setPhotoCount(9).setShowCamera(false)
                .setPreviewEnabled(false).start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PhotoPicker.REQUEST_CODE, PhotoPreview.REQUEST_CODE ->{
                if (resultCode == Activity.RESULT_OK) {
                    val photos: java.util.ArrayList<String>
                    if (data != null) {
                        photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS)
                        for (photo in photos) {
                            LogUtil.d("photo is " + photo)
                        }
                        val mark = timeStamp2Date(System.currentTimeMillis().toString() + "",
                                "yyyyMMdd_HH_mm_ss")
                        protocolFileName = "protocol$mark.zip"
                        // 将文件压缩到本地
                        protocolFile = CompressUtil.zipANDSave(photos, protocolFileName)
                        if (protocolFile == null) {
                            ToastUtil.showShort(currentContext, "文件操作失败，请换部手机")
                            return
                        }
                        ivProtocol.imageResource = R.mipmap.ico_application_authorization_success
                    }
                }
            }
            else -> LogUtil.d("unknown request")
        }
    }

    private fun verifyData() {
        if (tvCategoryName.text.isEmpty()) {
            toast("请选择产品名")
            return
        }
        if (tvProductName.text.isEmpty()) {
            toast("请选择商品名")
            return
        }
        if (tvSpec.text.isEmpty()) {
            toast("请选择规格")
            return
        }
        if (tvDosageForm.text.isEmpty()) {
            toast("请选择剂型")
            return
        }
        if (tvProvince.text.isEmpty()) {
            toast("请选择省")
            return
        }
        if (tvCity.text.isEmpty()) {
            toast("请选择市")
            return
        }
        if (tvArea.text.isEmpty()) {
            toast("请选择县")
            return
        }
        if (tvCustomer.text.isEmpty()) {
            toast("请选择客户")
            return
        }
        if (tvApplyDeliveryAt.text.isEmpty()) {
            toast("请选择发货日期")
            return
        }
        deliveryQty = evDeliveryQty.text.toString().trim()
        if (evDeliveryQty.text.toString().trim().isEmpty()) {
            toast("请填写发货数量")
            return
        }
        //禁止数量为0
        if (evDeliveryQty.text.toString().equals("0")) {
            toast("发货数量禁止为0")
            return
        }
        if (tvUnits.text.isEmpty()) {
            toast("请选择发货单位")
            return
        }
        if (protocolFile == null) {
            toast("请上传产品采购协议")
        }
        remark = evRemark.text.toString()
        if (evRemark.text.toString().trim().isEmpty()) {
            toast("请填写备注")
            return
        }
        consigneeName = evConsigneeName.text.toString()
        if (evConsigneeName.text.toString().trim().isEmpty()) {
            toast("请填写联系人姓名")
            return
        }
        if (!evConsigneeName.text.toString().matches(Regex.name.toRegex())) {
            ToastUtil.showShort(this, "联系人" + getString(R.string.regex_name))
            return
        }
        consigneePhoneNumber = evConsigneePhone.text.toString()
        if (evConsigneePhone.text.toString().trim().isEmpty()) {
            toast("请填写联系电话")
            return
        }
        consigneeAddress = evConsigneeAddress.text.toString()
        if (evConsigneeAddress.text.toString().trim().isEmpty()) {
            toast("请填写收货详细地址")
            return
        }
        bizName = evBizName.text.toString()
        if (evBizName.text.toString().trim().isEmpty()) {
            toast("请填写推广人姓名")
            return
        }
        if (!evBizName.text.toString().matches(Regex.name.toRegex())) {
            ToastUtil.showShort(this, "推广人" + getString(R.string.regex_name))
            return
        }
        bizPhoneNumber = evBizPhone.text.toString()
        if (evBizPhone.text.toString().trim().isEmpty()) {
            toast("请填写推广人电话号码")
            return
        }
        confirmSubmit()
    }

    /**
     * 确认提交
     */
    private fun confirmSubmit() {
        val builder = AlertDialog.Builder(this, R.style.dialog)
        val view = View.inflate(this, R.layout.dialog_confirm_submit, null)
        val content = view.findViewById(R.id.dialog_fillet) as TextView
        content.text = getString(R.string.dialog_confirm_submit_report)
        builder.setView(view)
        val dialog = builder.create()
        view.findViewById(R.id.dialog_check).setOnClickListener { dialog.dismiss() }
        view.findViewById(R.id.dialog_confirm).setOnClickListener {
            dialog.dismiss()
            // 提交数据
            submitData()
        }
        dialog.show()
    }

    private fun submitData() {
        progressDialog = ProgressDialog(this)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCancelable(false)
        progressDialog.setTitle(getString(R.string.upload_progress_dialog_title))
        val responseDialog = ProgressDialog(this)
        responseDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        responseDialog.setMessage(getString(R.string.response_progress_dialog_title))
        responseDialog.setCancelable(false)

        OkHttpUtils.post().url(URL_ORDER_SUBMIT).addHeader(ACCESS_TOKEN, accessToken)
                .addParams(VENDOR_ID, vendorId.toString())
                .addParams(CATEGORY_ID, categoryId.toString())
                .addParams(PRODUCT_ID, productId.toString())
                .addParams(SPEC_ID, specId.toString())
                .addParams(DOSAGE_FORM_ID, dosageFormId.toString())
                .addParams(PROVINCE_ID, provinceId.toString())
                .addParams(CITY_ID, cityId.toString())
                .addParams(AREA_ID, areaId.toString())
                .addParams(CUSTOMER_ID, customerId.toString())
                .addParams(APPLY_DELIVERY_AT, applyDeliveryAt)
                .addParams(DELIVERY_QTY, deliveryQty)
                .addParams(DELIVERY_UNITS, deliveryUnits)
                .addFile(PROTOCOL_FILE, protocolFileName, protocolFile)
                .addParams(REMARK, remark)
                .addParams(CONSIGNEE_NAME, consigneeName)
                .addParams(CONSIGNEE_PHONE_NUMBER, consigneePhoneNumber)
                .addParams(CONSIGNEE_ADDRESS, consigneeAddress)
                .addParams(BIZ_NAME, bizName)
                .addParams(BIZ_PHONE_NUMBER, bizPhoneNumber)
                .build().connTimeOut(10 * 60 * 1000)
                .readTimeOut(10 * 60 * 1000)
                .writeTimeOut(10 * 60 * 1000).execute(object : StringCallback(){

            override fun onBefore(request: Request?, id: Int) {
                super.onBefore(request, id)
                progressDialog.show()
                LogUtil.d("onBefore")
            }

            override fun inProgress(progress: Float, total: Long, id: Int) {
                super.inProgress(progress, total, id)
                progressDialog.progress = (100 * progress + 0.5).toInt()
                LogUtil.d("inProgress 进度是$progress")
                if (progress == 1f) {
                    // 上传完成，等待回传结果
                    progressDialog.dismiss()
                    // 显示等待返回结果dialog
                    responseDialog.show()
                }
            }

            override fun onAfter(id: Int) {
                super.onAfter(id)
                LogUtil.d("onAfter")
                if (responseDialog.isShowing) {
                    responseDialog.dismiss()
                }
            }

            override fun onResponse(response: String, id: Int) {
                LogUtil.d("result is " + response)
                val errorBean = JSON.parseObject(response, ErrorBean::class.java)
                when (errorBean!!.status) {
                    "success" -> {
                        toast(getString(R.string.upload_success))
                        enterOrderOnlineNote()
                    }
                    "failure" -> Util.showError(currentContext, errorBean.reason)
                }
            }

            override fun onError(call: Call?, e: Exception?, id: Int) {
                LogUtil.d("submit failure")
                Util.showTimeOutNotice(currentContext)
            }
        })
    }

    override fun leftOnClick() {
        finish()
    }

    override fun rightOnClick() {

    }
}
