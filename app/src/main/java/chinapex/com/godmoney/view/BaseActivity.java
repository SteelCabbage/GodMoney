package chinapex.com.godmoney.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.lang.reflect.Field;

import chinapex.com.godmoney.utils.CpLog;

/**
 * Created by SteelCabbage on 2018/5/21 0021.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    /***是否显示状态栏*/
    private boolean mIsShowStatusBar = false;
    private boolean mIsBlackStatusBar = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //沉浸式状态栏
        if (!mIsShowStatusBar) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                    Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                    field.setAccessible(true);
                    field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);
                } catch (IllegalAccessException e) {
                    CpLog.e(TAG, "onCreate() -> IllegalAccessException:" + e.getMessage());
                } catch (NoSuchFieldException e) {
                    CpLog.e(TAG, "onCreate() -> NoSuchFieldException:" + e.getMessage());
                } catch (ClassNotFoundException e) {
                    CpLog.e(TAG, "onCreate() -> ClassNotFoundException:" + e.getMessage());
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mIsBlackStatusBar) {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
            }
        }
    }
}
