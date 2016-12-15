package com.yimiao100.sale.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 考试题目的选项
 * Created by 亿苗通 on 2016/11/3.
 */
public class Option implements Parcelable {
    private String id;
    private String desc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.desc);
    }

    public Option() {
    }

    protected Option(Parcel in) {
        this.id = in.readString();
        this.desc = in.readString();
    }

    public static final Parcelable.Creator<Option> CREATOR = new Parcelable.Creator<Option>() {
        public Option createFromParcel(Parcel source) {
            return new Option(source);
        }

        public Option[] newArray(int size) {
            return new Option[size];
        }
    };
}
