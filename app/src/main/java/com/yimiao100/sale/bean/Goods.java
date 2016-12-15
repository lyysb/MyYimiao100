package com.yimiao100.sale.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 亿苗通 on 2016/11/11.
 */
public class Goods implements Parcelable {
    private int id;
    private int categoryId;
    private String goodsName;
    private String categoryName;
    private int integralValue;
    private double unitPrice;
    private String goodsImageUrl;
    private String imageUrl;
    private long createdAt;
    private long updatedAt;


    public String getGoodsImageUrl() {
        return goodsImageUrl;
    }

    public void setGoodsImageUrl(String goodsImageUrl) {
        this.goodsImageUrl = goodsImageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getIntegralValue() {
        return integralValue;
    }

    public void setIntegralValue(int integralValue) {
        this.integralValue = integralValue;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.categoryId);
        dest.writeString(this.goodsName);
        dest.writeString(this.categoryName);
        dest.writeInt(this.integralValue);
        dest.writeDouble(this.unitPrice);
        dest.writeString(this.imageUrl);
        dest.writeLong(this.createdAt);
        dest.writeLong(this.updatedAt);
    }

    public Goods() {
    }

    protected Goods(Parcel in) {
        this.id = in.readInt();
        this.categoryId = in.readInt();
        this.goodsName = in.readString();
        this.categoryName = in.readString();
        this.integralValue = in.readInt();
        this.unitPrice = in.readDouble();
        this.imageUrl = in.readString();
        this.createdAt = in.readLong();
        this.updatedAt = in.readLong();
    }

    public static final Parcelable.Creator<Goods> CREATOR = new Parcelable.Creator<Goods>() {
        public Goods createFromParcel(Parcel source) {
            return new Goods(source);
        }

        public Goods[] newArray(int size) {
            return new Goods[size];
        }
    };
}
