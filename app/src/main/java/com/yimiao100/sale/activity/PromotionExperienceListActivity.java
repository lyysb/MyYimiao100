package com.yimiao100.sale.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.ExperienceAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Experience;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.Util;
import com.yimiao100.sale.view.TitleView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 疫苗推广经历列表
 */
public class PromotionExperienceListActivity extends BaseActivity implements TitleView.TitleBarOnClickListener, AdapterView.OnItemClickListener, View.OnClickListener, ExperienceAdapter.onDeleteClickListener {


    @BindView(R.id.promotion_experience_title)
    TitleView mTitle;
    @BindView(R.id.promotion_experience_list)
    ListView mListView;
    @BindView(R.id.promotion_experience_add)
    TextView mExperienceAdd;

    private static int TYPE;
    private final static int CORPORATE = 1;
    private final static int PERSONAL = 2;

    private static final int ADD_EXPERIENCE = 101;
    private static final int EDIT_EXPERIENCE = 102;

    private ExperienceAdapter mAdapter;
    private ArrayList<Experience> mList;
    private HashMap<String, Experience> mMap = new HashMap<>();
    private String mAccountStatus;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_experience_list);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initView() {
        mTitle.setOnTitleBarClick(this);
        mListView.setOnItemClickListener(this);
        mExperienceAdd.setOnClickListener(this);
        mEmptyView = findViewById(R.id.experience_list_empty_view);
        TextView emptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
        emptyText.setText(getString(R.string.empty_view_experience));
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ico_extension_experience), null, null);
    }

    private void initData() {
        TYPE = getIntent().getIntExtra("type", -1);
        mAccountStatus = getIntent().getStringExtra("accountStatus");
        mList = getIntent().getParcelableArrayListExtra("experience");
        if (mList != null) {
            for (Experience experience : mList) {
                if (experience.getSerialNo() == null || experience.getSerialNo().isEmpty()) {
                    experience.setSerialNo(Util.generateSeriaNo());
                }
                mMap.put(experience.getSerialNo(), experience);
            }
        } else {
            mList = new ArrayList<>();
        }
        mEmptyView.setVisibility(mList == null || mList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
        mAdapter = new ExperienceAdapter(mList);
        mListView.setAdapter(mAdapter);
        mAdapter.setOnDeleteClickListener(this);
        // 已通过审核，不能新增
        if (TextUtils.equals("passed", mAccountStatus)) {
            LogUtil.d("账户已通过审核，禁止新增");
            mExperienceAdd.setVisibility(View.GONE);
        } else if (TextUtils.equals("auditing", mAccountStatus)) {
            LogUtil.d("账户正在审核中，禁止新增");
            mExperienceAdd.setVisibility(View.GONE);
        } else {
            mExperienceAdd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 根据账户状态
        if (TextUtils.equals("passed", mAccountStatus)) {
            LogUtil.d("账户已通过审核，禁止修改");
        } else if (TextUtils.equals("auditing", mAccountStatus)) {
            LogUtil.d("账户正在审核中，禁止修改");
        } else {
            edit(position);
        }
    }

    private void edit(int position) {
        Experience experience = mList.get(position);
        Intent intent = new Intent(this, PromotionExperienceActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("experience", experience);
        intent.putExtra("type", EDIT_EXPERIENCE);
        startActivityForResult(intent, EDIT_EXPERIENCE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.promotion_experience_add:
                add();
                break;
        }
    }

    private void add() {
        Intent intent = new Intent(this, PromotionExperienceActivity.class);
        intent.putExtra("type", ADD_EXPERIENCE);
        startActivityForResult(intent, ADD_EXPERIENCE);
    }

    @Override
    public void delete(final int position) {
        // 如果已通过，则不能删除
        if (TextUtils.equals("passed", mAccountStatus)) {
            LogUtil.d("已通过审核，禁止删除");
        } else if (TextUtils.equals("auditing", mAccountStatus)) {
            LogUtil.d("账户正在审核中，禁止修改");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("确定删除？");
            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Experience experience = mList.get(position);
                    mMap.remove(experience.getSerialNo());
                    mList.remove(position);
                    mAdapter.notifyDataSetChanged();
                    mEmptyView.setVisibility(mList == null || mList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("不删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_EXPERIENCE:
                if (resultCode == ADD_EXPERIENCE && data != null) {
                    Experience experience = data.getParcelableExtra("experience");
                    mList.add(experience);
                    mAdapter.notifyDataSetChanged();
                    mMap.put(experience.getSerialNo(), experience);
                    mEmptyView.setVisibility(mList == null || mList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
                break;
            case EDIT_EXPERIENCE:
                if (resultCode == EDIT_EXPERIENCE && data != null) {
                    int position = data.getIntExtra("position", -1);
                    Experience experience = data.getParcelableExtra("experience");
                    if (-1 != position) {
                        mList.set(position, experience);
                        mAdapter.notifyDataSetChanged();
                        mMap.put(experience.getSerialNo(), experience);
                        mEmptyView.setVisibility(mList == null || mList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                    }
                }
        }
    }

    @Override
    public void leftOnClick() {
        finalResult();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finalResult();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void finalResult() {
//        mList = verifyList();
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("experience", mList);
        switch (TYPE) {
            case CORPORATE:
                setResult(CORPORATE, intent);
                break;
            case PERSONAL:
                setResult(PERSONAL, intent);
                break;
        }
    }

    private ArrayList<Experience> verifyList() {
        return new ArrayList<>(mMap.values());
    }

    @Override
    public void rightOnClick() {

    }
}
