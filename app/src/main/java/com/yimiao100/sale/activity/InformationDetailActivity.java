package com.yimiao100.sale.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.CommentAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.CommentBean;
import com.yimiao100.sale.bean.CommentListBean;
import com.yimiao100.sale.bean.CommentResultBean;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.InformationDetailBean;
import com.yimiao100.sale.bean.NewsBean;
import com.yimiao100.sale.bean.TagListBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.CommentPopupWindow;
import com.yimiao100.sale.view.Html5WebView;
import com.yimiao100.sale.view.JavascriptInterface;
import com.yimiao100.sale.view.PullToRefreshListView;
import com.yimiao100.sale.view.TitleView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gujun.android.taggroup.TagGroup;
import okhttp3.Call;

/**
 * 资讯-详情
 */
public class InformationDetailActivity extends BaseActivity implements View.OnClickListener,
        CommentAdapter.OnScoreClickListener, CommentPopupWindow.OnPopupWindowClickListener,
        TitleView.TitleBarOnClickListener, PullToRefreshListView.OnRefreshingListener, TagGroup
                .OnTagClickListener {
    /**
     * 资讯详情URL
     */
    private final String DETAIL_URL = Constant.BASE_URL + "/api/news/detail";
    /**
     * 资讯内容部分URL
     */
    private final String CONTENT_URL = Constant.BASE_URL + "/api/news/content";
    /**
     * 资讯评论列表URL
     */
    private final String NEWS_COMMENT_LIST = Constant.BASE_URL + "/api/comment/news_comment_list";

    /**
     * 提交资讯评分URL
     */
    private final String POST_SCORE = Constant.BASE_URL + "/api/news/post_score";

    /**
     * 提交评论评分
     */
    private final String POST_COMMENT_SCORE = Constant.BASE_URL + "/api/comment/post_comment_score";

    /**
     * 发表评论
     */
    private final String POST_NEWS_COMMENT = Constant.BASE_URL + "/api/comment/post_news_comment";
    /**
     * 收藏资讯
     */
    private final String ADD_COLLECTION = Constant.BASE_URL + "/api/news/add_collection";
    @BindView(R.id.information_detail_title)
    TitleView mInformationDetailTitle;
    @BindView(R.id.information_detail_unread)
    ImageView mInformationDetailUnread;

    private PullToRefreshListView mListView;
    private int mNewsId;
    private Html5WebView mWebView;
    private TextView mInformationDetailTitle1;
    private TextView mInformationDetailFrom;
    private TextView mInformationDetailTime;
    private TextView mInformationDetailScore;
    private TextView mInformationDetailType;
    private TagGroup mInformationTagGroup;
    private TextView mInformationDetailCheck;
    private int mUserId;
    private List<CommentListBean> mCommentList;
    private TextView mCommentUserScore;

    private CommentPopupWindow mCommentPopupWindow;
    private CommentAdapter mCommentAdapter;
    private ImageView mInformationDetailCollection;
    private int mIntegral;

    private String mImageUrl;
    private ImageView mInformationCircleOfFriends;
    private ImageView mInformationQqZone;
    private ImageView mInformationSina;
    private Drawable mActivationScore;
    private Drawable mDefaultScore;
    private NewsBean mNews;
    private TextView mInformationDetailRemarks;
    private View mHeadView;
    private LinearLayout mLayout;
    private int mScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        //获取资讯id
        mNewsId = intent.getIntExtra("newsId", -1);
        LogUtil.Companion.d("newsId：" + mNewsId);
        //获取用户id
        mUserId = (int) SharePreferenceUtil.get(this, Constant.USERID, -1);
        //获取用户当前积分
        mIntegral = (int) SharePreferenceUtil.get(this, Constant.INTEGRAL, -1);

        initView();

        initData();

    }

    private void initView() {
        initTitleView();

        //评论列表
        mListView = (PullToRefreshListView) findViewById(R.id.information_detail_comments);
        //分享按钮
        ImageView information_detail_share1 = (ImageView) findViewById(R.id
                .information_detail_share1);
        information_detail_share1.setOnClickListener(this);

        initHeadView();

        initBottomView();


        mListView.addHeaderView(mHeadView);
        mListView.setOnRefreshingListener(this);
    }

    private void initTitleView() {
        mInformationDetailTitle.setOnTitleBarClick(this);
    }

    /**
     * 网页和标题
     */
    private void initHeadView() {
        //评论列表上部布局
        mHeadView = View.inflate(this, R.layout.head_information_detail, null);
        mInformationDetailTitle1 = (TextView) mHeadView.findViewById(R.id
                .information_detail_title1);    //标题
        mInformationDetailType = (TextView) mHeadView.findViewById(R.id
                .information_detail_type);       //来源
        mInformationDetailFrom = (TextView) mHeadView.findViewById(R.id
                .information_detail_from);        //来源
        mInformationDetailTime = (TextView) mHeadView.findViewById(R.id
                .information_detail_time);        //时间
        //资讯网页内容
        mLayout = (LinearLayout) mHeadView.findViewById(R.id.information_detail_content);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
        .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mWebView = new Html5WebView(this);
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);
        //本地不处理外部链接
        mWebView.shouldOverrideUrlLoading = false;

        //载入js--可以点击显示图片，进行放大处理
        mWebView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");
        //超链接点击处理
        mWebView.addJavascriptInterface(new JavascriptInterface(this), "hreflistner");

        //标签
        mInformationTagGroup = (TagGroup) mHeadView.findViewById(R.id
                .information_tag_group);            //Tag
        mInformationDetailScore = (TextView) mHeadView.findViewById(R.id
                .information_detail_score);      //点赞
        mInformationDetailCheck = (TextView) mHeadView.findViewById(R.id
                .information_detail_check);      //纠错
        mInformationDetailRemarks = (TextView) mHeadView.findViewById(R.id
                .information_detail_remarks);   //加积分（根据是否需要加积分控制显示）

        //朋友圈、空间、微博分享
        mInformationCircleOfFriends = (ImageView) mHeadView.findViewById(R.id
                .information_circle_of_friends);
        mInformationCircleOfFriends.setOnClickListener(this);
        mInformationQqZone = (ImageView) mHeadView.findViewById(R.id.information_qq_zone);
        mInformationQqZone.setOnClickListener(this);
        mInformationSina = (ImageView) mHeadView.findViewById(R.id.information_sina);
        mInformationSina.setOnClickListener(this);

        mInformationDetailScore.setOnClickListener(this);
        mInformationDetailCheck.setOnClickListener(this);
    }

    private void initBottomView() {
        //底部评论
        TextView information_write_comment = (TextView) findViewById(R.id
                .information_write_comment);
        ImageView information_detail_comment2 = (ImageView) findViewById(R.id
                .information_detail_comment2);
        information_write_comment.setOnClickListener(this);
        //写评论
        information_detail_comment2.setOnClickListener(this);
        //底部收藏按钮
        mInformationDetailCollection = (ImageView) findViewById(R.id.information_detail_collection);
        mInformationDetailCollection.setOnClickListener(this);

        //分享按钮
        ImageView information_detail_share = (ImageView) findViewById(R.id
                .information_detail_share);
        information_detail_share.setOnClickListener(this);

        //已赞图片
        mActivationScore = getResources().getDrawable(R.mipmap.ico_information_activation_zambia);
        //未赞图片
        mDefaultScore = getResources().getDrawable(R.mipmap.ico_information_default_zambia);

    }

    private void initData() {
        //获取资讯详情显示
        showInformationDetail();

        //WebView获取资讯内容--GET请求方式
//        showInformationContent();

        //获取评论列表
        showInformationComment();
    }

    /**
     * 显示资讯详情
     */
    private void showInformationDetail() {
        OkHttpUtils.post().url(DETAIL_URL)
                .addParams("newsId", mNewsId + "")
                .addParams("userId", mUserId + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("资讯详情E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("资讯详情：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //解析json，设置显示
                        mNews = JSON.parseObject(response, InformationDetailBean.class)
                                .getNews();
                        //设置新闻细节
                        setNewsDetail(mNews);
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 设置新闻细节
     *
     * @param news
     */
    private void setNewsDetail(NewsBean news) {
        //设置标题
        mInformationDetailTitle1.setText(news.getTitle());
        //根据来源设置图标
        int newsType = news.getNewsType();
        String newsTypeName = news.getNewsTypeName();
        switch (newsType) {
            case 1:
                //摘录
                mInformationDetailType.setBackgroundResource(R.drawable.shape_information_excerpt);
                mInformationDetailType.setTextColor(Color.parseColor("#4188d2"));
                break;
            case 2:
                //原创
                mInformationDetailType.setBackgroundResource(R.drawable.shape_information_original);
                mInformationDetailType.setTextColor(Color.parseColor("#eb6100"));
                break;
            case 3:
                //广告
                mInformationDetailType.setBackgroundResource(R.drawable
                        .shape_information_advertisement);
                mInformationDetailType.setTextColor(Color.parseColor("#22ac38"));
                break;
            case 4:
                //转发
                mInformationDetailType.setBackgroundResource(R.drawable.shape_information_forward);
                mInformationDetailType.setTextColor(Color.parseColor("#004986"));
                break;
        }
        mInformationDetailType.setText(newsTypeName);
        //来源
        String newsFrom = news.getNewsSource();
        mInformationDetailFrom.setText(newsFrom);
        //发布时间
        mInformationDetailTime.setText(TimeUtil.timeStamp2Date(news.getPublishAt() + "", "MM月dd日 " +
                "HH:mm"));
        //设置标签
        List<TagListBean> tagList = news.getTagList();
        List<String> TagNameList = new ArrayList<>();
        for (TagListBean tagListBean : tagList) {
            TagNameList.add(tagListBean.getTagName());
        }
        //获取所有的标签名字
        mInformationTagGroup.setTags(TagNameList);
        //标签点击事件
        mInformationTagGroup.setOnTagClickListener(this);

        //设置点赞数量
        mScore = news.getScore();
        mInformationDetailScore.setText(mScore + "人点赞");
        //根据用户收藏状态，设置收藏图标显示
        if (news.getUserCollectionStatus() == 0) {
            mInformationDetailCollection.setImageResource(R.mipmap
                    .ico_information_default_collection);
        } else {
            mInformationDetailCollection.setImageResource(R.mipmap
                    .ico_information_activation_collection);
        }
        //根据积分类型，设置积分显示
        switch (mNews.getIntegralType()) {
            case "increase":
                //送积分的话，设置分享备注显示，并且显示送多少积分
                mInformationDetailRemarks.setVisibility(View.VISIBLE);
                mInformationDetailRemarks.setText("分享此文章，+" + mNews.getIntegralValue() + "会员积分。");
                break;
            case "free":
                //免费的话，设置分享备注不显示
                mInformationDetailRemarks.setVisibility(View.GONE);
                break;
        }
        //获取分享链接
        if (mNews.getImageList().isEmpty() || mNews.getImageList().get(0).getImageUrl().isEmpty()) {
            mImageUrl = Constant.DEFAULT_IMAGE;
        } else {
            mImageUrl = mNews.getImageList().get(0).getImageUrl();
        }
        //-富文本方式显示资讯内容
        showInformationContent(mNews.getNewsContent());
    }

    /**
     * WebView显示资讯详情内容--富文本
     */
    private void showInformationContent(String newsContent) {
        LogUtil.Companion.d("newsContent:\n" + newsContent);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //自适应屏幕
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setUseWideViewPort(true);
        mWebView.loadData(newsContent, "text/html; charset=UTF-8", null);
//        mWebView.loadDataWithBaseURL(null, newsContent, "text/html; charset=UTF-8", null, null);
    }

    /**
     * WebView显示资讯详情内容--GET请求--废弃
     */
    private void showInformationContent() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //自适应屏幕
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setUseWideViewPort(true);
        mWebView.loadUrl(CONTENT_URL + "?newsId=" + mNewsId);
    }


    /**
     * 显示资讯评论
     */
    private void showInformationComment() {
        OkHttpUtils.post().url(NEWS_COMMENT_LIST)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams("objectId", mNewsId + "")
                .addParams("userId", mUserId + "")
                .addParams(PAGE, "1")
                .addParams(PAGE_SIZE, "10")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("评论列表E：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("评论列表：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        CommentResultBean commentResult = JSON.parseObject(response,
                                CommentBean.class).getCommentResult();
                        mCommentList = commentResult.getCommentList();
                        if (mCommentList.size() != 0) {
                            mInformationDetailUnread.setVisibility(mPage == mTotalPage ?
                                    View.INVISIBLE : View.VISIBLE);
                        } else {
                            mInformationDetailUnread.setVisibility(View.INVISIBLE);
                        }
                        mTotalPage = commentResult.getTotalPage();
                        mPage = 2;
                        mCommentAdapter = new CommentAdapter(getApplicationContext(),
                                mCommentList);
                        //评论
                        mCommentAdapter.setOnScoreClickListener(InformationDetailActivity
                                .this);
                        mListView.setAdapter(mCommentAdapter);
                        break;
                    case "failure":
                        Util.showError(InformationDetailActivity.this, errorBean
                                .getReason());
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.information_detail_check:
                //打开纠错
                Intent intent = new Intent(this, ChangeErrorActivity.class);
                intent.putExtra("newsId", mNewsId);
                startActivity(intent);
                break;
            case R.id.information_detail_score:
                //点赞
                postScore();
                break;
            case R.id.information_circle_of_friends:
                //朋友圈分享
                WeChatCircleShare();
                break;
            case R.id.information_qq_zone:
                //空间分享
                QZoneShare();
                break;
            case R.id.information_sina:
                //新浪微博分享
                SinaShare();
                break;
            case R.id.information_write_comment:
            case R.id.information_detail_comment2:
                //写评论
                writeComment();
                break;
            case R.id.information_detail_collection:
                //收藏或者取消收藏
                LogUtil.Companion.d(mNews.getUserCollectionStatus() + "");
                if (mNews.getUserCollectionStatus() == 0) {
                    //未收藏，去收藏
                    collection();
                } else {
                    //已收藏，取消收藏
                    cancelCollection();
                }
                break;
            case R.id.information_detail_share1:
            case R.id.information_detail_share:
                //分享
                share();
                break;
        }
    }


    /**
     * 微信朋友圈分享
     */
    private void WeChatCircleShare() {
        UMImage umImage = new UMImage(InformationDetailActivity.this, mImageUrl);
        new ShareAction(InformationDetailActivity.this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                .withTitle(mInformationDetailTitle1.getText().toString().trim())
                .withText("我在疫苗圈发现了一篇不错的文章，快来看看吧")
                .withMedia(umImage)
                .withTargetUrl(Constant.SHARE_URL + "/api/news/" + mNewsId)
                .setCallback(umShareListener)
                .share();
    }

    /**
     * QQ空间分享
     */
    private void QZoneShare() {
        UMImage umImage = new UMImage(InformationDetailActivity.this, mImageUrl);
        new ShareAction(InformationDetailActivity.this).setPlatform(SHARE_MEDIA.QZONE)
                .withTitle(mInformationDetailTitle1.getText().toString().trim())
                .withText("我在疫苗圈发现了一篇不错的文章，快来看看吧")
                .withMedia(umImage)
                .withTargetUrl(Constant.SHARE_URL + "/api/news/" + mNewsId)
                .setCallback(umShareListener)
                .share();
    }

    /**
     * 新浪微博分享
     */
    private void SinaShare() {
        UMImage umImage = new UMImage(InformationDetailActivity.this, mImageUrl);
        new ShareAction(InformationDetailActivity.this).setPlatform(SHARE_MEDIA.SINA)
                .withTitle(mInformationDetailTitle1.getText().toString().trim())
                .withText("我在疫苗圈发现了一篇不错的文章，快来看看吧")
                .withMedia(umImage)
                .withTargetUrl(Constant.SHARE_URL + "/api/news/" + mNewsId)
                .setCallback(umShareListener)
                .share();
    }

    /**
     * 分享
     */
    private void share() {
        final SHARE_MEDIA[] displayList = new SHARE_MEDIA[]
                {
                        SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN,
                        SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA
                };
        UMImage umImage = new UMImage(InformationDetailActivity.this, mImageUrl);
        new ShareAction(InformationDetailActivity.this).setDisplayList(displayList)
                .withTitle(mInformationDetailTitle1.getText().toString().trim())
                .withText("我在疫苗圈发现了一篇不错的文章，快来看看吧")
                .withMedia(umImage)
                .withTargetUrl(Constant.SHARE_URL + "/api/news/" + mNewsId)
                .setCallback(umShareListener)
                .open();
    }

    /**
     * 分享回调接口
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastUtil.showShort(getApplicationContext(), "分享成功");
            if ("increase".equals(mNews.getIntegralType())) {
                //增加积分
                addIntegral();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtil.showShort(getApplicationContext(), "分享失败");
            if (t != null) {
                LogUtil.Companion.d("分享失败E：" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtil.showShort(getApplicationContext(), "取消分享");
        }
    };

    /**
     * 增加积分
     */
    private void addIntegral() {
        OkHttpUtils.post().url(Constant.BASE_URL + "/api/integral/calculate")
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams("objectId", mNewsId + "")
                .addParams("objectType", "news")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("分享增加积分E：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("分享增加积分：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //本地增加积分
                        mIntegral += mNews.getIntegralValue();
                        //更新本地积分数据
                        SharePreferenceUtil.put(getApplicationContext(), Constant.INTEGRAL,
                                mIntegral);
                        //显示新增积分
                        ToastUtil.showShort(currentContext, "分享成功，增加" + mNews.getIntegralValue() + "积分");
                        break;
                    case "failure":
                        if (errorBean.getReason() == 116) {
                            Util.showError(InformationDetailActivity.this, errorBean.getReason());
                        } else {
                            ToastUtil.showShort(currentContext, "积分只能累加一次哟~");
                        }
                        break;
                }
            }
        });
    }

    /**
     * 收藏
     */
    private void collection() {
        getBuild(ADD_COLLECTION)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.Companion.d("收藏资讯E：" + e.getMessage());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.Companion.d("收藏资讯：" + response);
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                //收藏成功，改变图标样式，记录收藏结果
                                mInformationDetailCollection.setImageResource(R.mipmap
                                        .ico_information_activation_collection);
                                ToastUtil.showShort(getApplicationContext(), "收藏成功");
                                mNews.setUserCollectionStatus(1);
                                break;
                            case "failure":
                                Util.showError(InformationDetailActivity.this, errorBean
                                        .getReason());
                                break;
                        }
                    }
                });
    }

    /**
     * 取消收藏
     */
    private void cancelCollection() {
        getBuild(Constant.BASE_URL + "/api/news/cancel_collection")
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.Companion.d("取消收藏E：" + e.getLocalizedMessage());
                        Util.showTimeOutNotice(currentContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.Companion.d("取消收藏：" + response);
                        ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                        switch (errorBean.getStatus()) {
                            case "success":
                                //取消收藏成功，改变图标样式，记录收藏结果
                                mInformationDetailCollection.setImageResource(R.mipmap
                                        .ico_information_default_collection);
                                ToastUtil.showShort(getApplicationContext(), "取消收藏成功");
                                mNews.setUserCollectionStatus(0);
                                break;
                            case "failure":
                                Util.showError(InformationDetailActivity.this, errorBean
                                        .getReason());
                                break;
                        }
                    }
                });

    }

    private void writeComment() {
        mCommentPopupWindow = new CommentPopupWindow(this);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.6f;
        getWindow().setAttributes(lp);//设置PopupWindow弹出后背景颜色改变为半透明的黑色
        /**
         * 第一个参数指定PopupWindow的锚点view，即依附在哪个view上。
         * 第二个参数指定起始点为parent的右下角，
         * 第三个参数设置以parent的右下角为原点，向左、上各偏移多少像素。
         * 将PopupWindow作为anchor的下拉窗口显示。即在anchor的左下角显示
         **/

        mCommentPopupWindow.showAtLocation(this.findViewById(R.id.ll_comment_bottom), Gravity
                .BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        mCommentPopupWindow.update();

        //弹出窗体销毁时恢复背景颜色
        mCommentPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;//恢复原来背景颜色的透明度
                getWindow().setAttributes(lp);
                mCommentPopupWindow.hideKeyBoard();
            }
        });
        //点击监听，提交评论
        mCommentPopupWindow.setOnPopupWindowClickListener(this);
    }

    /**
     * 给资讯点赞
     */
    private void postScore() {
        getBuild(POST_SCORE).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("资讯点赞E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("资讯点赞" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //资讯点赞数加1
                        mInformationDetailScore.setText((mScore + 1) + "人点赞");
                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
    }

    private RequestCall getBuild(String url) {
        return OkHttpUtils.post().url(url)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams("newsId", mNewsId + "").build();
    }

    /**
     * 给评论点赞
     *
     * @param comment_user_score
     * @param commentId
     */
    @Override
    public void OnScoreClick(TextView comment_user_score, int commentId) {
        mCommentUserScore = comment_user_score;
        OkHttpUtils.post().url(POST_COMMENT_SCORE)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams("commentId", commentId + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("评论点赞E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.Companion.d("评论点赞" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //评论点赞数加1
                        int score_new = Integer.parseInt(mCommentUserScore.getText()
                                .toString()) + 1;
                        mCommentUserScore.setText(score_new + "");
                        mCommentUserScore.setCompoundDrawablesWithIntrinsicBounds
                                (mActivationScore, null, null, null);
                        break;
                    case "failure":
                        Util.showError(InformationDetailActivity.this, errorBean
                                .getReason());
                        break;
                }
            }
        });
    }

    @Override
    public void OnPopupWindowClick(EditText comment_content) {
        String commentContent = comment_content.getText().toString();
        OkHttpUtils.post().url(POST_NEWS_COMMENT)
                .addHeader(ACCESS_TOKEN, mAccessToken)
                .addParams("objectId", mNewsId + "")
                .addParams("commentContent", commentContent)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.Companion.d("提交评论E：" + e.getMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        ToastUtil.showLong(InformationDetailActivity.this, "提交成功");
                        //重新请求网络
                        showInformationComment();
                        break;
                    case "failure":
                        Util.showError(InformationDetailActivity.this, errorBean
                                .getReason());
                        break;
                }
            }
        });
    }

    @Override
    public void leftOnClick() {
        finish();
    }

    @Override
    public void rightOnClick() {

    }

    /**
     * 加载更多评论
     */
    @Override
    public void onLoadMore() {
        if (mPage <= mTotalPage) {
            OkHttpUtils.post().url(NEWS_COMMENT_LIST)
                    .addHeader(ACCESS_TOKEN, mAccessToken)
                    .addParams("objectId", mNewsId + "")
                    .addParams("userId", mUserId + "")
                    .addParams(PAGE, mPage + "")
                    .addParams(PAGE_SIZE, "10")
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    LogUtil.Companion.d("评论列表E：" + e.getMessage());
                }

                @Override
                public void onResponse(String response, int id) {
                    LogUtil.Companion.d("评论列表：" + response);
                    ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                    switch (errorBean.getStatus()) {
                        case "success":
                            mInformationDetailUnread.setVisibility(mPage == mTotalPage ?
                                    View.INVISIBLE : View.VISIBLE);
                            mPage++;
                            mCommentList.addAll(JSON.parseObject(response, CommentBean
                                    .class).getCommentResult().getCommentList());
                            mCommentAdapter.notifyDataSetChanged();
                            break;
                        case "failure":
                            Util.showError(InformationDetailActivity.this, errorBean
                                    .getReason());
                            break;
                    }
                    mListView.onLoadMoreComplete();
                }
            });
        } else {
            mListView.noMore();
        }
    }

    /**
     * 标签搜索
     *
     * @param tag The tag text of the tag that was clicked.
     */
    @Override
    public void onTagClick(String tag) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("TAG", tag);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && mWebView != null) {
            mWebView.onPause(); // 暂停网页中正在播放的视频
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && mWebView != null) {
            mWebView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLayout.removeView(mWebView);
        mWebView.removeAllViews();
        mWebView.destroy();
    }
}
