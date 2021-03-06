package com.yimiao100.sale.base;

import android.app.Activity;
import com.yimiao100.sale.bean.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity管理类
 * Created by 亿苗通 on 2016/10/19.
 */
public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<Activity>();
    private static BaseActivity topActivity;

    private ActivityCollector() {
         /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
//            System.exit(0);
        }
    }

    public static void setTopActivity(BaseActivity activity) {
        topActivity = activity;
    }

    public static BaseActivity getTopActivity() {
        if (topActivity != null) {
            return topActivity;
        }
        return null;
    }
}
