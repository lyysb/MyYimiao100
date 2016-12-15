package com.yimiao100.sale.adapter.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.ReconciliationDetail;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 推广费提现详情Adapter
 * <p/>
 * Created by 亿苗通 on 2016/8/25.
 */
public class PromotionDetailAdapter extends BaseAdapter {


    private final ArrayList<ReconciliationDetail> mList;

    public PromotionDetailAdapter(ArrayList<ReconciliationDetail> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public ReconciliationDetail getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ReconciliationDetail item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_promotion_detail, parent, false);
        }
        //显示详情数据
        TextView promotion_detail_qty = ViewHolderUtil.get(convertView, R.id.promotion_detail_qty);
        promotion_detail_qty.setText(item.getQty() + "（" + item.getUnits() + "）");

        TextView promotion_detail_unit_price = ViewHolderUtil.get(convertView, R.id
                .promotion_detail_unit_price);
        promotion_detail_unit_price.setText(item.getUnitPrice() + "元/" + item.getUnits());

        TextView promotion_detail_expired_at = ViewHolderUtil.get(convertView, R.id
                .promotion_detail_expired_at);
        promotion_detail_expired_at.setText(TimeUtil.timeStamp2Date(item.getExpiredAt() + "",
                "yyyy年MM月dd日"));

        TextView promotion_detail_product_batch_no = ViewHolderUtil.get(convertView, R.id
                .promotion_detail_product_batch_no);
        promotion_detail_product_batch_no.setText(item.getProductBatchNo());

        TextView promotion_detail_invoice_no = ViewHolderUtil.get(convertView, R.id
                .promotion_detail_invoice_no);
        promotion_detail_invoice_no.setText(item.getInvoiceNo());

        TextView promotion_detail_serial_no = ViewHolderUtil.get(convertView, R.id
                .promotion_detail_serial_no);
        promotion_detail_serial_no.setText(item.getSerialNo());

        //发货
        TextView tv_promotion_ship_time = ViewHolderUtil.get(convertView, R.id
                .tv_promotion_ship_time);
        TextView promotion_detail_delivery_at = ViewHolderUtil.get(convertView, R.id
                .promotion_detail_delivery_at);
        TextView tv_promotion_ship_cast = ViewHolderUtil.get(convertView, R.id
                .tv_promotion_ship_cast);
        TextView promotion_detail_delivery_total_amount = ViewHolderUtil.get(convertView, R.id
                .promotion_detail_delivery_total_amount);
        double deliveryTotalAmount = item.getDeliveryTotalAmount();
        promotion_detail_delivery_total_amount.setText(FormatUtils.MoneyFormat(deliveryTotalAmount) + "元");
        long deliveryAt = item.getDeliveryAt();
        promotion_detail_delivery_at.setText(TimeUtil.timeStamp2Date(deliveryAt + "", "yyyy年MM月dd日"));
        //控制发货条目的显示和隐藏
        tv_promotion_ship_time.setVisibility(deliveryTotalAmount == 0 ? View.GONE : View.VISIBLE);
        promotion_detail_delivery_at.setVisibility(deliveryTotalAmount == 0 ? View.GONE : View.VISIBLE);
        tv_promotion_ship_cast.setVisibility(deliveryTotalAmount == 0 ? View.GONE : View.VISIBLE);
        promotion_detail_delivery_total_amount.setVisibility(deliveryTotalAmount == 0 ? View.GONE : View.VISIBLE);

        //回款
        TextView tv_promotion_payment_time = ViewHolderUtil.get(convertView,R.id.
                tv_promotion_payment_time);
        TextView promotion_detail_payment_at = ViewHolderUtil.get(convertView,R.id.
                promotion_detail_payment_at);
        TextView tv_promotion_payment_cast = ViewHolderUtil.get(convertView,R.id.
                tv_promotion_payment_cast);
        TextView promotion_detail_payment_total_amount = ViewHolderUtil.get(convertView,R.id.
                promotion_detail_payment_total_amount);
        long paymentAt = item.getPaymentAt();
        double paymentTotalAmount = item.getPaymentTotalAmount();
        promotion_detail_payment_total_amount.setText(paymentTotalAmount == 0 ? "" : FormatUtils.MoneyFormat(paymentTotalAmount) + "元");
        //控制回款的显示和隐藏
        tv_promotion_payment_time.setVisibility(paymentTotalAmount == 0 ? View.GONE : View.VISIBLE);
        promotion_detail_payment_at.setVisibility(paymentTotalAmount == 0 ? View.GONE : View.VISIBLE);
        tv_promotion_payment_cast.setVisibility(paymentTotalAmount == 0 ? View.GONE : View.VISIBLE);
        promotion_detail_payment_total_amount.setVisibility(paymentTotalAmount == 0 ? View.GONE : View.VISIBLE);
        promotion_detail_payment_at.setText(paymentTotalAmount == 0 ? "" : TimeUtil.timeStamp2Date
                (paymentAt + "", "yyyy年MM月dd日"));

        return convertView;
    }


}
