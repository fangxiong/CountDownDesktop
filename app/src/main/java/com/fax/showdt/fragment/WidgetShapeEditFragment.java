package com.fax.showdt.fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.showdt.R;
import com.fax.showdt.bean.WidgetShapeBean;
import com.fax.showdt.callback.WidgetEditShapeCallback;
import com.fax.showdt.callback.WidgetEditShapeElementSelectedCallback;
import com.fax.showdt.fragment.widgetShapeEdit.WidgetShapeElementEditFragment;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.view.colorPicker.ColorPickerDialog;
import com.fax.showdt.view.colorPicker.ColorPickerDialogListener;
import com.fax.showdt.view.sticker.DrawableSticker;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class WidgetShapeEditFragment extends Fragment implements View.OnClickListener {
    private ImageView mIvLocal, mIvColor,mConsume;
    private WidgetShapeElementEditFragment mStickerElementEditFragment;
    private WidgetEditShapeCallback mWidgetEditShapeCallback;
    private DrawableSticker mDrawableSticker;


    public WidgetShapeEditFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_shape_edit_fragment, container, false);
        mIvLocal = view.findViewById(R.id.iv_local);
        mIvColor = view.findViewById(R.id.iv_color);
        mConsume = view.findViewById(R.id.iv_consume);
        mIvLocal.setOnClickListener(this);
        mIvColor.setOnClickListener(this);
        mConsume.setOnClickListener(this);
        mIvLocal.setSelected(true);
        initFragment();
        return view;
    }

    @Override
    public void onClick(View view) {
        int resId = view.getId();
        if (resId == R.id.iv_consume) {
            if (mWidgetEditShapeCallback != null) {
                mWidgetEditShapeCallback.closePanel();
            }
        }else if(resId == R.id.iv_color){
            if(mDrawableSticker!= null) {
                showColorPickDialog(mDrawableSticker.getSvgColor());
            }
        }
    }

    public void setWidgetEditShapeSticker(DrawableSticker drawableSticker) {
        mDrawableSticker = drawableSticker;
    }
    private void showColorPickDialog(String color){
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
                    mDrawableSticker.setSvgColor(hexCode);
                    try {
                        SVG svg = new SVGBuilder().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN))
                                .readFromAsset(getActivity().getAssets(), mDrawableSticker.getDrawablePath()).build();
                        PictureDrawable drawable = svg.getDrawable();
                        mDrawableSticker.setDrawable(drawable);
                    }catch (IOException e){

                    }
                }
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getChildFragmentManager(),"color_dialog");
    }

    private void initFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        mStickerElementEditFragment = new WidgetShapeElementEditFragment();
        transaction.add(R.id.fl_shape_edit_body, mStickerElementEditFragment);
        transaction.commitAllowingStateLoss();
        mStickerElementEditFragment.setWidgetShapeElementSelectedCallback(new WidgetEditShapeElementSelectedCallback() {
            @Override
            public void selectShapeElement(WidgetShapeBean widgetShapeBean) {
                mWidgetEditShapeCallback.onAddShapeSticker(widgetShapeBean);
            }
        });
    }
    public void setWidgetEditShapeCallback(WidgetEditShapeCallback callback) {
        this.mWidgetEditShapeCallback = callback;
    }

}
