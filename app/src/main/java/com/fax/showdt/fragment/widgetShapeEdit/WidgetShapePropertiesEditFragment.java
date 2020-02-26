package com.fax.showdt.fragment.widgetShapeEdit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.fax.showdt.R;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.view.colorPicker.ColorPickerDialog;
import com.fax.showdt.view.colorPicker.ColorPickerDialogListener;
import com.fax.showdt.view.sticker.DrawableSticker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WidgetShapePropertiesEditFragment extends Fragment implements View.OnClickListener {

    private TextView  mTvStrokeColor,mTvSolidColor;
    private SeekBar mShapeHeightSeekBar,mShapeLineWidthSeekBar,mShapeCornerWidthSeekBar;
    private DrawableSticker drawableSticker;
    private boolean isInitSuccessed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_shape_properties_edit_fragment, container, false);
        mTvStrokeColor = view.findViewById(R.id.tv_strokeColor);
        mTvSolidColor = view.findViewById(R.id.tv_solidColor);
        mShapeHeightSeekBar = view.findViewById(R.id.shape_height_seekbar);
        mShapeLineWidthSeekBar = view.findViewById(R.id.shape_line_seekbar);
        mShapeCornerWidthSeekBar = view.findViewById(R.id.shape_corner_seekbar);
        mTvStrokeColor.setOnClickListener(this);
        mTvSolidColor.setOnClickListener(this);
        initSeekBar();
        isInitSuccessed = true;
        initActionUI();
        return view;
    }


    @Override
    public void onClick(View v) {
    if (v == mTvStrokeColor) {
            if (drawableSticker != null) {
                showColorPickDialog(drawableSticker.getStrokeColor(),0);
            }
    } else if(v == mTvSolidColor){
        if (drawableSticker != null) {
            showColorPickDialog(drawableSticker.getDrawableColor(),1);
        }
    }
    }

    private void initSeekBar() {
        mShapeHeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (drawableSticker != null) {
                    drawableSticker.setShapeHeightRatio(progress/100f);
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
                        drawableSticker.setStrokeRatio(progress/100f);
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
                    drawableSticker.setCornerRatio(progress/100f);
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
                if(drawableSticker != null){
                    if(colorType == 0) {
                        drawableSticker.setStrokeColor(hexCode);
                        mTvStrokeColor.setBackgroundColor(color);
                    }else {
                        drawableSticker.setDrawableColor(hexCode);
                        mTvSolidColor.setBackgroundColor(color);
                    }
                }
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getChildFragmentManager(), "color_dialog");
    }

    public void setDrawableSticker(DrawableSticker drawableSticker) {
        this.drawableSticker = drawableSticker;
        initActionUI();
    }

    public void initActionUI() {
        if (drawableSticker != null && isInitSuccessed) {
            mTvStrokeColor.setBackgroundColor(Color.parseColor(drawableSticker.getStrokeColor()));
            mTvSolidColor.setBackgroundColor(Color.parseColor(drawableSticker.getDrawableColor()));
            mShapeHeightSeekBar.setProgress((int)(drawableSticker.getShapeHeightRatio()*100));
            mShapeLineWidthSeekBar.setProgress((int)(drawableSticker.getStrokeRatio()*100));
            mShapeCornerWidthSeekBar.setProgress((int)(drawableSticker.getCornerRatio()*100));
        }
    }

}
