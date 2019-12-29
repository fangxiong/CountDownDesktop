package com.fax.showdt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.showdt.R;
import com.fax.showdt.callback.WidgetEditStickerCallback;
import com.fax.showdt.callback.WidgetEditStickerElementSelectedCallback;
import com.fax.showdt.fragment.widgetShapeEdit.WidgetShapeElementEditFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class WidgetStickerEditFragment extends Fragment implements View.OnClickListener {
    private ImageView mIvLocal, mAdd, mConsume;
    private WidgetEditStickerCallback mCallback;
    private WidgetShapeElementEditFragment mStickerElementEditFragment;
    private WidgetClickSettingFragment mTouchEditFragment;

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
        mIvLocal.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        mIvLocal.setOnClickListener(this);
        mConsume.setOnClickListener(this);
        initFragment();
        return view;
    }

    @Override
    public void onClick(View view) {
        int resId = view.getId();
        if (resId == R.id.iv_consume) {
            if (mCallback != null) {
                mCallback.closePanel();
            }
        } else if (resId == R.id.iv_add) {
            if (mCallback != null) {
                mCallback.onPickPhoto();
            }
        }
    }

    private void initFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        mStickerElementEditFragment = new WidgetShapeElementEditFragment();
        mTouchEditFragment = new WidgetClickSettingFragment();
        transaction.add(R.id.fl_sticker_edit_body, mStickerElementEditFragment);
        transaction.add(R.id.fl_sticker_edit_body, mTouchEditFragment);
        transaction.commitAllowingStateLoss();

    }

    private void switchToOneFragment(WidgetTextEditFragment.EditTextType editTextType) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch (editTextType) {
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
                break;
            }
        }

        transaction.commitAllowingStateLoss();
    }


    public void setWidgetEditStickerCallback(WidgetEditStickerCallback callback) {
        this.mCallback = callback;
    }
}
