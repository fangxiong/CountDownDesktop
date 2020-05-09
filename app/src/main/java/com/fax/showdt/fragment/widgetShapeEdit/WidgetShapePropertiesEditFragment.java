package com.fax.showdt.fragment.widgetShapeEdit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fax.showdt.R;
import com.fax.showdt.dialog.ios.v3.CustomDialog;
import com.fax.showdt.fragment.widgetStickerEdit.WidgetStickerPropertiesEditFragment;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.view.colorPicker.ColorPickerDialog;
import com.fax.showdt.view.colorPicker.ColorPickerDialogListener;
import com.fax.showdt.view.sticker.DrawableSticker;
import com.kyleduo.switchbutton.SwitchButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.List;

public class WidgetShapePropertiesEditFragment extends Fragment implements View.OnClickListener {

    private TextView mTvStrokeColor, mTvSolidColor, mTvGradientColor, mTvGradientColor1, mTvGradientColor2,mTvOrientation;
    private SeekBar mShapeHeightSeekBar, mShapeLineWidthSeekBar, mShapeCornerWidthSeekBar;
    private DrawableSticker drawableSticker;
    private boolean isInitSuccessed = false;
    private SwitchButton switchButton;
    private LinearLayout moreColor,orientation;
    private ImageView ivOperateColor;
    private FrameLayout oneColor, flColor3, flColorOperate;
    private CustomDialog mGradientOrientationDialog;
    private String[] gradientOrientationArray = {"上 -> 下","右上 -> 左下","右 -> 左","右下 -> 左上","下 -> 上","左下 -> 右上","左 -> 右","左上 -> 右下"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_shape_properties_edit_fragment, container, false);
        mTvStrokeColor = view.findViewById(R.id.tv_strokeColor);
        mTvGradientColor = view.findViewById(R.id.tv_gradientColor);
        mTvGradientColor1 = view.findViewById(R.id.tv_gradientColor1);
        mTvGradientColor2 = view.findViewById(R.id.tv_gradientColor2);
        mTvSolidColor = view.findViewById(R.id.tv_solidColor);
        mTvOrientation = view.findViewById(R.id.tv_orientation);
        mShapeHeightSeekBar = view.findViewById(R.id.shape_height_seekbar);
        mShapeLineWidthSeekBar = view.findViewById(R.id.shape_line_seekbar);
        mShapeCornerWidthSeekBar = view.findViewById(R.id.shape_corner_seekbar);
        switchButton = view.findViewById(R.id.switch_btn);
        moreColor = view.findViewById(R.id.ll_more_color);
        orientation = view.findViewById(R.id.ll_orientation);
        oneColor = view.findViewById(R.id.fl_one_color);
        flColor3 = view.findViewById(R.id.fl_color3);
        flColorOperate = view.findViewById(R.id.fl_color_operate);
        ivOperateColor = view.findViewById(R.id.iv_color_operate);
        mTvStrokeColor.setOnClickListener(this);
        mTvSolidColor.setOnClickListener(this);
        switchButton.setOnClickListener(this);
        mTvGradientColor.setOnClickListener(this);
        mTvGradientColor1.setOnClickListener(this);
        mTvGradientColor2.setOnClickListener(this);
        flColor3.setOnClickListener(this);
        flColorOperate.setOnClickListener(this);
        ivOperateColor.setOnClickListener(this);
        mTvOrientation.setOnClickListener(this);
        initSeekBar();
        isInitSuccessed = true;
        initSwitchBtn();
        initActionUI();
        return view;
    }

    private void initSwitchBtn() {
        switchButton.setChecked(drawableSticker.isGradient());
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (drawableSticker != null) {
                    drawableSticker.setGradient(isChecked);
                }
                moreColor.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                orientation.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                oneColor.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mTvStrokeColor) {
            if (drawableSticker != null) {
                showColorPickDialog(drawableSticker.getStrokeColor(), 0);
            }
        } else if (v == mTvSolidColor) {
            if (drawableSticker != null) {
                showColorPickDialog(drawableSticker.getDrawableColor(), 1);
            }
        } else if (v == mTvGradientColor) {
            if (drawableSticker != null) {
                List<Integer> colors = drawableSticker.getGradientColors();
                showColorPickDialog(CommonUtils.toHexEncoding(colors.get(0)), 2);
            }
        } else if (v == mTvGradientColor1) {
            if (drawableSticker != null) {
                List<Integer> colors = drawableSticker.getGradientColors();
                showColorPickDialog(CommonUtils.toHexEncoding(colors.get(1)), 3);
            }
        } else if (v == mTvGradientColor2) {
            if (drawableSticker != null) {
                List<Integer> colors = drawableSticker.getGradientColors();
                if (colors.size() == 2) {
                    colors.add(Color.parseColor("#FFFFFF"));
                    drawableSticker.setGradientColors(colors);
                }
                showColorPickDialog(CommonUtils.toHexEncoding(colors.get(2)), 4);
            }
        } else if (v == ivOperateColor) {
            List<Integer> colors = drawableSticker.getGradientColors();
            if (colors.size() == 3) {
                flColor3.setVisibility(View.GONE);
                colors.remove(2);
                drawableSticker.setGradientColors(colors);
                mTvGradientColor2.setBackgroundResource(R.color.white);
                ivOperateColor.setImageResource(R.drawable.widget_shape_color_add_btn);
            }else {
                flColor3.setVisibility(View.VISIBLE);
                colors.add(Color.parseColor("#FFFFFF"));
                drawableSticker.setGradientColors(colors);
                ivOperateColor.setImageResource(R.drawable.widget_shape_color_delete_btn);
            }

        } else if(v == mTvOrientation){
            showGradientOrientationDialog();
        } else if(v.getId() == R.id.tv_01){
            drawableSticker.setGradientOrientation(DrawableSticker.TOP_BOTTOM);
            mTvOrientation.setText(gradientOrientationArray[0]);
            mGradientOrientationDialog.doDismiss();
        }else if(v.getId() == R.id.tv_02){
            drawableSticker.setGradientOrientation(DrawableSticker.TR_BL);
            mTvOrientation.setText(gradientOrientationArray[1]);
            mGradientOrientationDialog.doDismiss();
        }else if(v.getId() == R.id.tv_03){
            drawableSticker.setGradientOrientation(DrawableSticker.RIGHT_LEFT);
            mTvOrientation.setText(gradientOrientationArray[2]);
            mGradientOrientationDialog.doDismiss();
        }else if(v.getId() == R.id.tv_04){
            drawableSticker.setGradientOrientation(DrawableSticker.BR_TL);
            mTvOrientation.setText(gradientOrientationArray[3]);
            mGradientOrientationDialog.doDismiss();
        }else if(v.getId() == R.id.tv_05){
            drawableSticker.setGradientOrientation(DrawableSticker.BOTTOM_TOP);
            mTvOrientation.setText(gradientOrientationArray[4]);
            mGradientOrientationDialog.doDismiss();
        }else if(v.getId() == R.id.tv_06){
            drawableSticker.setGradientOrientation(DrawableSticker.BL_TR);
            mTvOrientation.setText(gradientOrientationArray[5]);
            mGradientOrientationDialog.doDismiss();
        }else if(v.getId() == R.id.tv_07){
            drawableSticker.setGradientOrientation(DrawableSticker.LEFT_RIGHT);
            mTvOrientation.setText(gradientOrientationArray[6]);
            mGradientOrientationDialog.doDismiss();
        }else if(v.getId() == R.id.tv_08){
            drawableSticker.setGradientOrientation(DrawableSticker.TL_BR);
            mTvOrientation.setText(gradientOrientationArray[7]);
            mGradientOrientationDialog.doDismiss();
        }
    }

    private void initSeekBar() {
        mShapeHeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (drawableSticker != null) {
                    drawableSticker.setShapeHeightRatio(progress / 100f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mShapeLineWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (drawableSticker != null) {
                    drawableSticker.setStrokeRatio(progress / 100f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mShapeCornerWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (drawableSticker != null) {
                    drawableSticker.setCornerRatio(progress / 100f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void showColorPickDialog(String color, final int colorType) {
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
                    if (colorType == 0) {
                        drawableSticker.setStrokeColor(hexCode);
                        mTvStrokeColor.setBackgroundColor(color);
                    } else if (colorType == 1) {
                        drawableSticker.setDrawableColor(hexCode);
                        mTvSolidColor.setBackgroundColor(color);
                    } else if (colorType == 2) {
                        List<Integer> colors = drawableSticker.getGradientColors();
                        colors.set(0, color);
                        drawableSticker.setGradientColors(colors);
                        mTvGradientColor.setBackgroundColor(color);
                    } else if (colorType == 3) {
                        List<Integer> colors = drawableSticker.getGradientColors();
                        colors.set(1, color);
                        drawableSticker.setGradientColors(colors);
                        mTvGradientColor1.setBackgroundColor(color);
                    } else if (colorType == 4) {
                        List<Integer> colors = drawableSticker.getGradientColors();
                        colors.set(2, color);
                        drawableSticker.setGradientColors(colors);
                        mTvGradientColor2.setBackgroundColor(color);
                    }
                }
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getChildFragmentManager(), "color_dialog");
    }

    private void showGradientOrientationDialog() {
        mGradientOrientationDialog = CustomDialog.build(((AppCompatActivity) getActivity()), R.layout.widget_shape_gradient_orientation_dialog, new CustomDialog.OnBindView() {
            @Override
            public void onBind(CustomDialog dialog, View v) {
                TextView tv01 = v.findViewById(R.id.tv_01);
                TextView tv02 = v.findViewById(R.id.tv_02);
                TextView tv03 = v.findViewById(R.id.tv_03);
                TextView tv04 = v.findViewById(R.id.tv_04);
                TextView tv05 = v.findViewById(R.id.tv_05);
                TextView tv06 = v.findViewById(R.id.tv_06);
                TextView tv07 = v.findViewById(R.id.tv_07);
                TextView tv08 = v.findViewById(R.id.tv_08);
                tv01.setOnClickListener(WidgetShapePropertiesEditFragment.this);
                tv02.setOnClickListener(WidgetShapePropertiesEditFragment.this);
                tv03.setOnClickListener(WidgetShapePropertiesEditFragment.this);
                tv04.setOnClickListener(WidgetShapePropertiesEditFragment.this);
                tv05.setOnClickListener(WidgetShapePropertiesEditFragment.this);
                tv06.setOnClickListener(WidgetShapePropertiesEditFragment.this);
                tv07.setOnClickListener(WidgetShapePropertiesEditFragment.this);
                tv08.setOnClickListener(WidgetShapePropertiesEditFragment.this);
            }
        });
        mGradientOrientationDialog.setAlign(CustomDialog.ALIGN.DEFAULT).setCancelable(true).show();
    }

    public void setDrawableSticker(DrawableSticker drawableSticker) {
        this.drawableSticker = drawableSticker;
        initActionUI();
    }

    public void initActionUI() {
        if (drawableSticker != null && isInitSuccessed) {
            mTvStrokeColor.setBackgroundColor(Color.parseColor(drawableSticker.getStrokeColor()));
            mTvSolidColor.setBackgroundColor(Color.parseColor(drawableSticker.getDrawableColor()));
            List<Integer> colors = drawableSticker.getGradientColors();
            mTvGradientColor.setBackgroundColor(colors.get(0));
            mTvGradientColor1.setBackgroundColor(colors.get(1));
            if (colors.size() == 3) {
                mTvGradientColor2.setBackgroundColor(colors.get(2));
                ivOperateColor.setImageResource(R.drawable.widget_shape_color_delete_btn);
            }else {
                flColor3.setVisibility(View.GONE);
                ivOperateColor.setImageResource(R.drawable.widget_shape_color_add_btn);
            }
            switchButton.setChecked(drawableSticker.isGradient());
            boolean isGradient = drawableSticker.isGradient();
            moreColor.setVisibility(isGradient ? View.VISIBLE : View.GONE);
            orientation.setVisibility(isGradient ? View.VISIBLE : View.GONE);
            oneColor.setVisibility(isGradient ? View.GONE : View.VISIBLE);
            mTvOrientation.setText(gradientOrientationArray[drawableSticker.getGradientOrientation()]);
            mShapeHeightSeekBar.setProgress((int) (drawableSticker.getShapeHeightRatio() * 100));
            mShapeLineWidthSeekBar.setProgress((int) (drawableSticker.getStrokeRatio() * 100));
            mShapeCornerWidthSeekBar.setProgress((int) (drawableSticker.getCornerRatio() * 100));

        }
    }

}
