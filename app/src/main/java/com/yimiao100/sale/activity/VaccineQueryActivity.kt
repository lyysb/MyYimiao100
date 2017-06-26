package com.yimiao100.sale.activity

import android.os.Bundle
import android.widget.TextView
import com.yimiao100.sale.R
import com.yimiao100.sale.base.BaseActivity
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

/**
 * 疫苗查询
 */
class VaccineQueryActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vaccine_query)
        find<TextView>(R.id.vaccine_query_return).setOnClickListener {
            finish()
        }
        find<TextView>(R.id.vaccine_query_warehouse).setOnClickListener {
            startActivity<WareHouseActivity>()
        }
        find<TextView>(R.id.vaccine_query_wholesale).setOnClickListener{
            startActivity<WholesaleActivity>()
        }
    }
}
