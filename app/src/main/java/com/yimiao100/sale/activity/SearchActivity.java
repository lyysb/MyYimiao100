package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.InformationAdapter;
import com.yimiao100.sale.adapter.listview.SearchAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.InformationBean;
import com.yimiao100.sale.bean.PagedListBean;
import com.yimiao100.sale.bean.PagedResultBean;
import com.yimiao100.sale.bean.TagListBean;
import com.yimiao100.sale.bean.TagsBean;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gujun.android.taggroup.TagGroup;
import okhttp3.Call;

/**
 * 搜索界面
 */
public class SearchActivity extends BaseActivity implements PullToRefreshListView.OnRefreshingListener, AdapterView.OnItemClickListener, TagGroup.OnTagClickListener, TextWatcher {

    @BindView(R.id.search_return)
    ImageView mSearchReturn;
    @BindView(R.id.search_input_text)
    EditText mSearchInputText;
    @BindView(R.id.search_search)
    TextView mSearchSearch;
    @BindView(R.id.search_history_list)
    ListView mSearchHistoryListView;
    @BindView(R.id.search_hot_tags)
    TagGroup mSearchHotTags;
    @BindView(R.id.search_foot)
    TextView mSearchFoot;
    @BindView(R.id.ll_search)
    LinearLayout mLlSearch;
    @BindView(R.id.search_result)
    PullToRefreshListView mSearchResult;
    @BindView(R.id.search_empty)
    View mSearchEmpty;
    private SearchAdapter mSearchAdapter;

    HashMap<String, Integer> mTags = new HashMap<>();       //kay-标签内容。value-标签id
    ArrayList<String> mHistoryList = new ArrayList<>();     //搜索历史集合
    private ArrayList<TagListBean> mTagList;

    private int PAGE = 1;
    private int TOTAL_PAGE;
    private List<PagedListBean> mPagedList;
    private InformationAdapter mInformationAdapter;
    private String TAG;
    private int WAY = 0;        //0--输入搜索；1--热词搜索


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initView();

