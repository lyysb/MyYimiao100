package com.yimiao100.sale.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 厂家bean
 * Created by 亿苗通 on 2016/10/30.
 */
public class Vendor implements Parcelable {
    private int id;
    private String vendorName;
    private String logoImageUrl;
    private int tipStatus;



    public int getTipStatus() {
        return tipStatus;
    }

    public void setTipStatus(int tipStatus) {
        this.tipStatus = tipStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getLogoImageUrl() {
        return logoImageUrl;
    }

    public void setLogoImageUrl(String logoImageUrl) {
        this.logoImageUrl = logoImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.vendorName);
        dest.writeString(this.logoImageUrl);
        dest.writeInt(this.tipStatus);
    }

    public Vendor() {
    }

    protected Vendor(Parcel in) {
        this.id = in.readInt();
        this.vendorName = in.readString();
        this.logoImageUrl = in.readString();
        this.tipStatus = in.readInt();
    }

    public static final Parcelable.Creator<Vendor> CREATOR = new Parcelable.Creator<Vendor>() {
        public Vendor createFromParcel(Parcel source) {
            return new Vendor(source);
        }

        public Vendor[] newArray(int size) {
            return new Vendor[size];
        }
    };
}
