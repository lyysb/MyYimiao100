package com.yimiao100.sale.adapter.expendable;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.CourseExam;
import com.yimiao100.sale.bean.ExamInfo;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.ViewHolderUtil;
import com.yimiao100.sale.view.HorizontalProgressBar;

import java.util.ArrayList;

/**
 * 学习成绩详情Adapter
 * Created by 亿苗通 on 2016/10/26.
 */

public class ScoreDetailAdapter extends BaseExpandableListAdapter {

    private final ArrayList<ExamInfo> mStatList;

    public ScoreDetailAdapter(ArrayList<ExamInfo> statList) {
        mStatList = statList;
    }

    @Override
    public int getGroupCount() {
        return mStatList != null ? mStatList.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mStatList.get(groupPosition) != null ? mStatList.get(groupPosition).getCourseExamList().size() : 0;
    }

    @Override
    public ExamInfo getGroup(int groupPosition) {
        return mStatList != null ? mStatList.get(groupPosition) : null;
    }

    @Override
    public CourseExam getChild(int groupPosition, int childPosition) {
        return mStatList.get(groupPosition) != null ? mStatList.get(groupPosition).getCourseExamList().get(childPosition) : null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup
            parent) {
        ExamInfo examInfo = getGroup(groupPosition);
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.group_study_score, null);
        }
        TextView productName = ViewHolderUtil.get(convertView, R.id.score_detail_product_name);
        TextView categoryName = ViewHolderUtil.get(convertView, R.id.score_detail_category_name);
        TextView dosageForm = ViewHolderUtil.get(convertView, R.id.score_detail_dosage_form);
        TextView spec = ViewHolderUtil.get(convertView, R.id.score_detail_spec);
        HorizontalProgressBar avgScoreProgress = ViewHolderUtil.get(convertView, R.id.score_detail_avg_score_progress);
        HorizontalProgressBar shipProgress = ViewHolderUtil.get(convertView, R.id.score_detail_ship_progress);
        TextView bounds = ViewHolderUtil.get(convertView, R.id.score_detail_bounds);
        ImageView status = ViewHolderUtil.get(convertView, R.id.score_detail_expend_status);

        productName.setText(examInfo.getProductName());
        categoryName.setText(examInfo.getCategoryName());
        dosageForm.setText("剂型：" + examInfo.getDosageForm());
        spec.setText("规格：" + examInfo.getSpec());

        avgScoreProgress.setText(examInfo.getAvgScore() + "分");
        avgScoreProgress.setProgress((int) examInfo.getAvgScore());

        shipProgress.setMax(examInfo.getTargetQty());
        shipProgress.setProgress(examInfo.getTotalQty());
        shipProgress.setText(examInfo.getTotalQty() + "");

        bounds.setText("奖学金：￥" + FormatUtils.MoneyFormat(examInfo.getTotalAmount()) + "元");


        if (isExpanded) {
            //设置箭头向上
            status.setImageResource(R.mipmap.ico_my_grades_close);
        } else {
            //设置箭头向下
            status.setImageResource(R.mipmap.ico_my_grades_open);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {
        CourseExam courseExam = getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView =View.inflate(parent.getContext(), R.layout.child_study_score, null);
        }
        TextView courseName = ViewHolderUtil.get(convertView, R.id.score_detail_course_name);
        TextView score = ViewHolderUtil.get(convertView, R.id.score_detail_score);

        courseName.setText(courseExam.getCourseName());
        score.setText(courseExam.getScore() + "分");

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
