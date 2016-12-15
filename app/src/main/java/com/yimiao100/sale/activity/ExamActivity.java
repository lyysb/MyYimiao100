package com.yimiao100.sale.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.peger.ExamAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Course;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.QuestionList;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 考试
 */
public class ExamActivity extends BaseActivity implements ViewPager.OnPageChangeListener,
        ExamAdapter.OnAnswerChooseListener {


    @BindView(R.id.exam_view_pager)
    ViewPager mExamViewPager;
    @BindView(R.id.exam_return)
    ImageView mExamReturn;
    @BindView(R.id.exam_time)
    TextView mExamTime;
    @BindView(R.id.exam_progress)
    TextView mExamProgress;
    @BindView(R.id.exam_submit)
    TextView mExamSubmit;
    @BindView(R.id.exam_left)
    TextView mExamLeft;
    @BindView(R.id.exam_center)
    TextView mExamCenter;
    @BindView(R.id.exam_right)
    TextView mExamRight;
    private ExamAdapter mExamAdapter;
    private ArrayList<QuestionList> mQuestionList;

    private final String URL_SUBMIT_COURSE = Constant.BASE_URL + "/api/course/exam";
    private final String ACCESS_TOKEN = "X-Authorization-Token";
    private final String COURSE_ID = "courseId";
    private final String SCORE = "score";

    private String mAccessToken;
    private int mCourseId;
    private int mScore = 0;


    private Handler handler = new Handler();
    private long mRemainingTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {

    }

    private void initData() {
        mAccessToken = (String) SharePreferenceUtil.get(this, Constant.ACCESSTOKEN, "");
        //获取考试信息
        Course course = getIntent().getParcelableExtra("course");
        //初始化考试信息
        initCourseData(course);
        //初始化问题信息
        initQuestionData(course);
    }

    /**
     * 初始化考试信息
     *
     * @param course
     */
    private void initCourseData(Course course) {
        mCourseId = course.getId();
        mRemainingTime = course.getExamDuration() * 60 * 1000;
        //设置倒计时
        mExamTime.setText(TimeUtil.timeStamp2Date(mRemainingTime + "", "mm:ss"));
        //显示考题进度
        String progress = "1/" + course.getQuestionList().size();
        mExamProgress.setText(progress);
        //初始化底部考试进度
        mExamCenter.setText(progress);

        handler.postDelayed(runnable, 1000);
    }

    /**
     * 初始化问题信息
     *
     * @param course
     */
    private void initQuestionData(Course course) {
        //初始化ViewPager
        mQuestionList = course.getQuestionList();

        mExamAdapter = new ExamAdapter(mQuestionList);
        mExamViewPager.setAdapter(mExamAdapter);
        mExamViewPager.setCurrentItem(0);
        mExamViewPager.addOnPageChangeListener(this);
        //选项点击监听，记录选择，判断对错记录考题
        mExamAdapter.setOnAnswerChooseListener(this);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mRemainingTime >= 1000) {
                //如果还有时间，则刷新时间显示
                mRemainingTime -= 1000;
                mExamTime.setText(TimeUtil.timeStamp2Date(mRemainingTime + "", "mm:ss"));
                handler.postDelayed(this, 1000);
            } else {
                //时间到，计算最终成绩
                finalScore();
                //直接联网提交成绩
                submitScore();
            }
        }
    };

    /**
     * 选择答案
     *
     * @param choose
     * @param position
     */
    @Override
    public void onChoose(int choose, final int position) {
        QuestionList question = mQuestionList.get(position);
        //记录该题已经被回答过
        question.setAnswered(true);
        //记录该题被选中的位置
        question.setChooseAt(choose);
        //记录该题正确与否
        boolean isRight = TextUtils.equals(question.getAnswer(), question.getOptionList().get(choose).getId());
        question.setRight(isRight);

        //自动切换到下一界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (position < mExamAdapter.getCount() - 1) {
                    mExamViewPager.setCurrentItem(position + 1);
                }
            }
        }, 500);
    }


    @OnClick({R.id.exam_return, R.id.exam_submit, R.id.exam_left, R.id.exam_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exam_return:
                //退出直接提示交卷
                submit();
                break;
            case R.id.exam_submit:
                submit();
                break;
            case R.id.exam_left:
                //上一题
                toLast();
                break;
            case R.id.exam_right:
                //下一题
                toNext();
                break;
        }
    }

    /**
     * 上一题
     */
    private void toLast() {
        if (mExamViewPager.getCurrentItem() != 0) {
            mExamViewPager.setCurrentItem(mExamViewPager.getCurrentItem() - 1);
        } else {
            ToastUtil.showShort(this, "当前已经是第一道题");
        }
    }

    /**
     * 下一题
     */
    private void toNext() {
        if (mExamViewPager.getCurrentItem() != (mExamAdapter.getCount() - 1)) {
            mExamViewPager.setCurrentItem(mExamViewPager.getCurrentItem() + 1);
        } else {
            ToastUtil.showShort(this, "当前已经是最后一道题");
        }
    }

    /**
     * 交卷，交卷之前提示是否真正交卷
     */
    private void submit() {
        //提示确认交卷
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("考试未完成，是否现在交卷？");
        builder.setCancelable(false);
        builder.setNegativeButton("不，我还没有做完", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("是的，交卷", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //计算成绩
                finalScore();
                //联网提交成绩
                submitScore();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 提交成绩
     */
    private void submitScore() {
        OkHttpUtils.post().url(URL_SUBMIT_COURSE).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(COURSE_ID, mCourseId + "").addParams(SCORE, mScore + "").build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.d("提交考试成绩E：" + e.getLocalizedMessage());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.d("提交考试成绩：" + response);
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                if (mRemainingTime >= 1000) {
                                    //主动提交的成绩，显示最终成绩
                                    showScore();
                                } else {
                                    //由于时间到提交的成绩，显示最终结果
                                    forceSubmit();
                                }
                                break;
                            case "failure":
                                Util.showError(currentContext, errorBean.getReason());
                                break;
                        }
                    }
                });
    }

    /**
     * 计算最终成绩
     */
    private void finalScore() {
        //计算成绩
        for (QuestionList question : mQuestionList) {
            if (question.isRight()) {
                //如果做对了，累加分数
                mScore += question.getScore();
            }
        }
        LogUtil.d("score:" + mScore);
    }

    /**
     * 时间到，强制交卷
     */
    private void forceSubmit() {
        //显示最终成绩
        AlertDialog.Builder builder = new AlertDialog.Builder(ExamActivity.this);
        builder.setMessage("时间到，您本次的考试成绩为：" + mScore + "分，欢迎您参加本次考试");
        builder.setCancelable(false);
        builder.setPositiveButton("退出本次考试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ExamActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * 计算并显示成绩
     */
    private void showScore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ExamActivity.this);
        builder.setMessage("您最终成绩为：" + mScore + "分，感谢您参与本次考试。");
        builder.setCancelable(false);
        builder.setPositiveButton("退出本次考试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ExamActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN &&
                mRemainingTime > 1000) {
            //如果按下返回键，并且剩余时间大于1000毫秒，询问是否交卷
            submit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //设置当前考试进度
        String progress = position + 1 + "/" + mQuestionList.size();
        mExamProgress.setText(progress);
        mExamCenter.setText(progress);
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
