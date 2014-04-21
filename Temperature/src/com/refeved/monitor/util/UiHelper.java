package com.refeved.monitor.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.refeved.monitor.R;

public class UiHelper {
    
    /**
     * 获取屏幕显示的宽度和高度
     * @param context 上下文
     * @return 屏幕显示的宽度和高度
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * 点击图片变暗效果
     * 
     * @param imageView
     *            图片
     * @param brightness
     *            亮度
     */
    public static void changeLight(ImageView imageView, int brightness) {
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1, 0, 0,
                brightness, 0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });// 改变亮度
        imageView.setColorFilter(new ColorMatrixColorFilter(cMatrix));
    }

    public static boolean isGifImage(String imageUrl) {
        return (!TextUtils.isEmpty(imageUrl) && (imageUrl.endsWith(".gif") || imageUrl
                .endsWith(".GIF")));
    }

    private static Toast noNetToast;

    public static boolean showNoNetToast(final Activity activity) {
        return showNoNetToast(activity, false);
    }

    public static boolean showNoNetToast(final Activity activity,
            final boolean flag) {
        if (activity != null && !activity.isFinishing()
                && !NetUtil.isNetworkAvailable(activity)) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (noNetToast == null) {
                        noNetToast = Toast.makeText(activity, R.string.no_net,
                                Toast.LENGTH_LONG);
                    } else {
                        noNetToast.setText(R.string.no_net);
                    }
                    if (!flag) {
                        noNetToast.show();
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

}

