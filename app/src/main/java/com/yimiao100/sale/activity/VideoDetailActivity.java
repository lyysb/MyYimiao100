package com.yimiao100.sale.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Course;
import com.yimiao100.sale.bean.CourseBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TextViewExpandableAnimation;
import com.yimiao100.sale.view.TitleView;
import com.yimiao100.sale.view.YMVideoPlayer;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.Call;


/**
 * 课程详情
 */
public class VideoDetailActivity extends BaseActivity implements YMVideoPlayer
        .OnCompleteListener, TitleView.TitleBarOnClickListener, YMVideoPlayer.OnPlayListener {

    @BindView(R.id.video_detail_title)
    TitleView mVideoDetailTitle;
    @BindView(R.id.video_detail_counts)
    TextView mVideoDetailCounts;
    @BindView(R.id.video_detail_cast)
    TextView mVideoDetailCast;
    @BindView(R.id.video_detail_play_now)
    ImageView mVideoDetailPlayNow;
    @BindView(R.id.video_detail_player)
    YMVideoPlayer mVideoDetailPlayer;
    @BindView(R.id.video_detail_class_name)
    TextView mVideoDetailClassName;
    @BindView(R.id.video_detail_collection)
    TextView mVideoDetailCollection;
    @BindView(R.id.video_detail_description)
    TextViewExpandableAnimation mVideoDetailDescription;
    @BindView(R.id.video_detail_for)
    TextView mVideoDetailFor;
    @BindView(R.id.video_detail_time)
    TextView mVideoDetailTime;
    @BindView(R.id.video_detail_score)
    TextView mVideoDetailScore;
    @BindView(R.id.video_detail_notice)
    TextView mVideoDetailNotice;
    private AlertDialog mNoticeDialog;

    private final String URL_COURSE_INFO = Constant.BASE_URL + "/api/course/info";
    private final String COURSE_ID = "courseId";
    private final String URL_COLLECTION = Constant.BASE_URL + "/api/course/add_collection";
    private final String URL_CANCEL_COLLECTION = Constant.BASE_URL +
            "/api/course/cancel_collection";
    private final String URL_COURSE_PLAY = Constant.BASE_URL + "/api/course/play";
    private final String URL_INTEGRAL_CALCULATE = Constant.BASE_URL + "/api/integral/calculate";
    private final String OBJECT_ID = "objectId";
    private final String OBJECT_TYPE = "objectType";

    private int mCourseId;
    private Course mCourse;
    private final String mObjectType = "course";
    private int mIntegral;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        ButterKnife.bind(this);
        mIntegral = (int) SharePreferenceUtil.get(this, Constant.INTEGRAL, -1);

        initView();

        initData();
    }

    private void initView() {
        mVideoDetailTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        //联网请求数据，显示课程详情
        mCourseId = getIntent().getIntExtra("courseId", -1);
        OkHttpUtils.post().url(URL_COURSE_INFO).addHeader(ACCESS_TOKEN, mAccessToken).addParams
                (COURSE_ID, mCourseId + "").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("课程详情E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("课程详情：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mCourse = JSON.parseObject(response, CourseBean.class).getCourse();
                        //初始化视频接口
                        initVideoData(mCourse);
                        //初始化课程部分
                        initCourse(mCourse);
                        break;
                    case "failure":
                        Util.showError(VideoDetailActivity.this, errorBean.getReason());
                        break;
                }
            }
        });

    }

    /**
     * 设置视频播放数据
     *
     * @param course
     */
    private void initVideoData(Course course) {
        if (course.getVideoUrl() != null) {
            LogUtil.d("视频链接：" + course.getVideoUrl());
        }
        mVideoDetailPlayer.setUp(course.getVideoUrl() != null ? course.getVideoUrl() :
                        "http://oduhua0b1.bkt.clouddn.com/default_video.mp4",
                JCVideoPlayerStandard.SCREEN_LAYOUT_LIST, course.getCourseName());
        Picasso.with(this).load(course.getVideoUrl() + "?vframe/png/offset/10")
                .into(mVideoDetailPlayer.thumbImageView);
        mVideoDetailPlayer.setOnCompleteListener(this);
        mVideoDetailPlayer.setOnPlayListener(this);
    }

    /**
     * 设置课程描述
     *
     * @param course
     */
    private void initCourse(Course course) {
        switch (mCourse.getCourseType()) {
            case "exam":
                //如果是考试类型的课程
                mVideoDetailTitle.setTitle("推广/考试");
                // 隐藏收藏按钮
                mVideoDetailCollection.setVisibility(View.GONE);
                //显示提示
                mVideoDetailNotice.setVisibility(View.VISIBLE);
                //考试课程，读取是否弹窗提示
                boolean isNotice = (boolean) SharePreferenceUtil.get(this, Constant
                        .EXAM_IS_NOTICE, true);
                //读取是否考过
                int examStatus = mCourse.getExamStatus();
                if (isNotice || examStatus != 1) {
                    //提示
                    showNoticeDialog();
                }
                break;
            case "open":
                //如果是公开课
                mVideoDetailTitle.setTitle("公开课");
                mVideoDetailCollection.setVisibility(View.VISIBLE);
                mVideoDetailNotice.setVisibility(View.GONE);
                break;
        }
        //课程名称
        mVideoDetailClassName.setText(course.getCourseName());
        //是否收藏
        if (course.getCollectionStatus() == 1) {
            //已收藏
            mVideoDetailCollection.setCompoundDrawablesWithIntrinsicBounds(getResources()
                    .getDrawable(R.mipmap.ico_study_collection_activation), null, null, null);
        } else {
            //未收藏
            mVideoDetailCollection.setCompoundDrawablesWithIntrinsicBounds(getResources()
                    .getDrawable(R.mipmap.ico_study_collection), null, null, null);
        }

        //课程概述
        mVideoDetailDescription.setText(course.getCourseDesc());
        //适合人群
        mVideoDetailFor.setText(course.getSuitCrowds());
        //课程时间
        mVideoDetailTime.setText(TimeUtil.timeStamp2Date(course.getStartAt() + "", "yyyy年MM月dd日")
                + "-" + TimeUtil.timeStamp2Date(course.getEndAt() + "", "yyyy年MM月dd日"));
        //考试分数
        mVideoDetailScore.setVisibility(course.getExamStatus() == 1 ? View.VISIBLE : View
                .INVISIBLE);
        mVideoDetailScore.setText(course.getExamScore() + "分");
        //播放次数
        mVideoDetailCounts.setText(course.getPlayCount() + "次");
        //播放花费
        switch (course.getIntegralType()) {
            case "increase":
                mVideoDetailCast.setText("+" + course.getIntegralValue() + "会员积分");
                break;
            case "decrease":
                mVideoDetailCast.setText("-" + course.getIntegralValue() + "会员积分");
                break;
            case "free":
                mVideoDetailCast.setText("免费");
                break;
        }
    }

    /**
     * 考试课程根据设置，弹出Dialog
     */
    private void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoDetailActivity.this, R.style
                .dialog);
        View dialogView = View.inflate(VideoDetailActivity.this, R.layout.dialog_exam, null);
        dialogView.findViewById(R.id.dialog_exam_confirm).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                mNoticeDialog.dismiss();
            }
        });
        CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.dialog_exam_radio);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //记录是否被选中，选中-不提示，不选中-提示
                SharePreferenceUtil.put(getApplicationContext(), Constant.EXAM_IS_NOTICE,
                        !isChecked);
            }
        });
        builder.setView(dialogView);
        mNoticeDialog = builder.create();
        mNoticeDialog.show();
    }

    @OnClick({R.id.video_detail_play_now, R.id.video_detail_collection})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video_detail_play_now:
                //播放视频
                mVideoDetailPlayer.prepareVideo();
                onPlay();
                break;
            case R.id.video_detail_collection:
                collection(mCourse);   //收藏课程
                break;
        }
    }

    /**
     * 收藏
     *
     * @param course
     */
    private void collection(final Course course) {
        //如果已经收藏，则请求网络取消收藏；如果没有收藏，则收藏课程
        OkHttpUtils.post().url(course.getCollectionStatus() == 1 ? URL_CANCEL_COLLECTION :
                URL_COLLECTION).addHeader(ACCESS_TOKEN, mAccessToken).addParams(COURSE_ID,
                mCourseId + "").build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("收藏课程E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("收藏课程：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        if (course.getCollectionStatus() == 1) {
                            //以前是已收藏，现在改为未收藏
                            course.setCollectionStatus(0);
                            mVideoDetailCollection.setCompoundDrawablesWithIntrinsicBounds
                                    (getResources()
                                            .getDrawable(R.mipmap.ico_study_collection), null,
                                            null, null);
                            ToastUtil.showShort(getApplicationContext(), "取消收藏成功");
                        } else {
                            //以前是未收藏，现在改为收藏
                            course.setCollectionStatus(1);
                            mVideoDetailCollection.setCompoundDrawablesWithIntrinsicBounds
                                    (getResources()
                                                    .getDrawable(R.mipmap
                                                            .ico_study_collection_activation), null,
                                            null, null);
                            ToastUtil.showShort(getApplicationContext(), "收藏成功");
                        }

                        break;
                    case "failure":
                        Util.showError(VideoDetailActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 视频播放完成，进入考试界面
     */
    @Override
    public void onComplete() {
        //如果是考试类型的课程&&没有参与过考试
        if (TextUtils.equals(mCourse.getCourseType(), "exam") && mCourse.getExamStatus() != 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("视频播放完成，是否直接进入考试？");
            builder.setCancelable(false);
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //开始考试
                    startExam(mCourse);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /**
     * 开始考试
     *
     * @param course
     */
    private void startExam(Course course) {
        Intent intent = new Intent(VideoDetailActivity.this, ExamActivity.class);
        intent.putExtra("course", course);
        startActivity(intent);
        finish();
    }

    /**
     * 播放视频，提交播放
     */
    @Override
    public void onPlay() {
        //判断是否已经参与过积分计算
        //-true，增加播放次数
        //-false，判断课程类型
        //-increase，积分计算&增加播放次数
        //decrease，判断积分是否足够扣除
        //-足够，积分计算&增加播放次数
        //-不足，提示积分不足，禁止播放
        //-free，正常播放，增加播放次数

        //判断是否已经参与过积分计算
        if (mCourse.getIntegralStatus() == 1) {
            //参与过积分计算，增加播放次数
            normalPlay();
        } else {
            //判断课程类型
            switch (mCourse.getIntegralType()) {
                case "increase":
                    //积分计算-增加积分
                    calculateIntegral();
                    //增加播放次数
                    normalPlay();
                    break;
                case "decrease":
                    //判断积分是否足够扣除
                    if (mIntegral >= mCourse.getIntegralValue()) {
                        //-足够，积分计算&增加播放次数
                        //积分计算-扣除积分
                        calculateIntegral();
                        //增加播放次数
                        normalPlay();
                    } else {
                        //禁止播放
                        forbidPlay();
                    }
                    break;
                case "free":
                    //-free，增加播放次数
                    normalPlay();
                    break;
            }
        }

    }

    /**
     * 计算积分
     */
    private void calculateIntegral() {
        OkHttpUtils.post().url(URL_INTEGRAL_CALCULATE).addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams(OBJECT_ID, mCourseId + "").addParams(OBJECT_TYPE, mObjectType)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("积分计算E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("积分计算：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //本地同步积分，提示积分变动
                        switch (mCourse.getIntegralType()) {
                            case "increase":
                                mIntegral += mCourse.getIntegralValue();
                                ToastUtil.showShort(getApplicationContext(), "成功增加" + mCourse
                                        .getIntegralValue() + "积分");
                                break;
                            case "decrease":
                                mIntegral -= mCourse.getIntegralValue();
                                ToastUtil.showShort(getApplicationContext(), "成功扣除" + mCourse
                                        .getIntegralValue() + "积分");
                                break;
                        }
                        SharePreferenceUtil.put(getApplicationContext(), Constant.INTEGRAL,
                                mIntegral);
                        break;
                    case "failure":
                        Util.showError(VideoDetailActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 增加播放次数
     */
    private void normalPlay() {
        OkHttpUtils.post().url(URL_COURSE_PLAY).addHeader(ACCESS_TOKEN, mAccessToken).addParams
                (COURSE_ID, mCourseId + "").build().execute(new StringCallback() {


            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("增加播放次数E：" + e.getLocalizedMessage());
                e.printStackTrace();
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("增加播放次数：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        mVideoDetailCounts.setText((mCourse.getPlayCount() + 1) + "次");
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 禁止播放
     */
    private void forbidPlay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您的积分余额不足，请完成积分任务后再来观看此视频。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        mVideoDetailPlayer.release();
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
