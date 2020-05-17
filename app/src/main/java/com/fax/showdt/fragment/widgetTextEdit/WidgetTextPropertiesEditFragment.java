package com.fax.showdt.fragment.widgetTextEdit;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.fax.showdt.view.bubbleseekbar.BubbleSeekBar;
import com.fax.showdt.view.colorPicker.ColorPickerDialog;
import com.fax.showdt.view.colorPicker.ColorPickerDialogListener;
import com.fax.showdt.view.sticker.TextSticker;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-10
 * Description:
 */
public class WidgetTextPropertiesEditFragment extends Fragment implements View.OnClickListener {
    private TextView mTvColor,mTvLetter,mTvRow,mTvShadowRadius,mTvShadowX,mTvShadowY,mTvShadowColor;
    private TextSticker mTextSticker;
    private ImageView mIvAlignment;
    private BubbleSeekBar mLetterSeekBar, mLineSeekBar,mRadiusSeekBar,mOffestXSeekBar,mOffestYSeekBar;
    private LinearLayout llShadow;
    private SwitchButton switchButton;
    private boolean isInitSucceed = false;

    public WidgetTextPropertiesEditFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_text_properties_edit_fragment, container, false);
        mTvColor = view.findViewById(R.id.tv_color);
        mTvLetter = view.findViewById(R.id.tv_letter);
        mTvRow = view.findViewById(R.id.tv_row);
        mIvAlignment = view.findViewById(R.id.iv_alignment);
        mLetterSeekBar = view.findViewById(R.id.letter_seekbar);
        mLineSeekBar = view.findViewById(R.id.row_seekbar);
        llShadow = view.findViewById(R.id.ll_shadow);
        mTvShadowRadius = view.findViewById(R.id.tv_radius);
        mTvShadowX = view.findViewById(R.id.tv_offestX);
        mTvShadowY = view.findViewById(R.id.tv_offestY);
        mTvShadowColor = view.findViewById(R.id.tv_shadowColor);
        mRadiusSeekBar = view.findViewById(R.id.radius_seekbar);
        mOffestXSeekBar = view.findViewById(R.id.offestX_seekbar);
        mOffestYSeekBar = view.findViewById(R.id.offestY_seekbar);
        switchButton = view.findViewById(R.id.switch_btn);
        mTvColor.setOnClickListener(this);
        mIvAlignment.setOnClickListener(this);
        mTvShadowColor.setOnClickListener(this);
        isInitSucceed = true;
        initSeekBarView();
        initSwitchButton();
        return view;
    }

    @Override
    public void onClick(View v) {
        if (mTextSticker == null) {
            return;
        }
        if (v == mTvColor) {
            showColorPickDialog(mTextSticker.getTextColor(),0);
        } else if(v == mTvShadowColor){
            showColorPickDialog(mTextSticker.getShadowColor(),1);

        }
        else if (v == mIvAlignment) {
            clickAlignmentBtn(mTextSticker.getAlignment());
        }

    }

    private void initSwitchButton(){
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                llShadow.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if(mTextSticker != null){
                    mTextSticker.setShadow(isChecked);
                }
            }
        });
    }

    private void initSeekBarView() {

        mLetterSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                float result = (progress - 2) * 1.0f / 10;
                if(mTextSticker != null){
                    mTextSticker.setLetterSpacing(result);
                }
                mTvLetter.setText(String.valueOf(progress));
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });

        mLineSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                float result = (progress+5) * 1.0f / 10;
                if(mTextSticker != null){
                    mTextSticker.setLineSpacingMultiplier(result);
                }
                mTvRow.setText(String.valueOf(progress));
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        mRadiusSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if(mTextSticker != null){
                    mTextSticker.setShadowRadius(progress);
                }
                mTvShadowRadius.setText(String.valueOf(progress));
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        mOffestXSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if(mTextSticker != null){
                    mTextSticker.setShadowX(progress);
                }
                mTvShadowX.setText(String.valueOf(progress));
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        mOffestYSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if(mTextSticker != null){
                    mTextSticker.setShadowY(progress);
                }
                mTvShadowY.setText(String.valueOf(progress));
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
    }

    public void setSticker(TextSticker textSticker) {
        this.mTextSticker = textSticker;
        initActionUI();
    }

    private void showColorPickDialog(String color,final int dialogId) {
        final ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(true)
                .setDialogId(dialogId)
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
                    if(dialogId == 0) {
                        mTextSticker.setTextColor(hexCode);
                        mTvColor.setBackgroundColor(color);
                    }else {
                        mTextSticker.setShadowColor(hexCode);
                        mTvShadowColor.setBackgroundColor(color);
                    }
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
            mTvShadowColor.setBackgroundColor(Color.parseColor(mTextSticker.getShadowColor()));
            initAlignmentUI(mTextSticker.getAlignment());
            initSeekBar();
            mTvLetter.setText(String.valueOf((int)(mTextSticker.getLetterSpacing()*10+2)));
            mTvRow.setText(String.valueOf((int)(mTextSticker.getLineSpacingMultiplier()*10-5)));
            mTvShadowRadius.setText(String.valueOf((int)(mTextSticker.getShadowRadius())));
            mTvShadowX.setText(String.valueOf((int)(mTextSticker.getShadowX())));
            mTvShadowY.setText(String.valueOf((int)(mTextSticker.getShadowY())));
            switchButton.setChecked(mTextSticker.isShadow());
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
            mRadiusSeekBar.setProgress((int)(mTextSticker.getShadowRadius()));
            mOffestXSeekBar.setProgress((int)(mTextSticker.getShadowX()));
            mOffestYSeekBar.setProgress((int)(mTextSticker.getShadowY()));
        }
    }

}
