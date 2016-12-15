package com.yimiao100.sale.adapter.listview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.ReconciliationDetail;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 对账详情Adapter
 * <p/>
 * Created by 亿苗通 on 2016/8/25.
 */
public class ReconciliationDetailAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final ArrayList<ReconciliationDetail> mOrderItemList;
    private onStatusClickListener mListener;

    public ReconciliationDetailAdapter(Context context, ArrayList<ReconciliationDetail> orderItemList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mOrderItemList = orderItemList;
    }

    @Override
    public int getCount() {
        return mOrderItemList != null ? mOrderItemList.size() : 0;
    }

    @Override
    public ReconciliationDetail getItem(int position) {
        return mOrderItemList != null ? mOrderItemList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ReconciliationDetail reconciliationDetail = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_reconciliation_detail, parent, false);
        }
        //数量
        int qty = reconciliationDetail.getQty();
        TextView reconciliation_detail_qty = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_qty);
        reconciliation_detail_qty.setText(qty + reconciliationDetail.getUnits());
        //有效期
        long expiredAt = reconciliationDetail.getExpiredAt();
        TextView reconciliation_detail_expired_at = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_expired_at);
        reconciliation_detail_expired_at.setText(TimeUtil.timeStamp2Date(expiredAt + "", "yyyy年MM月dd日"));
        //销货单号
        String invoiceNo = reconciliationDetail.getInvoiceNo();
        TextView reconciliation_detail_invoice_no = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_invoice_no);
        reconciliation_detail_invoice_no.setText(invoiceNo);
        //供货价
        double unitPrice = reconciliationDetail.getUnitPrice();
        TextView reconciliation_detail_unit_price = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_unit_price);
        reconciliation_detail_unit_price.setText(FormatUtils.MoneyFormat(unitPrice) + "元/" + reconciliationDetail.getUnits());
        //产品批号
        String productBatchNo = reconciliationDetail.getProductBatchNo();
        TextView reconciliation_detail_product_batch_no = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_product_batch_no);
        reconciliation_detail_product_batch_no.setText(productBatchNo);
        //订单单号
        String serialNo = reconciliationDetail.getSerialNo();
        TextView reconciliation_detail_serial_no = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_serial_no);
        reconciliation_detail_serial_no.setText(serialNo);
        //发货日期
        long deliveryAt = reconciliationDetail.getDeliveryAt();
        TextView reconciliation_detail_delivery_at = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_delivery_at);
        reconciliation_detail_delivery_at.setText(TimeUtil.timeStamp2Date(deliveryAt + "", "yyyy年MM月dd日"));
        //发货费用
        double deliveryTotalAmount = reconciliationDetail.getDeliveryTotalAmount();
        TextView reconciliation_detail_delivery_total_amount = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_delivery_total_amount);
        reconciliation_detail_delivery_total_amount.setText(deliveryTotalAmount + "");
        //根据发货费用是否为0，设置发货条目是否显示
        LinearLayout shipItem = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_ship_detail);
        shipItem.setVisibility(deliveryTotalAmount != 0 ? View.VISIBLE : View.GONE);
        //回款日期
        long paymentAt = reconciliationDetail.getPaymentAt();
        TextView reconciliation_detail_payment_at = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_payment_at);
        reconciliation_detail_payment_at.setText(TimeUtil.timeStamp2Date(paymentAt + "", "yyyy年MM月dd日"));
        //回款费用
        double paymentTotalAmount = reconciliationDetail.getPaymentTotalAmount();
        TextView reconciliation_detail_payment_total_amount = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_payment_total_amount);
        reconciliation_detail_payment_total_amount.setText(paymentTotalAmount + "");
        //逾期扣款说明
        String withholdRemark = reconciliationDetail.getWithholdRemark();
        TextView reconciliation_detail_withhold_remark = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_withhold_remark);
        if (withholdRemark == null) {
            reconciliation_detail_withhold_remark.setVisibility(View.GONE);
        } else {
            reconciliation_detail_withhold_remark.setVisibility(View.VISIBLE);
            reconciliation_detail_withhold_remark.setText(withholdRemark);
        }
        //发货状态
        String deliveryConfirmStatus = reconciliationDetail.getDeliveryConfirmStatus();
        String deliveryConfirmStatusName = reconciliationDetail.getDeliveryConfirmStatusName();
        TextView ship = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_ship);
        ship.setText(deliveryConfirmStatusName);
        if (TextUtils.equals(deliveryConfirmStatus, "confirmed")) {
            //按钮不可点击
            ship.setEnabled(false);
            //背景颜色变蓝色
            ship.setBackgroundResource(R.mipmap.ico_reconciliation_not_confirmed_activation);
            //字体变成白色
            ship.setTextColor(Color.parseColor("#ffffff"));
        } else {
            //字体变成选择器
            ship.setTextColor(parent.getContext().getResources().getColorStateList(R.color.color_riches_wealth));
            ship.setEnabled(true);
            ship.setBackgroundResource(R.drawable.selector_reconciliation_not_confirm);
        }
        //回款状态
        String paymentConfirmStatus = reconciliationDetail.getPaymentConfirmStatus();
        String paymentConfirmStatusName = reconciliationDetail.getPaymentConfirmStatusName();
        TextView payment = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_payment);
        payment.setText(paymentConfirmStatusName);
        if (TextUtils.equals(paymentConfirmStatus, "confirmed")) {
            //按钮不可点击
            payment.setEnabled(false);
            //背景颜色变蓝色
            payment.setBackgroundResource(R.mipmap.ico_reconciliation_not_confirmed_activation);
            //字体变成白色
            payment.setTextColor(Color.parseColor("#ffffff"));
        } else {
            payment.setTextColor(parent.getContext().getResources().getColorStateList(R.color.color_riches_wealth));
            payment.setEnabled(true);
            payment.setBackgroundResource(R.drawable.selector_reconciliation_not_confirm);
        }
        //回款列表
        LinearLayout reconciliation_detail_payment_ll = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_payment_ll);
        if (paymentAt == 0) {
            //回款时间为0，回款列表不显示
            reconciliation_detail_payment_ll.setVisibility(View.GONE);
        } else {
            //回款时间不为0，回款列表显示
            reconciliation_detail_payment_ll.setVisibility(View.VISIBLE);
        }

        //发货状态
        // -如果是未确认，暴露接口
        ship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDeliveryClick(position);
                }
            }
        });
        //回款状态
        // -如果是未确认，暴露接口
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPaymentClick(position);
                }
            }
        });

        return convertView;
    }

    public void setOnStatusClickListener(onStatusClickListener listener) {
        this.mListener = listener;
    }

    public interface onStatusClickListener {
        /**
         * 发货确认
         */
        void onDeliveryClick(int position);

        /**
         * 回款确认
         */
        void onPaymentClick(int position);
    }
}
