package com.yimiao100.sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.adapter.listview.ExperienceAdapter;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.bean.Experience;
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
    }

    private void initData() {
        TYPE = getIntent().getIntExtra("type", -1);
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
        mAdapter = new ExperienceAdapter(mList);
        mListView.setAdapter(mAdapter);
        mAdapter.setOnDeleteClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        edit(position);
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
    public void delete(int position) {
        Experience experience = mList.get(position);
        mMap.remove(experience.getSerialNo());
        mList.remove(position);
        mAdapter.notifyDataSetChanged();
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
