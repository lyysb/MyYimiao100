package com.yimiao100.sale.adapter.expendable;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.yimiao100.sale.R;
import com.yimiao100.sale.bean.Payment;
import com.yimiao100.sale.bean.PaymentList;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.TimeUtil;
import com.yimiao100.sale.utils.ViewHolderUtil;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * CRM-回款统计图
 * Created by 亿苗通 on 2016/9/12.
 */
public class CRMPaymentAdapter extends BaseExpandableListAdapter{


    private LineChart mLineChart;
    private BarChart mBarChart;
    private ChartVisibilityListener mListener;
    private ImageView mChangeChart;
    private ArrayList<PaymentList> mList;

    public CRMPaymentAdapter(ArrayList<PaymentList> list) {
        mList = list;
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public PaymentList getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        PaymentList paymentList = getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_crm_payment, null);
        }
        //协议单号
        String serialNo = paymentList.getSerialNo();
        TextView payment_serial_no = ViewHolderUtil.get(convertView, R.id.payment_serial_no);
        payment_serial_no.setText("协议单号：" + serialNo);
        //厂家名称
        String vendorName = paymentList.getVendorName();
        TextView payment_vendor_name = ViewHolderUtil.get(convertView, R.id.payment_vendor_name);
        payment_vendor_name.setText(vendorName);
        //分类名
        String categoryName = paymentList.getCategoryName();
        TextView payment_product_common_name = ViewHolderUtil.get(convertView, R.id.payment_product_common_name);
        payment_product_common_name.setText(categoryName);
        //剂型
        String dosageForm = paymentList.getDosageForm();
        TextView payment_dosage_form = ViewHolderUtil.get(convertView, R.id.payment_dosage_form);
        payment_dosage_form.setText("剂型：" + dosageForm);
        //地域
        String region = paymentList.getProvinceName() + "\t" + paymentList.getCityName() + "\t" + paymentList.getAreaName();
        TextView payment_region = ViewHolderUtil.get(convertView, R.id.payment_region);
        payment_region.setText(region);
        //规格
        String spec = paymentList.getSpec();
        TextView payment_spec = ViewHolderUtil.get(convertView, R.id.payment_spec);
        payment_spec.setText("规格：" + spec);
        //推广周期起止
        long startAt = paymentList.getStartAt();
        long endAt = paymentList.getEndAt();
        TextView payment_time = ViewHolderUtil.get(convertView, R.id.payment_time);
        payment_time.setText("推广周期：" + TimeUtil.timeStamp2Date(startAt + "", "yyyy年MM月dd日")  + "——" + TimeUtil.timeStamp2Date(endAt + "", "yyyy年MM月dd日"));

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        PaymentList paymentList = getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_crm_payment, null);
        }
        /*---------------------------折线图---------------------------*/
        mLineChart = ViewHolderUtil.get(convertView, R.id.payment_line_chart);
        setLineChart(mLineChart, paymentList);
        /*---------------------------柱状图---------------------------*/
        mBarChart = ViewHolderUtil.get(convertView, R.id.payment_bar_chart);
        setBarChart(mBarChart, paymentList);

        mChangeChart = ViewHolderUtil.get(convertView, R.id.payment_change_chart);

        if (paymentList.isShowPerMonth()) {
            showPerMonth();
        } else {
            showTotal();
        }
        mChangeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.setChartVisibility(groupPosition);
                }

            }
        });
        return convertView;
    }

    private void setBarChart(BarChart chart, PaymentList paymentList) {
        // 数据描述
        chart.setDescription("");
        // 设置是否可以触摸
        chart.setTouchEnabled(true);
        // 是否可以拖拽
        chart.setDragEnabled(true);
        // 是否可以缩放
        chart.setScaleEnabled(true);
        // 集双指缩放
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        // 隐藏右边的坐标轴
        chart.getAxisRight().setEnabled(false);
        // 隐藏左边的左边轴
//        chart.getAxisLeft().setEnabled(true);


        //X轴属性
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置粒度为1
        xAxis.setGranularity(1f);
        //设置标签居中
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(true);
        //设置X轴单位格式
        final ArrayList<Payment> paymentStatList = paymentList.getPaymentStatList();//回款统计列表
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value >= 0 && value < paymentStatList.size()) {
                    return paymentStatList.get((int) value).getStatYear() + "/" + paymentStatList.get((int) value).getStatMonth();
                } else {
                    return value + "";
                }
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        //左侧Y轴属性
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(15f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinValue(0f);

        Legend mLegend = chart.getLegend(); // 设置比例图标示
        // 设置窗体样式
        mLegend.setForm(Legend.LegendForm.SQUARE);
        // 字体
        mLegend.setFormSize(4f);
        // 字体颜色
        mLegend.setTextColor(Color.parseColor("#4188d2"));

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        for (int i = 0; i < paymentStatList.size(); i++) {
            yVals1.add(new BarEntry(i, paymentStatList.get(i).getPaymentQuota()));
            yVals2.add(new BarEntry(i, paymentStatList.get(i).getPaymentAmount()));
        }


        //数据注解
        BarDataSet barDataSet1 = new BarDataSet(yVals1, "回款指标");
        barDataSet1.setColor(Color.parseColor("#6fb8af"));

        BarDataSet barDataSet2 = new BarDataSet(yVals2, "实际回款");
        barDataSet2.setColor(Color.parseColor("#8e5b94"));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);


        BarData data = new BarData(dataSets);
        data.setValueFormatter(new LargeValueFormatter());

        //每个分组之间的距离
        float groupSpace = 0.2f;
        //一个分组内柱子之间的距离
        float barSpace = 0.02f; // x3 dataset
        //float barWidth = 0.5f; // x3 dataset
        //每个柱子的宽度必须由计算得来，1是每个组的单位宽度
        float barWidth = (float) ((1.00 - groupSpace) / 2 - barSpace);
        // (0.3 + 0.02) * 3 + 0.04 = 1.00 -> interval per "group"
        chart.setData(data);

        //最小值为第一个值
