package com.fax.showdt.fragment.widgetVectorEdit;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fax.showdt.R;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.ToastShowUtils;
import com.fax.showdt.view.bubbleseekbar.BubbleSeekBar;
import com.fax.showdt.view.colorPicker.ColorPickerDialog;
import com.fax.showdt.view.colorPicker.ColorPickerDialogListener;
import com.fax.showdt.view.sticker.DrawableSticker;
import com.kyleduo.switchbutton.SwitchButton;

import es.dmoral.toasty.Toasty;


/**
 * Description:     矢量图标的属性编辑页面
 * Author:          fax
 * CreateDate:      2020-05-11 15:12
 * Email:           fxiong1995@gmail.com
 */
public class WidgetVectorPropertiesEditFragment extends Fragment implements View.OnClickListener {

    private BubbleSeekBar mStrokeWidthSeekBar;
    private LinearLayout llStrokeBody;
    private TextView mTvStrokeWidth, mTvStrokeColor;
    private DrawableSticker drawableSticker;
    private boolean isInitSuccessed = false;
    private SwitchButton switchButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_vector_properties_edit_fragment, container, false);
        mStrokeWidthSeekBar = view.findViewById(R.id.seekbar_stroke_width);
        switchButton = view.findViewById(R.id.switch_btn);
        llStrokeBody = view.findViewById(R.id.ll_stroke_body);
        mTvStrokeWidth = view.findViewById(R.id.tv_progress);
        mTvStrokeColor = view.findViewById(R.id.tv_strokeColor);
        mTvStrokeColor.setOnClickListener(this);
        initSeekBar();
        isInitSuccessed = true;
        initSwitchBtn();
        initActionUI();
        return view;

    }

    @Override
    public void onClick(View v) {
        if (v == mTvStrokeColor) {
            if (drawableSticker != null) {
                showColorPickDialog(drawableSticker);
            }else {
                ToastShowUtils.showCommonToast(getContext(),"请先选择矢量图", Toasty.LENGTH_SHORT);
            }
        }
    }

    private void showColorPickDialog(final DrawableSticker drawableSticker) {
        ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(true)
                .setDialogId(0)
                .setColor(Color.parseColor(drawableSticker.getStrokeColor()))
                .setShowAlphaSlider(true)
                .setShowAlphaSlider(true)
                .create();
        dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
            @Override
            public void onColorSelected(int dialogId, int color) {
                String hexCode = "";
                hexCode = CommonUtils.toHexEncoding(color);
                drawableSticker.setDrawableColor(hexCode);
                mTvStrokeColor.setBackground(new ColorDrawable(color));

            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getChildFragmentManager(), "color_dialog");
    }


    private void initSwitchBtn() {
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (drawableSticker != null) {
                    drawableSticker.setStroke(isChecked);
                }
                llStrokeBody.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void initSeekBar() {

        mStrokeWidthSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if (drawableSticker != null) {
                    drawableSticker.setStrokeWidth(progress);
                }
                mTvStrokeWidth.setText(String.valueOf(progress));
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
    }

    public void setDrawableSticker(DrawableSticker drawableSticker) {
        this.drawableSticker = drawableSticker;
        initActionUI();
    }


    public void initActionUI() {
        if (drawableSticker != null && isInitSuccessed) {
            switchButton.setChecked(drawableSticker.isStroke());
            llStrokeBody.setVisibility(drawableSticker.isStroke() ? View.VISIBLE : View.GONE);
            mStrokeWidthSeekBar.setProgress(drawableSticker.getStrokeWidth());
            mTvStrokeColor.setBackgroundColor(Color.parseColor(drawableSticker.getDrawableColor()));
        }
    }


}
