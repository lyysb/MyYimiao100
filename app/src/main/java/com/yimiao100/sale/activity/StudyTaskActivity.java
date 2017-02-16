package com.yimiao100.sale.activity;

import com.yimiao100.sale.base.BaseActivityWithTab;
import com.yimiao100.sale.base.BaseFragmentSingleList;
import com.yimiao100.sale.fragment.StudyAchievementFragment;
import com.yimiao100.sale.fragment.StudyUnAchievedFragment;

import java.util.ArrayList;

/**
 * 学习任务
 */
public class StudyTaskActivity extends BaseActivityWithTab {

    @Override
    protected String title() {
        return "学习任务";
    }

    @Override
    protected ArrayList<BaseFragmentSingleList> setFragments() {
        ArrayList<BaseFragmentSingleList> lists = new ArrayList<>();
        lists.add(new StudyAchievementFragment());
        lists.add(new StudyUnAchievedFragment());
        return lists;
    }

}
