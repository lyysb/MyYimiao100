package com.yimiao100.sale.activity

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.KeyEvent
import android.widget.LinearLayout
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.adapter.peger.MainPagerAdapter
import com.yimiao100.sale.base.BaseActivity
import com.yimiao100.sale.base.BaseFragment
import com.yimiao100.sale.bean.ErrorBean
import com.yimiao100.sale.bean.UserFundBean
import com.yimiao100.sale.ext.JSON
import com.yimiao100.sale.fragment.*
import com.yimiao100.sale.utils.*
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.jetbrains.anko.find


/**
 * 主界面
 */
class MainActivity : BaseActivity(), ViewPager.OnPageChangeListener {


    lateinit var mTabHost: FragmentTabHost
    lateinit var mViewPager: ViewPager

    private var exitTime: Long = 0

    //用户资金URL
    private val URL_USER_FUND = Constant.BASE_URL + "/api/fund/user_fund"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        initData()
    }

    private fun initView() {
        initViewPager()
        initTabs()
    }

    private fun initData() {
        initFundInformation()

        // 刚进入应用，获取广告弹窗显示
        obtainAdDialog()
    }

    private fun initViewPager() {
        mViewPager = find(R.id.main_view_pager)
        val fragments = ArrayList<BaseFragment>()
        fragments.add(InformationFragment())
        fragments.add(CRMFragment())
        fragments.add(StudyFragment())
        fragments.add(MineFragment())
        mViewPager.adapter = MainPagerAdapter(supportFragmentManager, fragments)
        mViewPager.addOnPageChangeListener(this)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        mTabHost.currentTab = position
    }
    /**
     * 设置底部导航按钮
     */
    private fun initTabs() {
        mTabHost = find(R.id.tabhost)
        mTabHost.setup(this, supportFragmentManager, R.id.main_view_pager)
        //去除分隔线
        mTabHost.tabWidget.showDividers = LinearLayout.SHOW_DIVIDER_NONE

        addTabToTabHost("资讯", R.drawable.selector_home_information, InformationFragment::class.java, null)
        addTabToTabHost("商务", R.drawable.selector_home_crm, CRMFragment::class.java, null)
        addTabToTabHost("学习", R.drawable.selector_home_study, StudyFragment::class.java, null)
        addTabToTabHost("我的", R.drawable.selector_home_mine, MineFragment::class.java, null)

        mTabHost.setOnTabChangedListener {
            mTabHost.currentTab.let { it -> mViewPager.setCurrentItem(it, false) }
        }
    }

    /**
     * 具体实现导航按钮的添加

     * @param tabName
     * *
     * @param tabIcon
     * *
     * @param fragmentClass
     * *
     * @param args
     */
    private fun addTabToTabHost(tabName: String, tabIcon: Int, fragmentClass: Class<*>, args: Bundle?) {
        //创建一个TextView
        val tabView = TextView(this)
        tabView.textSize = 12f
        tabView.text = tabName
        tabView.gravity = Gravity.CENTER
        tabView.setPadding(0, DensityUtil.dp2px(this, 5f), 0, DensityUtil.dp2px(this, 2f))
        tabView.setTextColor(resources.getColorStateList(R.color.viewpage_selector_slide_title))
        tabView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcon, 0, 0)

        //创建一个Tab
        val tab = mTabHost.newTabSpec(fragmentClass.canonicalName)
        tab.setIndicator(tabView)          // 指定tab显示的内容就是一个TextView

        //把tab与tab对应的Fragment添加到FragmentTabHost中
        mTabHost.addTab(tab, fragmentClass, args)
    }

    /**
     * 获取用户资金信息
     */
    private fun initFundInformation() {
        OkHttpUtils.post().url(URL_USER_FUND).addHeader(ACCESS_TOKEN, accessToken)
                .build().execute(object : StringCallback() {
            override fun onError(call: Call, e: Exception, id: Int) {
                LogUtil.d("获取用户资金信息E：" + e.localizedMessage)
            }

            override fun onResponse(response: String, id: Int) {
                LogUtil.d("获取用户资金信息：" + response)
                val errorBean = JSON.parseObject(response, ErrorBean::class.java)
                when (errorBean!!.status) {
                    "success" -> {
                        val userFundBean = JSON.parseObject(response, UserFundBean::class.java)
                        if (userFundBean!!.userFund != null) {
                            val userFund = userFundBean.userFund
                            //用户账户总金额-double
                            SharePreferenceUtil.put(applicationContext, Constant.TOTAL_AMOUNT,
                                    userFund.totalAmount)
                            //用户积分-int
                            SharePreferenceUtil.put(applicationContext, Constant.INTEGRAL,
                                    userFund.integral)
                            //用户奖学金-double
                            SharePreferenceUtil.put(applicationContext, Constant.TOTAL_EXAM_REWARD,
                                    userFund.totalExamReward)
                            //用户推广费-double
                            SharePreferenceUtil.put(applicationContext, Constant.TOTAL_SALE,
                                    userFund.totalSale)
                            //用户保证金-double
                            SharePreferenceUtil.put(applicationContext, Constant.DEPOSIT,
                                    userFund.deposit)
                        }
                    }
                    "failure" -> Util.showError(this@MainActivity, errorBean.reason)
                }
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            //System.currentTimeMillis()无论何时调用，肯定大于2000
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtil.showShort(applicationContext, "再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
                System.exit(0)
            }

            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
