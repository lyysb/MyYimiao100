package com.yimiao100.sale.adapter.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.RichesDetailList;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 财富-明细列表
 * Created by 亿苗通 on 2016/9/8.
 */
public class RichesDetailAdapter extends BaseAdapter {


    private final ArrayList<RichesDetailList> mList;

    public RichesDetailAdapter(ArrayList<RichesDetailList> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public RichesDetailList getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RichesDetailList item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riches_detail, null);
        }
        TextView riches_detail_list_time = ViewHolderUtil.get(convertView, R.id.riches_detail_list_time);
        TextView riches_detail_list_num = ViewHolderUtil.get(convertView, R.id.riches_detail_list_num);
        TextView riches_detail_list_type = ViewHolderUtil.get(convertView, R.id.riches_detail_list_type);
        //时间
        long createdAt = item.getCreatedAt();
        riches_detail_list_time.setText(TimeUtil.timeStamp2Date(createdAt + "", "yyyy/MM/dd\nHH:mm:ss"));
        //根据交易类型显示进出账金额
        /*put(TRANSACTION_TYPE_INCOME, "收入");
        put(TRANSACTION_TYPE_WITHDRAW, "提现");
        put(TRANSACTION_TYPE_WITHHOLD, "扣除");
        put(TRANSACTION_TYPE_CONSUME, "消费");*/
        switch (item.getTransactionType()) {
            case "income":
                riches_detail_list_num.setText("+" + FormatUtils.MoneyFormat(item.getAmount()));
                break;
            default:
                riches_detail_list_num.setText(FormatUtils.MoneyFormat(item.getAmount()));
                break;
        }
        //设置来源方式
        riches_detail_list_type.setText(item.getRemark());
        return convertView;
    }
}
