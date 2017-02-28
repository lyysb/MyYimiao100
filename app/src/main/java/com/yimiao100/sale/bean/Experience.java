package com.yimiao100.sale.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 推广经历
 * Created by 亿苗通 on 2016/11/29.
 */

public class Experience implements Parcelable {
    private int provinceId;
    private int cityId;
    private String provinceName;
    private String cityName;
    private String startAtFormat;
    private String endAtFormat;
    private String productName;
    private String serialNo;



    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStartAtFormat() {
        return startAtFormat;
    }

    public void setStartAtFormat(String startAtFormat) {
        this.startAtFormat = startAtFormat;
    }

    public String getEndAtFormat() {
        return endAtFormat;
    }

    public void setEndAtFormat(String endAtFormat) {
        this.endAtFormat = endAtFormat;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.provinceId);
        dest.writeInt(this.cityId);
        dest.writeString(this.provinceName);
        dest.writeString(this.cityName);
        dest.writeString(this.startAtFormat);
        dest.writeString(this.endAtFormat);
        dest.writeString(this.productName);
        dest.writeString(this.serialNo);
    }

    public Experience() {
    }

    protected Experience(Parcel in) {
        this.provinceId = in.readInt();
        this.cityId = in.readInt();
        this.provinceName = in.readString();
        this.cityName = in.readString();
        this.startAtFormat = in.readString();
        this.endAtFormat = in.readString();
        this.productName = in.readString();
        this.serialNo = in.readString();
    }

    public static final Parcelable.Creator<Experience> CREATOR = new Parcelable.Creator<Experience>() {
        public Experience createFromParcel(Parcel source) {
            return new Experience(source);
        }

        public Experience[] newArray(int size) {
            return new Experience[size];
        }
    };
}
