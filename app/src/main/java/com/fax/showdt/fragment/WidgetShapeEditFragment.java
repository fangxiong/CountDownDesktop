package com.fax.showdt.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.showdt.R;
import com.fax.showdt.callback.WidgetEditClickCallback;
import com.fax.showdt.callback.WidgetEditShapeCallback;
import com.fax.showdt.fragment.widgetShapeEdit.WidgetShapePropertiesEditFragment;
import com.fax.showdt.view.sticker.DrawableSticker;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class WidgetShapeEditFragment extends Fragment implements View.OnClickListener {
    private ImageView mIvLocal, mAdd, mTouch, mConsume;
    private WidgetShapePropertiesEditFragment propertiesEditFragment;
    private WidgetClickSettingFragment mTouchEditFragment;
    private List<View> mViews = new ArrayList<>();
    private DrawableSticker drawableSticker;
    private WidgetEditShapeCallback mCallback;

    enum EditShapeType {
        ELEMENT, TOUCH
    }

    public WidgetShapeEditFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_shape_edit_fragment, container, false);
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
        switchToOneFragment(EditShapeType.ELEMENT);
    }

    @Override
    public void onClick(View view) {
        int resId = view.getId();
        if (resId == R.id.iv_consume) {
            if (mCallback != null) {
                mCallback.closePanel();
            }
        } else if (resId == R.id.iv_local) {
            switchToOneFragment(EditShapeType.ELEMENT);
            refreshSelectedViewStatus(mIvLocal);
        } else if (resId == R.id.iv_touch) {
            switchToOneFragment(EditShapeType.TOUCH);
            refreshSelectedViewStatus(mTouch);

        } else if (resId == R.id.iv_add) {
            if(mCallback != null){
                mCallback.onAddShapeSticker();
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
        propertiesEditFragment = new WidgetShapePropertiesEditFragment();
        mTouchEditFragment = new WidgetClickSettingFragment();
        propertiesEditFragment.setDrawableSticker(drawableSticker);
        transaction.add(R.id.fl_sticker_edit_body, propertiesEditFragment);
        transaction.add(R.id.fl_sticker_edit_body, mTouchEditFragment);
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
    }

    private void switchToOneFragment(EditShapeType editTextType) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch (editTextType) {
            case ELEMENT: {
                transaction.hide(mTouchEditFragment);
                transaction.show(propertiesEditFragment);
                transaction.commitAllowingStateLoss();
                if (drawableSticker != null){
                    propertiesEditFragment.initActionUI();
                }
                break;
            }
            case TOUCH: {
                transaction.hide(propertiesEditFragment);
                transaction.show(mTouchEditFragment);
                transaction.commitAllowingStateLoss();
                if (drawableSticker != null){
                    mTouchEditFragment.initActionUI(drawableSticker.getJumpAppPath(),drawableSticker.getJumpContent(),drawableSticker.getAppName());
                }
                break;
            }
        }

    }
    public void setDrawableSticker(DrawableSticker drawableSticker) {
        this.drawableSticker = drawableSticker;
        if (propertiesEditFragment != null) {
            propertiesEditFragment.setDrawableSticker(drawableSticker);
        }
        if (mTouchEditFragment != null) {
            mTouchEditFragment.initActionUI(drawableSticker.getJumpAppPath(),drawableSticker.getJumpContent(),drawableSticker.getAppName());
        }
    }

    public void setWidgetEditShapeCallback(WidgetEditShapeCallback widgetEditShapeCallback){
        this.mCallback = widgetEditShapeCallback;
    }

}
