package chinapex.com.godmoney.global;

import android.app.Application;

import chinapex.com.godmoney.task.TaskController;

/**
 * Created by SteelCabbage on 2018/7/18 0018 18:28.
 * E-Mail：liuyi_61@163.com
 */

public class GodMoneyApplication extends Application {
    private static GodMoneyApplication sGodMoneyApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sGodMoneyApplication = this;
        TaskController.getInstance().doInit();
    }

    public static GodMoneyApplication getInstance() {
        return sGodMoneyApplication;
    }
}
