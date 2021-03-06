package com.yimiao100.sale.adapter.listview;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.ReconciliationDetail;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;

/**
 * 对账详情Adapter
 * <p/>
 * Created by 亿苗通 on 2016/8/25.
 * Update by Michel on 2017年11月3日. -- 增减垫款逻辑
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
        ReconciliationDetail item = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_reconciliation_detail, parent, false);
        }
        //数量
        int qty = item.getQty();
        TextView reconciliation_detail_qty = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_qty);
        reconciliation_detail_qty.setText(qty + item.getUnits());
        //有效期
        long expiredAt = item.getExpiredAt();
        TextView reconciliation_detail_expired_at = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_expired_at);
        reconciliation_detail_expired_at.setText(TimeUtil.timeStamp2Date(expiredAt + "", "yyyy年MM月dd日"));
        //销货单号
        String invoiceNo = item.getInvoiceNo();
        TextView reconciliation_detail_invoice_no = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_invoice_no);
        reconciliation_detail_invoice_no.setText(invoiceNo);
        //供货价
        double unitPrice = item.getUnitPrice();
        TextView reconciliation_detail_unit_price = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_unit_price);
        reconciliation_detail_unit_price.setText(FormatUtils.MoneyFormat(unitPrice) + "元/" + item.getUnits());
        //产品批号
        String productBatchNo = item.getProductBatchNo();
        TextView reconciliation_detail_product_batch_no = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_product_batch_no);
        reconciliation_detail_product_batch_no.setText(productBatchNo);
        //订单单号
        String serialNo = item.getSerialNo();
        TextView reconciliation_detail_serial_no = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_serial_no);
        reconciliation_detail_serial_no.setText(serialNo);
        //发货日期
        long deliveryAt = item.getDeliveryAt();
        TextView reconciliation_detail_delivery_at = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_delivery_at);
        reconciliation_detail_delivery_at.setText(TimeUtil.timeStamp2Date(deliveryAt + "", "yyyy年MM月dd日"));
        //发货费用
        double deliveryTotalAmount = item.getDeliveryTotalAmount();
        TextView reconciliation_detail_delivery_total_amount = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_delivery_total_amount);
        reconciliation_detail_delivery_total_amount.setText(deliveryTotalAmount + "");
        //根据发货奖励是否为0，设置发货奖励是否显示
        LinearLayout shipDeliveryItem = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_ship_detail);
        shipDeliveryItem.setVisibility(deliveryTotalAmount != 0 ? View.VISIBLE : View.GONE);

        if (!item.isAdvanceFlag()) {
            // 不显示垫款操作
            ViewHolderUtil.get(convertView, R.id.reconciliation_detail_overdue_ll).setVisibility(View.GONE);
        } else {
            // 显示垫款操作
            ViewHolderUtil.get(convertView, R.id.reconciliation_detail_overdue_ll).setVisibility(View.VISIBLE);
            TextView overdueAt = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_overdue_at);
            TextView overdueConfirm = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_overdue_confirm);
            if (item.getAdvanceAt() != null) {
                // 格式化显示垫款条目信息
                overdueAt.setText(TimeUtil.timeStamp2Date(item.getAdvanceAt(), "yyyy年MM月dd日"));
                // 垫款操作按钮禁止使用
                overdueConfirm.setEnabled(false);
                overdueConfirm.setTextColor(parent.getContext().getResources().getColor(R.color.color999));
                overdueConfirm.setBackgroundResource(R.mipmap.ico_reconciliation_gray);
            } else {
                // 格式化显示内容
                int diffDays = item.getDiffDays();
                if (diffDays > 0) {
                    overdueAt.setText(Html.fromHtml("距离逾期还剩<font color=\"#d24141\">" + diffDays + "</font>天"));
                } else if (diffDays == 0) {
                    overdueAt.setText(Html.fromHtml("距离逾期还剩<font color=\"#d24141\">1</font>天"));
                } else if (diffDays < 0) {
                    diffDays = Math.abs(diffDays);
                    overdueAt.setText(Html.fromHtml("已逾期<font color=\"#d24141\">" + diffDays + "</font>天"));
                }
                // 垫款操作按钮可以使用，外部回调接口
                overdueConfirm.setEnabled(true);
                overdueConfirm.setTextColor(Color.parseColor("#ffffffff"));
                overdueConfirm.setBackgroundResource(R.mipmap.ico_reconciliation_blue);
                overdueConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onOverdueClick(position);
                        }
                    }
                });
            }
        }



        //回款日期
        long paymentAt = item.getPaymentAt();
        TextView reconciliation_detail_payment_at = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_payment_at);
        reconciliation_detail_payment_at.setText(TimeUtil.timeStamp2Date(paymentAt + "", "yyyy年MM月dd日"));
        //回款费用
        double paymentTotalAmount = item.getPaymentTotalAmount();
        TextView reconciliation_detail_payment_total_amount = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_payment_total_amount);
        reconciliation_detail_payment_total_amount.setText(paymentTotalAmount + "");
        //逾期扣款说明
        String withholdRemark = item.getWithholdRemark();
        TextView reconciliation_detail_withhold_remark = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_withhold_remark);
        if (withholdRemark == null || withholdRemark.isEmpty()) {
            reconciliation_detail_withhold_remark.setVisibility(View.GONE);
        } else {
            reconciliation_detail_withhold_remark.setVisibility(View.VISIBLE);
            reconciliation_detail_withhold_remark.setText(withholdRemark);
        }
        // 发货额外奖励说明
        String extraRewardRemark = item.getExtraRewardRemark();
        TextView reconciliation_detail_extra_reward_remark = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_extra_reward_remark);
        if (extraRewardRemark == null || extraRewardRemark.isEmpty()) {
            reconciliation_detail_extra_reward_remark.setVisibility(View.GONE);
        } else {
            reconciliation_detail_extra_reward_remark.setVisibility(View.VISIBLE);
            reconciliation_detail_extra_reward_remark.setText(extraRewardRemark);
        }
        //发货状态
        String deliveryConfirmStatus = item.getDeliveryConfirmStatus();
        ImageView ship = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_ship);
        if (TextUtils.equals(deliveryConfirmStatus, "confirmed")) {
            // 已确认
            ship.setEnabled(false);
            ship.setImageResource(R.mipmap.ico_reconciliation_not_not_confirmed_gray);
        } else {
            // 未确认
            ship.setImageResource(R.mipmap.ico_reconciliation_not_not_confirmed_blue);
        }
        //回款状态
        String paymentConfirmStatus = item.getPaymentConfirmStatus();
        ImageView payment = ViewHolderUtil.get(convertView, R.id.reconciliation_detail_payment);
        if (TextUtils.equals(paymentConfirmStatus, "confirmed")) {
            // 已确认
            payment.setEnabled(false);
            payment.setImageResource(R.mipmap.ico_reconciliation_not_not_confirmed_gray);
        } else {
            // 未确认
            payment.setImageResource(R.mipmap.ico_reconciliation_not_not_confirmed_blue);
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
         * 垫款
         * @param position
         */
        void onOverdueClick(int position);

        /**
         * 回款确认
         */
        void onPaymentClick(int position);
    }
}
