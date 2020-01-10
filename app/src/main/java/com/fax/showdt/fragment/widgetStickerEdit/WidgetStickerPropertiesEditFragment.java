package com.fax.showdt.fragment.widgetStickerEdit;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.fax.showdt.R;
import com.fax.showdt.dialog.ios.v3.CustomDialog;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.ToastShowUtils;
import com.fax.showdt.view.colorPicker.ColorPickerDialog;
import com.fax.showdt.view.colorPicker.ColorPickerDialogListener;
import com.fax.showdt.view.sticker.DrawableSticker;
import com.kyleduo.switchbutton.SwitchButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import es.dmoral.toasty.Toasty;

public class WidgetStickerPropertiesEditFragment extends Fragment implements View.OnClickListener {
    private TextView mTvClipShape, mTvStrokeColor;
    private SwitchButton switchButton;
    private CustomDialog mClipShapeDialog;
    private DrawableSticker drawableSticker;
    private boolean isInitSuccessed = false;

    public WidgetStickerPropertiesEditFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_sticker_properties_edit_fragment, container, false);
        mTvClipShape = view.findViewById(R.id.tv_clip_shape);
        mTvStrokeColor = view.findViewById(R.id.tv_stroke_color);
        switchButton = view.findViewById(R.id.switch_btn);
        mTvClipShape.setOnClickListener(this);
        mTvStrokeColor.setOnClickListener(this);
        switchButton.setOnClickListener(this);
        initSwitchBtn();
        isInitSuccessed = true;
        initActionUI();
        return view;
    }

    @Override
    public void onClick(View v) {
        if (drawableSticker == null) {
            ToastShowUtils.showCommonToast(getActivity(), "请先从相册选择图片", Toasty.LENGTH_SHORT);
            return;
        }
        if (v == mTvClipShape) {
            showClipShapeDialog();
        } else if (v == mTvStrokeColor) {
            showColorPickDialog(drawableSticker.getStrokeColor());
        } else if (v.getId() == R.id.tv_clip_circle) {
            drawableSticker.addMaskBitmap(getActivity(), DrawableSticker.CIRCLE);
            mClipShapeDialog.doDismiss();
        } else if (v.getId() == R.id.tv_clip_round) {
            drawableSticker.addMaskBitmap(getActivity(), DrawableSticker.ROUND);
            mClipShapeDialog.doDismiss();
        } else if (v.getId() == R.id.tv_clip_rect) {
            drawableSticker.addMaskBitmap(getActivity(), DrawableSticker.RECT);
            mClipShapeDialog.doDismiss();
        } else if (v.getId() == R.id.tv_clip_love) {
            drawableSticker.addMaskBitmap(getActivity(), DrawableSticker.LOVE);
            mClipShapeDialog.doDismiss();
        } else if (v.getId() == R.id.tv_clip_pentagon) {
            drawableSticker.addMaskBitmap(getActivity(), DrawableSticker.PENTAGON);
            mClipShapeDialog.doDismiss();
        }
    }

    private void initSwitchBtn() {
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (drawableSticker != null) {
                    drawableSticker.setShowFrame(isChecked);
                    drawableSticker.addMaskBitmap(getActivity(),drawableSticker.getClipType());
                }
            }
        });
    }

    public void setSticker(DrawableSticker drawableSticker) {
        this.drawableSticker = drawableSticker;
        initActionUI();
    }

    private void showClipShapeDialog() {
        mClipShapeDialog = CustomDialog.build(((AppCompatActivity) getActivity()), R.layout.widget_clip_shape_dialog, new CustomDialog.OnBindView() {
            @Override
            public void onBind(CustomDialog dialog, View v) {
                TextView tvClipCircle = v.findViewById(R.id.tv_clip_circle);
                TextView tvClipRound = v.findViewById(R.id.tv_clip_round);
                TextView tvClipRect = v.findViewById(R.id.tv_clip_rect);
                TextView tvClipLove = v.findViewById(R.id.tv_clip_love);
                TextView tvClipPentagon = v.findViewById(R.id.tv_clip_pentagon);
                tvClipCircle.setOnClickListener(WidgetStickerPropertiesEditFragment.this);
                tvClipRound.setOnClickListener(WidgetStickerPropertiesEditFragment.this);
                tvClipRect.setOnClickListener(WidgetStickerPropertiesEditFragment.this);
                tvClipLove.setOnClickListener(WidgetStickerPropertiesEditFragment.this);
                tvClipPentagon.setOnClickListener(WidgetStickerPropertiesEditFragment.this);
            }
        });
        mClipShapeDialog.setAlign(CustomDialog.ALIGN.DEFAULT).setCancelable(false).show();
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
                if (drawableSticker != null) {
                    drawableSticker.setStrokeColor(hexCode);
                    drawableSticker.addMaskBitmap(getActivity(),drawableSticker.getClipType());
                }
                mTvStrokeColor.setBackground(new ColorDrawable(color));
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getChildFragmentManager(), "color_dialog");
    }

    public void initActionUI() {
        if (drawableSticker != null && isInitSuccessed) {
            switch (drawableSticker.getClipType()) {
                case DrawableSticker.CIRCLE: {
                    mTvClipShape.setText(getString(R.string.widget_clip_circle));
                    break;
                }
                case DrawableSticker.ROUND: {
                    mTvClipShape.setText(getString(R.string.widget_clip_round));
                    break;
                }
                case DrawableSticker.RECT: {
                    mTvClipShape.setText(getString(R.string.widget_clip_rect));
                    break;
                }
                case DrawableSticker.LOVE: {
                    mTvClipShape.setText(getString(R.string.widget_clip_love));
                    break;
                }
                case DrawableSticker.PENTAGON: {
                    mTvClipShape.setText(getString(R.string.widget_clip_pentagon));
                    break;
                }

            }
            switchButton.setChecked(drawableSticker.isShowFrame());
            mTvStrokeColor.setBackgroundColor(Color.parseColor(drawableSticker.getStrokeColor()));
        }

    }

}
