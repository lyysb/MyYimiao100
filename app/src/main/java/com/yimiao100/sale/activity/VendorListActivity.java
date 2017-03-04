package com.yimiao100.sale.activity;

import android.text.TextUtils;

import com.yimiao100.sale.base.BaseActivityWithTab;
import com.yimiao100.sale.base.BaseFragmentSingleList;
import com.yimiao100.sale.fragment.VendorCorporateFragment;
import com.yimiao100.sale.fragment.VendorPersonalFragment;

import java.util.ArrayList;

/**
 * 厂家列表
 * Created by 亿苗通 on 2016/10/30.
 */

public class VendorListActivity extends BaseActivityWithTab {

    @Override
    protected String title() {
        String moduleType = getIntent().getStringExtra("moduleType");
        if (TextUtils.equals(moduleType, "balance_order")) {
            return "对账";
        } else {
            return "厂家列表";
        }
    }

    @Override
    protected ArrayList<BaseFragmentSingleList> setFragments() {
        ArrayList<BaseFragmentSingleList> lists = new ArrayList<>();
        lists.add(new VendorCorporateFragment());
        lists.add(new VendorPersonalFragment());
        return lists;
    }
}
