package com.yimiao100.sale.bean;

/**
 * Created by Michel on 2016/12/29.
 */

public class JPushBean {
    private String message_type;
    private int target_id;

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public int getTarget_id() {
        return target_id;
    }

    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }
}
