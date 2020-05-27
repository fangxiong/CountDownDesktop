package com.fax.showdt.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.showdt.ConstantString;
import com.fax.showdt.R;
import com.fax.showdt.bean.AppInfo;
import com.fax.showdt.bean.AppInfoData;
import com.fax.showdt.callback.AppIconSelectCallback;
import com.fax.showdt.callback.WidgetEditClickCallback;
import com.fax.showdt.callback.WidgetEditStickerCallback;
import com.fax.showdt.fragment.widgetStickerEdit.WidgetLocalAppIconFragment;
import com.fax.showdt.fragment.widgetStickerEdit.WidgetStickerPropertiesEditFragment;
import com.fax.showdt.utils.BitmapUtils;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.Environment;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.view.sticker.DrawableSticker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WidgetStickerEditFragment extends Fragment implements View.OnClickListener {
    private ImageView mIvLocal, mAdd, mTouch,mAppIcon, mConsume;
    private WidgetEditStickerCallback mCallback;
    private WidgetStickerPropertiesEditFragment propertiesEditFragment;
    private WidgetLocalAppIconFragment widgetLocalAppIconFragment;
    private WidgetClickSettingFragment mTouchEditFragment;
    private List<View> mViews = new ArrayList<>();
    private DrawableSticker drawableSticker;

    enum EditStickerType {
        ELEMENT, TOUCH,APP_ICON
    }

    public WidgetStickerEditFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_sticker_edit_fragment, container, false);
        mIvLocal = view.findViewById(R.id.iv_local);
        mAdd = view.findViewById(R.id.iv_add);
        mConsume = view.findViewById(R.id.iv_consume);
        mTouch = view.findViewById(R.id.iv_touch);
        mAppIcon = view.findViewById(R.id.iv_app_icon);
        mIvLocal.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        mIvLocal.setOnClickListener(this);
        mConsume.setOnClickListener(this);
        mTouch.setOnClickListener(this);
        mAppIcon.setOnClickListener(this);
        mViews.add(mIvLocal);
        mViews.add(mTouch);
        mViews.add(mAppIcon);
        initFragment();
        refreshSelectedViewStatus(mIvLocal);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchToOneFragment(EditStickerType.ELEMENT);
    }

    @Override
    public void onClick(View view) {
        int resId = view.getId();
        if (resId == R.id.iv_consume) {
            if (mCallback != null) {
                mCallback.closePanel();
            }
        } else if (resId == R.id.iv_local) {
            switchToOneFragment(EditStickerType.ELEMENT);
            refreshSelectedViewStatus(mIvLocal);
        } else if (resId == R.id.iv_touch) {
            switchToOneFragment(EditStickerType.TOUCH);
            refreshSelectedViewStatus(mTouch);

        } else if (resId == R.id.iv_add) {
            if (mCallback != null) {
                mCallback.onPickPhoto();
            }
        }else if(resId == R.id.iv_app_icon){
            switchToOneFragment(EditStickerType.APP_ICON);
            refreshSelectedViewStatus(mAppIcon);
        }
    }

    private void refreshSelectedViewStatus(View view) {

        for (View views : mViews) {
            views.setSelected(false);
            Log.i("test_select:", "unselected");
        }
        view.setSelected(true);
    }

    private void initFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        propertiesEditFragment = new WidgetStickerPropertiesEditFragment();
        mTouchEditFragment = new WidgetClickSettingFragment();
        widgetLocalAppIconFragment = new WidgetLocalAppIconFragment();
        propertiesEditFragment.setSticker(drawableSticker);
        transaction.add(R.id.fl_sticker_edit_body, propertiesEditFragment);
        transaction.add(R.id.fl_sticker_edit_body, mTouchEditFragment);
        transaction.add(R.id.fl_sticker_edit_body, widgetLocalAppIconFragment);
        transaction.commitAllowingStateLoss();

        mTouchEditFragment.setEditClickCallback(new WidgetEditClickCallback() {
            @Override
            public void onActionType(String actionType) {
                if (drawableSticker != null) {
                    drawableSticker.setJumpAppPath(actionType);
                }
            }

            @Override
            public void onActionContent(String actionContent,String appName) {
                if (drawableSticker != null) {
                    drawableSticker.setJumpContent(actionContent);
                    drawableSticker.setAppName(appName);
                }
            }
        });

        widgetLocalAppIconFragment.setElementCallback(new AppIconSelectCallback() {
            @Override
            public void select(AppInfoData appInfo) {
                String fileName = appInfo.getName();
                String mAppIconPath = Environment.getHomeDir() + File.separator + ConstantString.DEFAULT_STICKER_APP_ICON_DRAWABLE_PATH;
                File file = new File(mAppIconPath, fileName);
                if (FileExUtils.checkWritableDir(mAppIconPath)) {
                    Drawable drawable = appInfo.getIcon();
                    CommonUtils.drawableToFile(drawable, file.getAbsolutePath(), Bitmap.CompressFormat.PNG);
                    if(mCallback != null){
                        mCallback.onAddSticker(file.getAbsolutePath(),appInfo.getPackageName(),appInfo.getName());
                    }
                }
            }
        });

    }


    private void switchToOneFragment(EditStickerType editTextType) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch (editTextType) {
            case ELEMENT: {
                transaction.hide(mTouchEditFragment);
                transaction.show(propertiesEditFragment);
                transaction.hide(widgetLocalAppIconFragment);
                transaction.commitAllowingStateLoss();
                if (drawableSticker != null){
                    propertiesEditFragment.initActionUI();
                }
                break;
            }
            case TOUCH: {
                transaction.hide(propertiesEditFragment);
                transaction.show(mTouchEditFragment);
                transaction.hide(widgetLocalAppIconFragment);
                transaction.commitAllowingStateLoss();
                if (drawableSticker != null){
                    mTouchEditFragment.initActionUI(drawableSticker.getJumpAppPath(),drawableSticker.getJumpContent(),drawableSticker.getAppName());
                }
                break;
            }
            case APP_ICON: {
                transaction.hide(propertiesEditFragment);
                transaction.hide(mTouchEditFragment);
                transaction.show(widgetLocalAppIconFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
        }

    }

    public void setDrawableSticker(DrawableSticker drawableSticker) {
        this.drawableSticker = drawableSticker;
        if (propertiesEditFragment != null) {
            propertiesEditFragment.setSticker(drawableSticker);
        }
        if (mTouchEditFragment != null) {
            mTouchEditFragment.initActionUI(drawableSticker.getJumpAppPath(),drawableSticker.getJumpContent(),drawableSticker.getAppName());
        }
    }

    public void setWidgetEditStickerCallback(WidgetEditStickerCallback callback) {
        this.mCallback = callback;
    }
}
