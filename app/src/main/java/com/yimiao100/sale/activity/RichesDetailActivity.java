package com.yimiao100.sale.activity;

import com.yimiao100.sale.base.BaseActivityWithTab;
import com.yimiao100.sale.base.BaseFragmentSingleList;
import com.yimiao100.sale.fragment.RichesDetailCorporateFragment;
import com.yimiao100.sale.fragment.RichesDetailPersonalFragment;

import java.util.ArrayList;

/**
 * 财富-明细列表
 */
public class RichesDetailActivity extends BaseActivityWithTab {

    @Override
    protected String title() {
        return "财富";
    }

    @Override
    protected ArrayList<BaseFragmentSingleList> setFragments() {
        ArrayList<BaseFragmentSingleList> lists = new ArrayList<>();
        lists.add(new RichesDetailCorporateFragment());
        lists.add(new RichesDetailPersonalFragment());
        return lists;
    }
}
