package com.yimiao100.sale.activity;

import com.yimiao100.sale.base.BaseActivityWithTab;
import com.yimiao100.sale.base.BaseFragmentSingleList;
import com.yimiao100.sale.fragment.OrderCorporateFragment;
import com.yimiao100.sale.fragment.OrderPersonalFragment;

import java.util.ArrayList;

/**
 * 我的业务-业务列表
 */
public class OrderActivity extends BaseActivityWithTab {


    @Override
    protected String title() {
        return "我的业务";
    }

    @Override
    protected ArrayList<BaseFragmentSingleList> setFragments() {
        ArrayList<BaseFragmentSingleList> lists = new ArrayList<>();
        lists.add(new OrderCorporateFragment());
        lists.add(new OrderPersonalFragment());
        return lists;
    }
}
