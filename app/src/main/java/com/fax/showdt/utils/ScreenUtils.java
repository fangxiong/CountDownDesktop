package com.fax.showdt.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.fax.showdt.AppContext;
import com.gyf.barlibrary.ImmersionBar;

import androidx.annotation.NonNull;

import static android.view.View.NO_ID;

public class ScreenUtils {
    private static final String NAVIGATION= "navigationBarBackground";
    /**
     * Adapt the screen for vertical slide.
     *
     * @param activity        The activity.
     * @param designWidthInPx The size of design diagram's width, in pixel.
     */
    public static void adaptScreen4VerticalSlide(final Activity activity,
                                                 final int designWidthInPx) {
        adaptScreen(activity, designWidthInPx, true);
    }

    /**
     * Adapt the screen for horizontal slide.
     *
     * @param activity         The activity.
     * @param designHeightInPx The size of design diagram's height, in pixel.
     */
    public static void adaptScreen4HorizontalSlide(final Activity activity,
                                                   final int designHeightInPx) {
        adaptScreen(activity, designHeightInPx, false);
    }

    /**
     * Reference from: https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA
     */
    private static void adaptScreen(final Activity activity,
                                    final int sizeInPx,
                                    final boolean isVerticalSlide) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = AppContext.get().getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        if (isVerticalSlide) {
            activityDm.density = activityDm.widthPixels / (float) sizeInPx;
        } else {
            activityDm.density = activityDm.heightPixels / (float) sizeInPx;
        }
        activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density);
        activityDm.densityDpi = (int) (160 * activityDm.density);

        appDm.density = activityDm.density;
        appDm.scaledDensity = activityDm.scaledDensity;
        appDm.densityDpi = activityDm.densityDpi;
    }

    /**
     * Cancel adapt the screen.
     *
     * @param activity The activity.
     */
    public static void cancelAdaptScreen(final Activity activity) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = AppContext.get().getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        activityDm.density = systemDm.density;
        activityDm.scaledDensity = systemDm.scaledDensity;
        activityDm.densityDpi = systemDm.densityDpi;

        appDm.density = systemDm.density;
        appDm.scaledDensity = systemDm.scaledDensity;
        appDm.densityDpi = systemDm.densityDpi;
    }

    /**
     * Return whether adapt screen.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAdaptScreen() {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = AppContext.get().getResources().getDisplayMetrics();
        return systemDm.density != appDm.density;
    }

    public interface OnNavigationStateListener{
        void onNavigationState(boolean isShowing, int bootom);
    }

    // 该方法需要在View完全被绘制出来之后调用，否则判断不了
    //在比如 onWindowFocusChanged（）方法中可以得到正确的结果
    public static  boolean isNavigationBarExist(@NonNull Activity activity){
        ViewGroup vp = (ViewGroup) activity.getWindow().getDecorView();
        if (vp != null) {
            for (int i = 0; i < vp.getChildCount(); i++) {
                vp.getChildAt(i).getContext().getPackageName();
                if (vp.getChildAt(i).getId()!= NO_ID && NAVIGATION.equals(activity.getResources().getResourceEntryName(vp.getChildAt(i).getId()))) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void isNavigationBarExist(final Activity activity, final OnNavigationStateListener onNavigationStateListener) {
        if (activity == null) {
            return;
        }
        final int height = getNavigationHeight(activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            activity.getWindow().getDecorView().setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets windowInsets) {
                    boolean isShowing = false;
                    int b = 0;
                    if (windowInsets != null) {
                        b = windowInsets.getSystemWindowInsetBottom();
                        isShowing = (b == height);
                    }
                    if (onNavigationStateListener != null && b <= height) {
                        if (RomUtils.isMiui() && isXiaoMiNavigationBarShow(activity)) {
                            isShowing = true;
                        }
                        onNavigationStateListener.onNavigationState(isShowing, b);
                    }
                    return windowInsets;
                }
            });
        }else {
            ImmersionBar.isNavigationAtBottom(activity);
        }
    }

    public static int getNavigationHeight(Context activity) {
        if (activity == null) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        int height = 0;
        if (resourceId > 0) {
            //获取NavigationBar的高度
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static boolean isXiaoMiNavigationBarShow(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0) {
                //开启手势，不显示虚拟键
                return false;
            }
        }
        return true;
    }


}
