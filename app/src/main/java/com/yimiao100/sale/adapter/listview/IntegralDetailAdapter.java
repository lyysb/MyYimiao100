package com.yimiao100.sale.adapter.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.IntegralList;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 积分明细Adapter
 * Created by 亿苗通 on 2016/10/28.
 */
public class IntegralDetailAdapter extends BaseAdapter{

    private final ArrayList<IntegralList> mLists;

    public IntegralDetailAdapter(ArrayList<IntegralList> integralLists) {
        mLists = integralLists;
    }

    @Override
    public int getCount() {
        return mLists != null ? mLists.size() : 0;
    }

    @Override
    public IntegralList getItem(int position) {
        return mLists != null ? mLists.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IntegralList integral = getItem(position);

        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_integral, null);
        }
        ImageView type = ViewHolderUtil.get(convertView, R.id.integral_item_type);
        TextView title = ViewHolderUtil.get(convertView, R.id.integral_item_title);
        TextView value = ViewHolderUtil.get(convertView, R.id.integral_item_value);

        //对象类型
        String objectType = integral.getObjectType();
        //积分类型
        String integralType = integral.getIntegralType();
        //积分分值
        int integralValue = integral.getIntegralValue();
        //对象标题
        String objectTitle = integral.getObjectTitle();

        switch (objectType) {
            case "news":                //分享资讯
                type.setImageResource(R.mipmap.ico_my_points_share);
                break;
            case "course":              //看公开课视频
                type.setImageResource(R.mipmap.ico_my_points_watch);
                break;
            case "exchange_goods":      //积分兑换商品
                type.setImageResource(R.mipmap.ico_earn_points_green_shopping);
                break;
        }
        title.setText(objectTitle);
        switch (integralType) {
            case "increase":
                value.setText("+" + integralValue + "积分");
                break;
            case "decrease":
                //扣积分返回的就是负数
                value.setText(integralValue + "积分");
                break;
        }


        return convertView;
    }
}
