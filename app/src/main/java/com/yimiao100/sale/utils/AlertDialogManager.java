package com.yimiao100.sale.utils;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import com.yimiao100.sale.R;
import org.jetbrains.annotations.NotNull;

/**
 * 暂未考虑多次弹出问题
 * Created by michel on 2017/11/9.
 */
public class AlertDialogManager {


    private Activity activity;
    private String message;
    private PositiveClickListener listener;
    private AlertDialog dialog;

    public AlertDialogManager(Builder builder) {
        this.activity = builder.activity;
        this.message = builder.message;
        this.listener = builder.listener;
        this.dialog = getDialog();
    }

    private AlertDialog getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.dialog);

        setBuilder(builder);

        return builder.create();
    }

    private void setBuilder(AlertDialog.Builder builder) {
        View v = View.inflate(activity, R.layout.dialog_normal, null);
        TextView msg = (TextView) v.findViewById(R.id.dialog_normal_msg);
        msg.setText(message);
        v.findViewById(R.id.dialog_normal_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        v.findViewById(R.id.dialog_normal_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPositiveClick();
                }
                dialog.dismiss();
            }
        });
        builder.setView(v);
    }

    public void show() {
        dialog.show();
    }


    public static class Builder{

        private Activity activity;
        private String message;
        private PositiveClickListener listener;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setMsg(String message) {
            this.message = message;
            return this;
        }

        public Builder setOnPositiveClickListener(PositiveClickListener listener) {
            this.listener = listener;
            return this;
        }

        public AlertDialogManager build() {
            return new AlertDialogManager(this);
        }
    }


    public interface PositiveClickListener {
        void onPositiveClick();
    }

}
