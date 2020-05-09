package com.fax.showdt.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.showdt.R;
import com.fax.showdt.bean.SvgIconBean;
import com.fax.showdt.bean.WidgetShapeBean;
import com.fax.showdt.callback.WidgetEditClickCallback;
import com.fax.showdt.callback.WidgetEditVectorCallback;
import com.fax.showdt.callback.WidgetEditVectorElementSelectedCallback;
import com.fax.showdt.fragment.widgetVectorEdit.WidgetVectorElementEditFragment;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.view.colorPicker.ColorPickerDialog;
import com.fax.showdt.view.colorPicker.ColorPickerDialogListener;
import com.fax.showdt.view.sticker.DrawableSticker;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class WidgetVectorEditFragment extends Fragment implements View.OnClickListener {
    private ImageView mIvLocal, mTouch, mIvColor, mConsume;
    private WidgetVectorElementEditFragment mStickerElementEditFragment;
    private WidgetClickSettingFragment mTouchEditFragment;
    private WidgetEditVectorCallback mWidgetEditShapeCallback;
    private DrawableSticker mDrawableSticker;
    private List<View> mViews = new ArrayList<>();

    enum EditShapeType {
        ELEMENT, TOUCH
    }

    public WidgetVectorEditFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_vector_edit_fragment, container, false);
        mIvLocal = view.findViewById(R.id.iv_local);
        mIvColor = view.findViewById(R.id.iv_color);
        mConsume = view.findViewById(R.id.iv_consume);
        mTouch = view.findViewById(R.id.iv_touch);
        mIvLocal.setOnClickListener(this);
        mIvColor.setOnClickListener(this);
        mConsume.setOnClickListener(this);
        mTouch.setOnClickListener(this);
        mIvLocal.setSelected(true);
        mViews.add(mIvLocal);
        mViews.add(mTouch);
        refreshSelectedViewStatus(mIvLocal);
        initFragment();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchToOneFragment(EditShapeType.ELEMENT);
    }

    @Override
    public void onClick(View view) {
        int resId = view.getId();
        if (resId == R.id.iv_consume) {
            if (mWidgetEditShapeCallback != null) {
                mWidgetEditShapeCallback.closePanel();
            }
        } else if (resId == R.id.iv_color) {
            if (mDrawableSticker != null) {
                showColorPickDialog(mDrawableSticker.getDrawableColor());
            }
        } else if (resId == R.id.iv_local) {
            switchToOneFragment(EditShapeType.ELEMENT);
            refreshSelectedViewStatus(mIvLocal);
        } else if (resId == R.id.iv_touch) {
            switchToOneFragment(EditShapeType.TOUCH);
            refreshSelectedViewStatus(mTouch);
        }
    }

    public void setWidgetEditShapeSticker(DrawableSticker drawableSticker) {
        mDrawableSticker = drawableSticker;
        if (mTouchEditFragment != null) {
            mTouchEditFragment.initActionUI(drawableSticker.getJumpAppPath(),mDrawableSticker.getJumpContent(),mDrawableSticker.getAppName());
        }
    }

    private void showColorPickDialog(String color) {
        ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(true)
                .setDialogId(0)
                .setColor(Color.parseColor(color))
                .setShowAlphaSlider(true)
                .setShowAlphaSlider(true)
                .create();
        dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
            @Override
            public void onColorSelected(int dialogId, int color) {
                String hexCode = "";
                hexCode = CommonUtils.toHexEncoding(color);
                if (mDrawableSticker != null) {
                    mDrawableSticker.setDrawableColor(hexCode);
                    try {
                        SVG svg = new SVGBuilder().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN))
                                .readFromAsset(getActivity().getAssets(), mDrawableSticker.getDrawablePath()).build();
                        PictureDrawable drawable = svg.getDrawable();
                        mDrawableSticker.setDrawable(drawable);
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getChildFragmentManager(), "color_dialog");
    }

    private void initFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        mStickerElementEditFragment = new WidgetVectorElementEditFragment();
        mTouchEditFragment = new WidgetClickSettingFragment();
        transaction.add(R.id.fl_shape_edit_body, mTouchEditFragment);
        transaction.add(R.id.fl_shape_edit_body, mStickerElementEditFragment);
        transaction.commitAllowingStateLoss();
        mStickerElementEditFragment.setWidgetShapeElementSelectedCallback(new WidgetEditVectorElementSelectedCallback() {
            @Override
            public void selectVectorElement(SvgIconBean widgetShapeBean) {
                mWidgetEditShapeCallback.onAddVectorSticker(widgetShapeBean);
            }
        });
        mTouchEditFragment.setEditClickCallback(new WidgetEditClickCallback() {
            @Override
            public void onActionType(String actionType) {
                if (mDrawableSticker != null) {
                    mDrawableSticker.setJumpAppPath(actionType);
                }
            }

            @Override
            public void onActionContent(String actionContent,String appName) {
                if (mDrawableSticker != null) {
                    mDrawableSticker.setJumpContent(actionContent);
                    mDrawableSticker.setAppName(appName);
                }
            }
        });
    }

    private void refreshSelectedViewStatus(View view) {

        for (View views : mViews) {
            views.setSelected(false);
            Log.i("test_select:", "unselected");
        }
        view.setSelected(true);
    }

    private void switchToOneFragment(EditShapeType editShapeType) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch (editShapeType) {
            case ELEMENT: {
                transaction.hide(mTouchEditFragment);
                transaction.show(mStickerElementEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
            case TOUCH: {
                transaction.hide(mStickerElementEditFragment);
                transaction.show(mTouchEditFragment);
                transaction.commitAllowingStateLoss();
                if(mDrawableSticker != null){
                    mTouchEditFragment.initActionUI(mDrawableSticker.getJumpAppPath(),mDrawableSticker.getJumpContent(),mDrawableSticker.getAppName());
                }
                break;
            }
        }
    }

    public void setWidgetEditShapeCallback(WidgetEditVectorCallback callback) {
        this.mWidgetEditShapeCallback = callback;
    }

}
