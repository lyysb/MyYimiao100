package com.yimiao100.sale.adapter.peger;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.Option;
import com.yimiao100.sale.bean.QuestionList;

import java.util.ArrayList;

/**
 * 考试页面Adapter
 * Created by 亿苗通 on 2016/10/18.
 */
public class ExamAdapter extends PagerAdapter {


    private final ArrayList<QuestionList> mList;
    private OnAnswerChooseListener mListener;

    public ExamAdapter(ArrayList<QuestionList> questionList) {
        mList = questionList;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        QuestionList question = mList.get(position);
        //根据position获取该页的选项列表集合
        View view = View.inflate(container.getContext(), R.layout.pager_exam, null);
        //显示考试问题
        TextView questionDesc = (TextView) view.findViewById(R.id.exam_question);
        questionDesc.setText(question.getTitle());

        //显示所有选项
        RadioGroup answers = (RadioGroup) view.findViewById(R.id.exam_answer_group);

        //获得所有选项
        ArrayList<Option> optionList = question.getOptionList();

        //是否已经回答过
        boolean answered = question.isAnswered();
        //选中位置-没回答过不做任何操作
        int chooseAt = question.getChooseAt();

        for (int i = 0; i < optionList.size(); i++) {
            RadioButton radioButton = new RadioButton(container.getContext());
            //设置选项内容
            radioButton.setText(optionList.get(i).getDesc());
            //设置字体大小
            radioButton.setTextSize(14);
            //设置边距
            radioButton.setPadding(5, 5, 5, 5);
            //设置文字颜色
            radioButton.setTextColor(
                    container.getContext().getResources().getColorStateList(R.color.change_error));

            //给RadioGroup设置Id
            radioButton.setId(i);

            //选项点击监听
            final int choose = i;
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onChoose(choose, position);
                    }
                }
            });
            answers.addView(radioButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (answered) {
            //如果已经回答过了，设置默认选中
            answers.check(chooseAt);
        }
        container.addView(view);
        return view;
    }

    public void setOnAnswerChooseListener(OnAnswerChooseListener listener) {
        mListener = listener;
    }

    public interface OnAnswerChooseListener {
        void onChoose(int choose, int position);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
}

