package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * Created by 亿苗通 on 2016/11/4.
 */
public class ExamInfoBean {
    private String status;
    private int reason;
    private ExamInfoStat stat;
    private ArrayList<ExamInfo> statList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public ExamInfoStat getStat() {
        return stat;
    }

    public void setStat(ExamInfoStat stat) {
        this.stat = stat;
    }

    public ArrayList<ExamInfo> getStatList() {
        return statList;
    }

    public void setStatList(ArrayList<ExamInfo> statList) {
        this.statList = statList;
    }
}
