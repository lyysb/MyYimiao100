package com.yimiao100.sale.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.yimiao100.sale.activity.BindPersonalActivity;
import com.yimiao100.sale.activity.PersonalAddressAddActivity;
import com.yimiao100.sale.bean.Event;
import com.yimiao100.sale.bean.EventType;
import com.yimiao100.sale.service.AliasService;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

/**
 * 纯工具
 * Created by 亿苗通 on 2016/9/14.
 */
public class Util {
    public Util() {
         /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static String getApkPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/VaccineCircle";
    }

    public static File createFile(String destFileName) {
        File dir = new File(Util.getApkPath());
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, destFileName);
        LogUtil.d("file exit? + " + file.exists());
        return file;
    }

    /**
     * @return 生成唯一key
     */
    public static String generateSeriaNo() {
        return UUID.randomUUID().toString();
    }

    /**
     * 计算税额
     *
     * @param money
     * @return
     */
    public static double getTex(double money) {
//        1、每次收入＜4000元的
//        应纳税额=（每次收入额-800）*20%
//        2、每次收入≥4000元以上的
//        应纳税额=每次收入额*（1-20%）*20%
//        3、每次收入≥25000元 以上的
//        应纳税额=每次收入额*（1-20）*30%-2000
//        4、每次收入≥62500以上的
//        应纳税额=每次收入额*（1-20）*40%-7000
        if (money >= 62500) {
            return money * 0.8 * 0.4 - 7000;
        } else if (money >= 25000) {
            return money * 0.8 * 0.3 - 2000;
        } else if (money >= 4000) {
            return money * 0.8 * 0.2;
        } else if (money >= 800) {
            return (money - 800) * 0.2;
        } else {
            return 0;
        }
    }

    /**
     * 提示错误信息
     *
     * @param activity
     * @param reason
     */
    public static void showError(final Activity activity, int reason) {
        if (reason == 116) {
            // 异地登录
            AlertDialog dialog = ActivityAlertDialogManager.getDialog(activity);
            // 限制只弹窗一次，以下操作只执行一次
            if (dialog.isShowing()) {
                return;
            }
            // step1：弹窗显示退出/重新登录
            showDialog(activity, dialog);
            // step2：清空本地所有数据
            SharePreferenceUtil.clear(activity);
            // step3：启动别名服务
            activity.startService(new Intent(activity, AliasService.class));
        } else {
            ToastUtil.showShort(activity, Constant.ERROR_INFORMATION.get(reason));
        }
    }

    /**
     * 弹窗显示退出/重新登录
     * @param activity
     * @param dialog
     */
    private static void showDialog(Activity activity, AlertDialog dialog) {
        LogUtil.d("dialog:" + dialog.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed()) {
                return;
            }
        }
        dialog.show();
    }

    public static void showTimeOutNotice(Activity activity) {
        ToastUtil.showShort(activity, "亲的网络状况不太好呢~请稍后重试");
    }

    /**
     * 显示个人信息不完整的Dialog
     *
     * @param activity
     */
    public static void showPersonalDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("信息不完整");
        builder.setMessage("请绑定个人银行卡");
        builder.setPositiveButton("去绑定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.startActivity(new Intent(activity, BindPersonalActivity.class));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * 显示收获地址不完整Dialog
     *
     * @param activity
     */
    public static void showAddressDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("未指定收货地址");
        builder.setMessage("请设置收获地址");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.startActivityForResult(new Intent(activity, PersonalAddressAddActivity
                        .class), 100);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 进入客服界面
     */
    public static void enterCustomerService(final Context context) {
        int customerId = (int) SharePreferenceUtil.get(context, Constant.USERID, 0);
        String userName = (String) SharePreferenceUtil.get(context, Constant.CNNAME, "未知");
        String userIcon = (String) SharePreferenceUtil.get(context, Constant.PROFILEIMAGEURL,
                "http://oduhua0b1.bkt.clouddn.com/default_avatar.png");
        String accountNumber = (String) SharePreferenceUtil.get(context, Constant.ACCOUNT_NUMBER,
                "未知");
        HashMap<String, String> info = new HashMap<>();
        info.put("name", userName);
        info.put("avatar", userIcon);
        info.put("tel", accountNumber);
        // 初始化美洽
        MQConfig.init(context, Constant.MEI_QIA_APP_KEY, new OnInitCallback() {
            @Override
            public void onSuccess(String clientId) {
                Toast.makeText(context, "欢迎进入客服咨询", Toast.LENGTH_SHORT).show();
                // 发送消息已读
                Event event = new Event();
                Event.eventType = EventType.READ_MSG;
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(context, "客服系统初始化失败，请稍后重试", Toast.LENGTH_SHORT).show();
                LogUtil.d("code = " + code + ", msg is " + message);
            }
        });
        Intent intent = new MQIntentBuilder(context)
                .setCustomizedId(customerId + "")        // 相同的id会被识别为同一个顾客
                .setClientInfo(info)
                .build();

        context.startActivity(intent);
    }

}
