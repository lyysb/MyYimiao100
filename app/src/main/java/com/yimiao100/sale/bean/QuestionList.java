package com.yimiao100.sale.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 *
 * Created by 亿苗通 on 2016/10/18.
 */
public class QuestionList implements Parcelable {
    private int id;
    private String title;
    private String answer;
    private int score;
    private ArrayList<Option> optionList;

    //判断对错
    private boolean right;

    //判断是否已经回答过
    private boolean answered;

    //选中的位置
    private int chooseAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Option> getOptionList() {
        return optionList;
    }

    public void setOptionList(ArrayList<Option> optionList) {
        this.optionList = optionList;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public int getChooseAt() {
        return chooseAt;
    }

    public void setChooseAt(int chooseAt) {
        this.chooseAt = chooseAt;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.answer);
        dest.writeInt(this.score);
        dest.writeList(this.optionList);
        dest.writeByte(right ? (byte) 1 : (byte) 0);
        dest.writeByte(answered ? (byte) 1 : (byte) 0);
        dest.writeInt(this.chooseAt);
    }

    public QuestionList() {
    }

    protected QuestionList(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.answer = in.readString();
        this.score = in.readInt();
        this.optionList = new ArrayList<Option>();
        in.readList(this.optionList, Option.class.getClassLoader());
        this.right = in.readByte() != 0;
        this.answered = in.readByte() != 0;
        this.chooseAt = in.readInt();
    }

    public static final Parcelable.Creator<QuestionList> CREATOR = new Parcelable.Creator<QuestionList>() {
        public QuestionList createFromParcel(Parcel source) {
            return new QuestionList(source);
        }

        public QuestionList[] newArray(int size) {
            return new QuestionList[size];
        }
    };
}
