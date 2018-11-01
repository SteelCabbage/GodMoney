package chinapex.com.godmoney.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * @author SteelCabbage
 * @date 2018/11/01
 */
public class PhoneUtils {

    private static final String TAG = PhoneUtils.class.getSimpleName();

    public static void copy2Clipboard(Context context, String copyContent) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (null == clipboardManager) {
            CpLog.e(TAG, "clipboardManager is null!");
            return;
        }

        ClipData clipData = ClipData.newPlainText("text", copyContent);
        clipboardManager.setPrimaryClip(clipData);
    }
}
