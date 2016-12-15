package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * Created by 亿苗通 on 2016/11/5.
 */
public class CarouselBean {
    private String status;
    private int reason;
    private ArrayList<Carousel> carouselList;

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

    public ArrayList<Carousel> getCarouselList() {
        return carouselList;
    }

    public void setCarouselList(ArrayList<Carousel> carouselList) {
        this.carouselList = carouselList;
    }
}
