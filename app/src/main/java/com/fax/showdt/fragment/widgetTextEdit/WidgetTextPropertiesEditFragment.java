package com.fax.showdt.fragment.widgetTextEdit;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.manager.widget.WidgetProgressStyle;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.ViewUtils;
import com.fax.showdt.view.colorPicker.ColorPickerDialog;
import com.fax.showdt.view.colorPicker.ColorPickerDialogListener;
import com.fax.showdt.view.sticker.TextSticker;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-10
 * Description:
 */
public class WidgetTextPropertiesEditFragment extends Fragment implements View.OnClickListener {
    private TextView mTvColor;
    private TextSticker mTextSticker;
    private ImageView mIvAlignment;
    private SeekBar mLetterSeekBar, mLineSeekBar;
    private boolean isInitSucceed = false;

    public WidgetTextPropertiesEditFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_text_properties_edit_fragment, container, false);
        mTvColor = view.findViewById(R.id.tv_color);
        mIvAlignment = view.findViewById(R.id.iv_alignment);
        mLetterSeekBar = view.findViewById(R.id.letter_seekbar);
        mLineSeekBar = view.findViewById(R.id.line_seekbar);
        mTvColor.setOnClickListener(this);
        mIvAlignment.setOnClickListener(this);
        isInitSucceed = true;
        initSeekBarView();
        return view;
    }

    @Override
    public void onClick(View v) {
        if (mTextSticker == null) {
            return;
        }
        if (v == mTvColor) {
            showColorPickDialog(mTextSticker.getTextColor());
        } else if (v == mIvAlignment) {
            clickAlignmentBtn(mTextSticker.getAlignment());
        }

    }

    private void initSeekBarView() {
        mLetterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float result = (progress - 2) * 1.0f / 10;
                if(mTextSticker != null){
                    mTextSticker.setLetterSpacing(result);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mLineSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float result = (progress+5) * 1.0f / 10;
                if(mTextSticker != null){
                    mTextSticker.setLineSpacingMultiplier(result);
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

    public void setSticker(TextSticker textSticker) {
        this.mTextSticker = textSticker;
        initActionUI();
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
                if (mTextSticker != null) {
                    mTextSticker.setTextColor(hexCode);
                    mTvColor.setBackgroundColor(color);
                }
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getChildFragmentManager(), "color_dialog");
    }

    public void initActionUI() {
        if (mTextSticker != null && isInitSucceed) {
            mTvColor.setBackgroundColor(Color.parseColor(mTextSticker.getTextColor()));
            initAlignmentUI(mTextSticker.getAlignment());
            initSeekBar();
        }
    }

    private void clickAlignmentBtn(Layout.Alignment alignment) {
        switch (alignment) {
            case ALIGN_NORMAL: {
                initAlignmentUI(Layout.Alignment.ALIGN_CENTER);
                if (mTextSticker != null) {
                    mTextSticker.setAlignment(Layout.Alignment.ALIGN_CENTER);
                }
                break;
            }
            case ALIGN_CENTER: {
                initAlignmentUI(Layout.Alignment.ALIGN_OPPOSITE);
                if (mTextSticker != null) {
                    mTextSticker.setAlignment(Layout.Alignment.ALIGN_OPPOSITE);
                }
                break;
            }
            case ALIGN_OPPOSITE: {
                initAlignmentUI(Layout.Alignment.ALIGN_NORMAL);
                if (mTextSticker != null) {
                    mTextSticker.setAlignment(Layout.Alignment.ALIGN_NORMAL);
                }
                break;
            }
        }
    }

    private void initAlignmentUI(Layout.Alignment alignment) {
        switch (alignment) {
            case ALIGN_NORMAL: {
                mIvAlignment.setImageResource(R.drawable.widget_text_left_alignment);
                break;
            }
            case ALIGN_CENTER: {
                mIvAlignment.setImageResource(R.drawable.widget_text_center_alignment);
                break;
            }
            case ALIGN_OPPOSITE: {
                mIvAlignment.setImageResource(R.drawable.widget_text_right_alignment);
                break;
            }
        }
    }

    private void initSeekBar(){
        if(mTextSticker != null){
            mLetterSeekBar.setProgress((int)(mTextSticker.getLetterSpacing()*10+2));
            mLineSeekBar.setProgress((int)(mTextSticker.getLineSpacingMultiplier()*10-5));
        }
    }

}