//        xAxis.setAxisMinValue(0);
        //最大值为最后的值
        xAxis.setAxisMaxValue(data.getDataSets().get(0).getEntryCount());
        data.setBarWidth(barWidth);

        //横轴缩放倍数--需要向上取整-保证前几页显示六条数据
        float scaleX = data.getDataSets().get(0).getEntryCount() / 6f ;
        chart.setScaleMinima((float) Math.ceil(scaleX), 1f);
        //设置每组柱状图
        chart.groupBars(0, groupSpace, barSpace);

        //计算当前月份和起始日期的差值
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        LogUtil.d("当前年份：" + year);
        LogUtil.d("当前月份：" + month);
        int xValue = (year - paymentStatList.get(0).getStatYear()) * 12 + (month - paymentStatList.get(0).getStatMonth()) + 1;
        chart.moveViewToX(xValue);

        chart.invalidate();
        // 动画
//        chart.animateY(1000);
    }

    private void setLineChart(LineChart chart, PaymentList paymentList) {
        //设置折线图细节
        //-设置描述
        chart.setDescription("");
        //-设置没有数据时的内容
        chart.setNoDataTextDescription("没有数据");
        //-设置触摸
        chart.setTouchEnabled(true);
        //-设置拖拽
        chart.setDragEnabled(true);
        //-设置缩放
        chart.setScaleEnabled(true);
        //-设置双指缩放
        chart.setPinchZoom(false);
        //-设置右侧Y轴不可用
        chart.getAxisRight().setEnabled(false);


        //设置X轴属性
        XAxis xAxis = chart.getXAxis();
        //设置X轴位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置粒度为1
        xAxis.setGranularity(1f);
        final ArrayList<Payment> paymentTotalStatList = paymentList.getPaymentTotalStatList();//回款统计列表
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value >= 0 && value < paymentTotalStatList.size()) {
                    return paymentTotalStatList.get((int) value).getStatYear() + "/" + paymentTotalStatList.get((int) value).getStatMonth();
                } else {
                    return value + "";
                }
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        //设置左侧Y轴
        YAxis axisLeft = chart.getAxisLeft();
        axisLeft.setDrawGridLines(true);
        axisLeft.setGranularity(20f);
        axisLeft.setAxisMinValue(0f);

        //设置比例图
        //-设置窗体样式
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        //-设置
        legend.setFormSize(6f);
        //-设置颜色
        legend.setTextColor(Color.parseColor("#4188d2"));

        //设置假数据
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        //-指标数据
        ArrayList<Entry> KPITotalList = new ArrayList<Entry>();
        //-回款数据
        ArrayList<Entry> PaymentTotalList = new ArrayList<Entry>();
        for (int i = 0; i < paymentTotalStatList.size(); i++) {
            KPITotalList.add(new Entry(i, paymentTotalStatList.get(i).getPaymentQuota()));
            PaymentTotalList.add(new Entry(i,  paymentTotalStatList.get(i).getPaymentAmount()));
        }

        //标注数据
        //-指标累计
        LineDataSet KPIDataSet = new LineDataSet(KPITotalList, "指标累计 ");
        KPIDataSet.setLineWidth(2.5f);
        KPIDataSet.setCircleRadius(4f);
        int KPIColor = Color.parseColor("#6fb8af");
        KPIDataSet.setColor(KPIColor);
        KPIDataSet.setCircleColor(KPIColor);
        dataSets.add(KPIDataSet);
        //-回款累计
        LineDataSet ShipDataSet = new LineDataSet(PaymentTotalList, "回款累计 ");
        ShipDataSet.setLineWidth(2.5f);
        ShipDataSet.setCircleRadius(4f);
        int ShipColor = Color.parseColor("#8e5b94");
        ShipDataSet.setColor(ShipColor);
        ShipDataSet.setCircleColor(ShipColor);
        dataSets.add(ShipDataSet);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        //计算X轴缩放比例--向上取整
        float scaleX = data.getDataSets().get(0).getEntryCount() / 6f;
        chart.setScaleMinima((float) Math.ceil(scaleX), 1f);

        //计算当前月份和起始日期的差值
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        LogUtil.d("当前年份：" + year);
        LogUtil.d("当前月份：" + month);
        int xValue = (year - paymentTotalStatList.get(0).getStatYear()) * 12 + (month - paymentTotalStatList.get(0).getStatMonth()) + 1;
        chart.moveViewToX(xValue);

        //设置动画--可以监听只有在打开时才触发动画
        //chart.animateY(500);
        chart.invalidate();
    }
    public void setChartVisibilityListener(ChartVisibilityListener listener){
        mListener = listener;
    }
    public interface ChartVisibilityListener{
        void setChartVisibility(int groupPosition);
    }
    public void showTotal(){
        mChangeChart.setImageResource(R.mipmap.ico_deliver_goods_choices_);
        mLineChart.setVisibility(View.VISIBLE);
        mBarChart.setVisibility(View.GONE);
    }
    public void showPerMonth(){
        mChangeChart.setImageResource(R.mipmap.ico_deliver_goods_choices);
        mLineChart.setVisibility(View.GONE);
        mBarChart.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
