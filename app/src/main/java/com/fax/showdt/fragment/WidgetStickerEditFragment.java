package com.fax.showdt.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.showdt.R;
import com.fax.showdt.callback.WidgetEditStickerCallback;
import com.fax.showdt.callback.WidgetEditStickerElementSelectedCallback;
import com.fax.showdt.fragment.widgetShapeEdit.WidgetShapeElementEditFragment;
import com.fax.showdt.fragment.widgetStickerEdit.WidgetStickerPropertiesEditFragment;
import com.fax.showdt.view.sticker.DrawableSticker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class WidgetStickerEditFragment extends Fragment implements View.OnClickListener {
    private ImageView mIvLocal, mAdd, mTouch, mConsume;
    private WidgetEditStickerCallback mCallback;
    private WidgetStickerPropertiesEditFragment propertiesEditFragment;
    private WidgetClickSettingFragment mTouchEditFragment;
    private List<View> mViews = new ArrayList<>();
    private DrawableSticker drawableSticker;

    enum EditStickerType {
        ELEMENT, TOUCH
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
        mIvLocal.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        mIvLocal.setOnClickListener(this);
        mConsume.setOnClickListener(this);
        mTouch.setOnClickListener(this);
        mViews.add(mIvLocal);
        mViews.add(mTouch);
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
        propertiesEditFragment.setSticker(drawableSticker);
        transaction.add(R.id.fl_sticker_edit_body, propertiesEditFragment);
        transaction.add(R.id.fl_sticker_edit_body, mTouchEditFragment);
        transaction.commitAllowingStateLoss();

    }

    private void switchToOneFragment(EditStickerType editTextType) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch (editTextType) {
            case ELEMENT: {
                transaction.hide(mTouchEditFragment);
                transaction.show(propertiesEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
            case TOUCH: {
                transaction.hide(propertiesEditFragment);
                transaction.show(mTouchEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
        }

    }

    public void setDrawableSticker(DrawableSticker drawableSticker) {
        this.drawableSticker = drawableSticker;
        if(propertiesEditFragment != null){
            propertiesEditFragment.setSticker(drawableSticker);
        }
    }

    public void setWidgetEditStickerCallback(WidgetEditStickerCallback callback) {
        this.mCallback = callback;
    }
}
