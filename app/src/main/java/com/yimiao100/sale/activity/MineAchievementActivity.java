package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ExamStat;
import com.yimiao100.sale.bean.ExamStatBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;


/**
 * 我的成就
 */
public class MineAchievementActivity extends BaseActivity implements TitleView
        .TitleBarOnClickListener {

    @BindView(R.id.mine_achievement_title)
    TitleView mMineAchievementTitle;
    @BindView(R.id.mine_achievement_photo)
    CircleImageView mMineAchievementPhoto;
    @BindView(R.id.mine_achievement_study_task)
    TextView mMineAchievementStudyTask;
    @BindView(R.id.mine_achievement_note)
    TextView mMineAchievementNote;
    @BindView(R.id.mine_achievement_mine_score)
    TextView mMineAchievementMineScore;
    @BindView(R.id.mine_achievement_mine_collection)
    TextView mMineAchievementMineCollection;
    @BindView(R.id.mine_achievement_name)
    TextView mMineAchievementName;


    private final String URL_EXAM_STAT = Constant.BASE_URL + "/api/course/exam_stat";

    private String mImageUrl;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_achievement);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        mMineAchievementTitle.setOnTitleBarClick(this);
    }

    private void initData() {
        mAccessToken = (String) SharePreferenceUtil.get(this, Constant.ACCESSTOKEN, "");
        mImageUrl = (String) SharePreferenceUtil.get(this, Constant.PROFILEIMAGEURL, "");
        //设置个人头像
        if (!mImageUrl.isEmpty()) {
            Picasso.with(this).load(mImageUrl).placeholder(R.mipmap
                    .ico_my_default_avatar).into(mMineAchievementPhoto);
        }
        //设置用户姓名
        mUserName = (String) SharePreferenceUtil.get(this, Constant.CNNAME, "");
        if (!TextUtils.equals(mUserName, "")) {
            mMineAchievementName.setText(mUserName);
        } else {
            mMineAchievementName.setText("匿名用户");
        }

        //联网加载数据
        OkHttpUtils.post().url(URL_EXAM_STAT).addHeader(ACCESS_TOKEN, mAccessToken).build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.Companion.d("课程考试统计：" + e.getLocalizedMessage());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.Companion.d("课程考试统计：" + response);
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                ExamStat stat = JSON.parseObject(response, ExamStatBean.class)
                                        .getStat();
                                Spanned achievement = Html.fromHtml("<font color=\"#d24141\">"
                                        + stat.getTotal() + "</font>" + "个课时" + "<font " +
                                        "color=\"#d24141\">"
                                        + stat.getUnfinished() + "</font>" + "个未完成任务");
                                mMineAchievementNote.setText(achievement);
                                break;
                            case "failure":
                                Util.showError(currentContext, errorBean.getReason());
                                break;
                        }
                    }
                });
    }

    @OnClick({R.id.mine_achievement_study_task, R.id.mine_achievement_mine_score, R.id
            .mine_achievement_mine_collection})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mine_achievement_study_task:
                //学习任务
                StudyTask();
                break;
            case R.id.mine_achievement_mine_score:
                //我的奖学金
                StudyScore();
                break;
            case R.id.mine_achievement_mine_collection:
                //我的收藏
                MineCollection();
                break;
        }
    }

    /**
     * 学习任务
     */
    private void StudyTask() {
        startActivity(new Intent(this, StudyTaskActivity.class));
    }

    /**
     * 我的奖学金
     * todo 调整
     */
    private void StudyScore() {
        Intent intent = new Intent(this, VendorListForStudyActivity.class);
        intent.putExtra("enter_from", 3);
        startActivity(intent);
    }

    /**
     * 我的收藏
     */
    private void MineCollection() {
        startActivity(new Intent(this, StudyCollectionActivity.class));
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }
}
