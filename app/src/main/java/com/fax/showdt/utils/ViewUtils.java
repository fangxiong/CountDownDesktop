package com.fax.showdt.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Build;
import android.os.ResultReceiver;
import android.text.Editable;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.internal.NavigationMenuPresenter;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Field;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ViewUtils {

    public static final int IMG_SCALE_MATRIX = 0;
    public static final int IMG_SCALE_FIT_XY = 1;
    public static final int IMG_SCALE_FIT_START = 2;
    public static final int IMG_SCALE_FIT_CENTER = 3;
    public static final int IMG_SCALE_FIT_END = 4;
    public static final int IMG_SCALE_CENTER = 5;
    public static final int IMG_SCALE_CENTER_CROP = 6;
    public static final int IMG_SCALE_CENTER_INSIDE = 7;

    private static final int INT_UNDEFINED = -1;
    private static final int INT_TRUE = 1;
    private static final int INT_FALSE = 0;

    private final static Object sCacheValueSync = new Object();

    private static int sNavigationBarHeight = -1;
    private static int sMiuiVersion = -1;
    private static int sStatusBarHeight = -1;

    private static int sHasFlymeSmartBar = INT_UNDEFINED;

    private static int sHasNavigationBar = INT_UNDEFINED;

//    public static void setBackgroundDrawableKeepPadding(View view, Drawable drawable) {
//        int left = view.getPaddingLeft();
//        int top = view.getPaddingTop();
//        int right = view.getPaddingRight();
//        int bottom = view.getPaddingBottom();
//        ResourcesCompat.setBackgroundDrawable(view, drawable);
//        view.setPadding(left, top, right, bottom);
//    }

    public static void closeHardWare(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            closeHardWareHoneycomb(view);
        }
    }

    public static void closeHardWare(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            closeHardWareHoneycomb(drawable);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void closeHardWareHoneycomb(Drawable drawable) {
        Callback callback = drawable.getCallback();
        if (callback instanceof View) {
            closeHardWareHoneycomb((View) callback);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void closeHardWareHoneycomb(View view) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    /**
     * 禁止父View的触摸事件
     *
     * @param view
     */
    public static void attemptClaimDrag(View view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    public static void removeOnGlobalLayoutListener(ViewTreeObserver observer,
                                                    ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            observer.removeGlobalOnLayoutListener(listener);
        } else {
            observer.removeOnGlobalLayoutListener(listener);
        }
    }

    /**
     * 如果在弹窗上无法打开输入法, 请使用 {@link #showInputMethodDelayed}
     *
     * @param editText
     */
    public static void showInputMethod(EditText editText) {
        showInputMethod(editText, false);
    }

    /**
     * 如果在弹窗上无法打开输入法, 请使用 {@link #showInputMethodDelayed}
     *
     * @param editText
     */
    public static void showInputMethod(EditText editText, boolean selectAll) {
        InputMethodManager im = (InputMethodManager) editText.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (im != null) {
            try {
                editText.requestFocus();
                Editable editanle = editText.getText();
                if (editanle != null) {
                    editText.setSelection(editanle.length());
                }
                im.showSoftInput(editText, 0);
            } catch (Exception ex) {
            }
        }
        if (selectAll) {
            editText.selectAll();
        }
    }

    /**
     * 在弹窗上的EditText上打开输入法需要延时触发, 不然显示不出来
     *
     * @param editText
     * @param delayed
     */
    public static void showInputMethodDelayed(final EditText editText, final boolean selectAll,
                                              int delayed) {
        editText.postDelayed(new Runnable() {

            @Override
            public void run() {
                showInputMethod(editText, selectAll);
            }
        }, delayed);
    }

    public static void hideInputMethod(EditText editText) {
        InputMethodManager im = (InputMethodManager) editText.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (im != null && im.isActive(editText)) {
            try {
                im.hideSoftInputFromWindow(editText.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception ex) {
            }
        }
    }

    public static void hideInputMethod(EditText editText, ResultReceiver resultReceiver) {
        InputMethodManager im = (InputMethodManager) editText.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (im != null && im.isActive(editText)) {
            try {
                im.hideSoftInputFromWindow(editText.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS, resultReceiver);
            } catch (Exception ex) {
            }
        }
    }


    public static void hideSoftKeyboard(Context context, List<View> viewList) {
        if (viewList == null) return;

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        for (View v : viewList) {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static boolean removeFromParent(View view) {
        if (view != null) {
            final ViewParent parent = view.getParent();
            if (parent != null && (parent instanceof ViewGroup)) {
                ((ViewGroup) parent).removeView(view);
                return view.getParent() == null;
            }
        }
        return false;
    }

    public static int getScreenWithSize(Context context) {
        int screenSize = 0;
        WindowManager configMgr = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        if (configMgr != null) {
            screenSize = configMgr.getDefaultDisplay().getWidth();
            if (screenSize != 0) {
                return screenSize;
            }
        }
        return 0;
    }


    public static void playFeedBack(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
                        | HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        );
    }

    /**
     * 屏幕全屏/非全屏切换*
     */
    public static void trySetFullScreen(boolean fullScreen, Window window) {
        setFullScreenMode(fullScreen, window);
    }

    //全屏非全屏切换
    public static void setFullScreenMode(boolean fullScreen, Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        boolean isFullScreen = (params.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (isFullScreen == fullScreen) {
            return;
        }
        if (fullScreen) {
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //添加
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            }
        }
        window.setAttributes(params);
    }

    //=============适配

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static void hideNavigationBar(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (isNavigationBarShowing()) {
//                View decorView = activity.getWindow().getDecorView();
//                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//            }
//        }
//    }

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static void showNavigationBar(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (isNavigationBarShowing()) {
//                View decorView = activity.getWindow().getDecorView();
//                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility()
//                        & (~(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)));
//            }
//        }
//    }

//    public static boolean isNavigationBarShowing() {
//        boolean hasNavigationBar = false;
//        IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
//        if (wm != null) {
//            Object result = ReflectUtils.invoke(wm, "hasNavigationBar");
//
//            if (result != null && (result instanceof Boolean)) {
//                hasNavigationBar = (boolean) result;
//            }
//        }
//        return hasNavigationBar;
//    }

    public static int getStatusBarHeight() {
        int statusBarHeight;
        synchronized (sCacheValueSync) {
            statusBarHeight = sStatusBarHeight;
        }

        if (statusBarHeight >= 0) {
            return statusBarHeight;
        }

        statusBarHeight = 0;

        try {
            Resources res = Resources.getSystem();
            int identifier = res.getIdentifier("status_bar_height", "dimen", "android");
            if (identifier != 0) {
                statusBarHeight = res.getDimensionPixelOffset(identifier);
            }
        } catch (Exception ex) {
        }
        synchronized (sCacheValueSync) {
            sStatusBarHeight = statusBarHeight;
        }
        return statusBarHeight;
    }

    public static int getNavigationBarHeight() {
        int navigationHeight;
        synchronized (sCacheValueSync) {
            navigationHeight = sNavigationBarHeight;
        }

        if (navigationHeight >= 0) {
            return navigationHeight;
        }
        navigationHeight = 0;
        try {
            Resources res = Resources.getSystem();
            int identifier = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (identifier != 0) {
                navigationHeight = res.getDimensionPixelOffset(identifier);
            }
        } catch (Exception ex) {
        }
        synchronized (sCacheValueSync) {
            sNavigationBarHeight = navigationHeight;
        }
        return navigationHeight;
    }

//    public static boolean hasNavigationBar() {
//        int hasNavigationBar;
//        synchronized (sCacheValueSync) {
//            hasNavigationBar = sHasNavigationBar;
//        }
//        if (hasNavigationBar != INT_UNDEFINED) {
//            return hasNavigationBar == INT_TRUE;
//        }
//        try {
//            IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
//            Boolean result = ReflectUtils.invoke(wm, "hasNavigationBar");
//            if (result != null) {
//                hasNavigationBar = result ? INT_TRUE : INT_FALSE;
//            }
//        } catch (Exception ex) {
//        }
//        if (hasNavigationBar == INT_UNDEFINED) {
//            hasNavigationBar = INT_FALSE;
//            try {
//                Resources res = Resources.getSystem();
//                int identifier = res.getIdentifier("config_showNavigationBar", "bool", "android");
//                if (identifier != 0) {
//                    hasNavigationBar = res.getBoolean(identifier) ? INT_TRUE : INT_FALSE;
//                }
//            } catch (Exception ex) {
//            }
//        }
//        synchronized (sCacheValueSync) {
//            sHasNavigationBar = hasNavigationBar;
//        }
//        return hasNavigationBar == INT_TRUE;
//    }


    //添加顶部View MarginTop=statusBarHeight
    public static void addTopStatusBarMargin(View view) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.setMargins(lp.leftMargin, getStatusBarHeight(), lp.rightMargin, lp.bottomMargin);
        view.setLayoutParams(lp);
    }

    //添加底部View MarginBottom=NavigationBarHeight
    public static void addBottomNavigationMargin(View view) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, getNavigationBarHeight());
        view.setLayoutParams(lp);
    }

    public static void setTextViewUnderLine(TextView tv) {
        Paint paint = tv.getPaint();
        paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        paint.setAntiAlias(true);
    }

    public static ImageView.ScaleType intToImageScaleType(int type) {
        switch (type) {
            case IMG_SCALE_MATRIX: {
                return ImageView.ScaleType.MATRIX;
            }
            case IMG_SCALE_FIT_XY: {
                return ImageView.ScaleType.FIT_XY;
            }
            case IMG_SCALE_FIT_START: {
                return ImageView.ScaleType.FIT_START;
            }
            case IMG_SCALE_FIT_CENTER: {
                return ImageView.ScaleType.FIT_CENTER;
            }
            case IMG_SCALE_FIT_END: {
                return ImageView.ScaleType.FIT_END;
            }
            case IMG_SCALE_CENTER: {
                return ImageView.ScaleType.CENTER;
            }
            case IMG_SCALE_CENTER_CROP: {
                return ImageView.ScaleType.CENTER_CROP;
            }
            case IMG_SCALE_CENTER_INSIDE: {
                return ImageView.ScaleType.CENTER_INSIDE;
            }
            default: {
                return ImageView.ScaleType.CENTER_CROP;
            }
        }
    }

    public static Point getViewPositionInView(View view, View inView) {
        final int[] data = new int[2];
        view.getLocationOnScreen(data);
        final int x0 = data[0];
        final int y0 = data[1];

        inView.getLocationOnScreen(data);
        final int x1 = data[0];
        final int y1 = data[1];

        return new Point(x0 - x1, y0 - y1);
    }

    public static void setVisibilityWithCheck(View view, int visibility) {
        final int value = view.getVisibility();
        if (visibility != value) {
            view.setVisibility(visibility);
        }
    }


    //今日头条适配后的计算方式(以后统一使用这种计算方式)
    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //今日头条适配后的计算方式(以后统一使用这种计算方式)
    public static int sp2px(final float spValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public static int getSizeByScreenWidth(int screenWidth ,int size){
        return (int)(screenWidth * ((float)size/720));
    }

    public static int getSizeByScreenHeight(int screenHeight, int size){
        return (int)(screenHeight * ((float)size/1280));
    }

    public static void setNavigationMenuLineStyle(NavigationView navigationView, @ColorInt final int color, final int height) {
        try {
            Field fieldByPressenter = navigationView.getClass().getDeclaredField("mPresenter");
            fieldByPressenter.setAccessible(true);
            NavigationMenuPresenter menuPresenter = (NavigationMenuPresenter) fieldByPressenter.get(navigationView);
            Field fieldByMenuView = menuPresenter.getClass().getDeclaredField("mMenuView");
            fieldByMenuView.setAccessible(true);
            final NavigationMenuView mMenuView = (NavigationMenuView) fieldByMenuView.get(menuPresenter);
            mMenuView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    RecyclerView.ViewHolder viewHolder = mMenuView.getChildViewHolder(view);
                    if (viewHolder != null && "SeparatorViewHolder".equals(viewHolder.getClass().getSimpleName()) && viewHolder.itemView != null) {
                        if (viewHolder.itemView instanceof FrameLayout) {
                            FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
                            View line = frameLayout.getChildAt(0);
                            line.setBackgroundColor(color);
                            line.getLayoutParams().height = height;
                            line.setLayoutParams(line.getLayoutParams());
                        }
                    }
                }

                @Override
                public void onChildViewDetachedFromWindow(View view) {

                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }



}