        initData();
    }

    private void initView() {
        //列表上拉加载
        mSearchResult.setOnRefreshingListener(this);
        //列表条目点击监听
        mSearchResult.setOnItemClickListener(this);
        //标签点击监听
        mSearchHotTags.setOnTagClickListener(this);
        //输入框修改监听
        mSearchInputText.addTextChangedListener(this);

    }

    private void initData() {
        //初始化标签内容
        initTags();
        //初始化历史搜索内容
        initHistoryList();
        if (mHistoryList.isEmpty()) {
            mSearchFoot.setText("无记录");
        }

        mSearchAdapter = new SearchAdapter(mHistoryList);
        mSearchHistoryListView.setAdapter(mSearchAdapter);

        //如果由资讯详情进入，获取搜索内容，进行搜索
        Intent intent = getIntent();
        String tag = intent.getStringExtra("TAG");
        if (tag != null) {
            LogUtil.d("tag:" , tag);
            mSearchInputText.setText(tag);
            mSearchInputText.setSelection(tag.length());
            search();
        }

    }

    /**
     * 初始化标签内容
     */
    private void initTags() {
        OkHttpUtils.get().url(Constant.BASE_URL + "/api/news/tags")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("资讯搜索-标签E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.d("资讯搜索-标签：" + response);
                TagsBean tagsBean = JSON.parseObject(response, TagsBean.class);
                switch (tagsBean.getStatus()) {
                    case "success":
                        mTagList = tagsBean.getTagList();
                        ArrayList<String> s = new ArrayList<>();
                        for (TagListBean temp : mTagList) {
                            mTags.put(temp.getTagName(), temp.getId());
                            s.add(temp.getTagName());
                        }
                        mSearchHotTags.setTags(s);
                        break;
                    case "failure":
                        Util.showError(SearchActivity.this, tagsBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 初始化搜索历史
     */
    private void initHistoryList() {
        String temp = (String) SharePreferenceUtil.get(getApplicationContext(), Constant.SEARCH_HISTORY, "");
        String[] split = temp.split(",");
        if (split[0].length() != 0) {
            //遍历添加
            for (String s : split) {
                mHistoryList.add(s);
            }
        }
    }

    @OnClick({R.id.search_return, R.id.search_search, R.id.search_foot})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_return:
                finish();
                break;
            case R.id.search_search:
                search();
                break;
            case R.id.search_foot:
                //清空搜索记录
                mHistoryList.clear();
                SharePreferenceUtil.remove(getApplicationContext(), Constant.SEARCH_HISTORY);
                mSearchAdapter.notifyDataSetChanged();
                mSearchFoot.setText("无记录");
                break;
        }
    }

    private void search() {
        //如果输入内容为空，则提示输入内容
        if (mSearchInputText.getText().toString().length() == 0) {
            ToastUtil.showShort(getApplicationContext(), "请输入内容");
            return;
        }


        //如果能从标签Map中取得到值，则执行热词搜索
        if (!TextUtils.equals("null", mTags.get(mSearchInputText.getText().toString().trim()) + "")) {
            hotTagSearch(mSearchInputText.getText().toString().trim());
            return;
        }
        //标记方式为关键字搜索
        WAY = 0;
        getBuild(1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("资讯搜索E：" + e.getLocalizedMessage());
                Util.showTimeOutNotice(currentContext);
            }

            @Override
            public void onResponse(String response, int id) {
                mSearchResult.onLoadMoreComplete();
                LogUtil.d("资讯搜索：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //搜索成功
                        ///隐藏热词和搜索历史
                        mLlSearch.setVisibility(View.GONE);
                        ///显示搜索结果
                        mSearchResult.setVisibility(View.VISIBLE);
                        PAGE = 2;
                        PagedResultBean pagedResult = JSON.parseObject(response, InformationBean.class).getPagedResult();
                        TOTAL_PAGE = pagedResult.getTotalPage();

                        mSearchResult.setEmptyView(mSearchEmpty);

                        mPagedList = pagedResult.getPagedList();
                        mInformationAdapter = new InformationAdapter(getApplicationContext(), mPagedList);
                        mSearchResult.setAdapter(mInformationAdapter);

                        break;
                    case "failure":
                        Util.showError(currentContext, errorBean.getReason());
                        break;
                }
            }
        });
        //更新搜索记录
        updateSearchHistory(mSearchInputText.getText().toString().trim());
    }

    /**
     * 更新搜索记录
     * @param text
     */
    private void updateSearchHistory(String text) {
        String temp = (String) SharePreferenceUtil.get(getApplicationContext(), Constant.SEARCH_HISTORY, "");
        SharePreferenceUtil.put(getApplicationContext(), Constant.SEARCH_HISTORY, mSearchInputText.getText().toString().trim() + "," + temp);
        mHistoryList.add(0, text);
        mSearchAdapter.notifyDataSetChanged();
        mSearchFoot.setText("清空搜索记录");
    }

    /**
     * 普通搜索
     * @param page
     * @return
     */
    private RequestCall getBuild(int page) {
        return OkHttpUtils.post().url(Constant.BASE_URL + "/api/news/list")
                .addParams("searchText", mSearchInputText.getText().toString().trim())
                .addParams("page", page + "").addParams("pageSize", "10")
                .build();
    }

    @Override
    public void onLoadMore() {
        if (PAGE <= TOTAL_PAGE) {
            if (WAY == 0) {
                //关键字搜索加载更多
                searchLoadMore();
            } else {
                //热词搜索加载更多
                hotTagLoadMore();
            }
        } else {
            mSearchResult.noMore();
        }
    }

    /**
     * 关键字-加载更多
     */
    private void searchLoadMore() {
        getBuild(PAGE).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("资讯搜索E：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                mSearchResult.onLoadMoreComplete();
                LogUtil.d("资讯搜索：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //搜索成功
                        PAGE++;
                        mPagedList.addAll(JSON.parseObject(response, InformationBean.class).getPagedResult().getPagedList());
                        mInformationAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(SearchActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 热词-加载更多
     */
    private void hotTagLoadMore() {
        getBuild(TAG, PAGE).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("资讯搜索E：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                mSearchResult.onLoadMoreComplete();
                LogUtil.d("资讯搜索：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //搜索成功
                        PAGE++;
                        mPagedList.addAll(JSON.parseObject(response, InformationBean.class).getPagedResult().getPagedList());
                        mInformationAdapter.notifyDataSetChanged();
                        break;
                    case "failure":
                        Util.showError(SearchActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int news_id = mPagedList.get(position).getId();
        Intent intent = new Intent(getApplicationContext(), InformationDetailActivity.class);
        intent.putExtra("newsId", news_id);
        startActivity(intent);
    }

    @Override
    public void onTagClick(String tag) {
        mSearchInputText.setText(tag);
        mSearchInputText.setSelection(tag.length());
        hotTagSearch(tag);
        //更新搜索记录
        updateSearchHistory(tag);
    }

    private void hotTagSearch(String tag) {
        WAY = 1;
        TAG = tag;
        getBuild(tag, 1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.d("资讯搜索E：" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                mSearchResult.onLoadMoreComplete();
                LogUtil.d("资讯搜索：" + response);
                ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                switch (errorBean.getStatus()) {
                    case "success":
                        //搜索成功
                        ///隐藏热词和搜索历史
                        mLlSearch.setVisibility(View.GONE);
                        ///显示搜索结果
                        mSearchResult.setVisibility(View.VISIBLE);
                        PAGE = 2;
                        PagedResultBean pagedResult = JSON.parseObject(response, InformationBean.class).getPagedResult();
                        TOTAL_PAGE = pagedResult.getTotalPage();

                        mSearchResult.setEmptyView(mSearchEmpty);

                        mPagedList = pagedResult.getPagedList();
                        mInformationAdapter = new InformationAdapter(getApplicationContext(), mPagedList);
                        mSearchResult.setAdapter(mInformationAdapter);

                        break;
                    case "failure":
                        Util.showError(SearchActivity.this, errorBean.getReason());
                        break;
                }
            }
        });
    }

    /**
     * 标签搜索
     * @param tag
     * @param page
     * @return
     */
    private RequestCall getBuild(String tag, int page) {
        return OkHttpUtils.post().url(Constant.BASE_URL + "/api/news/list")
                .addParams("page", page + "").addParams("pageSize", "10")
                .addParams("tagId", mTags.get(tag) + "")
                .build();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            mLlSearch.setVisibility(View.VISIBLE);
            mSearchResult.setVisibility(View.GONE);
        }
        if (TextUtils.equals(s.toString().trim(), TAG)) {
            WAY = 1;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {}
}
