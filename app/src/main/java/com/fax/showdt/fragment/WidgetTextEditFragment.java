package com.fax.showdt.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.fax.showdt.R;
import com.fax.showdt.callback.WidgetEditTextCallback;
import com.fax.showdt.callback.WidgetEditTextElementSelectedCallback;
import com.fax.showdt.callback.WidgetEditTextFontSelectedCallback;
import com.fax.showdt.dialog.TimePickerDialog;
import com.fax.showdt.dialog.WidgetTextInputDialog;
import com.fax.showdt.fragment.widgetTextEdit.WidgetTextElementEditFragment;
import com.fax.showdt.fragment.widgetTextEdit.WidgetTextFontEditFragment;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.CustomPlugUtil;
import com.fax.showdt.utils.ViewUtils;
import com.fax.showdt.view.colorPicker.ColorPickerDialog;
import com.fax.showdt.view.colorPicker.ColorPickerDialogListener;
import com.fax.showdt.view.sticker.TextSticker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class WidgetTextEditFragment extends Fragment implements View.OnClickListener {

    private ImageView mIvKeyboard, mElement,mFont, mColor, mAdd,mConsume;
    private WidgetEditTextCallback mWidgetEditTextCallback;
    private TextSticker mTextSticker;
    private WidgetTextElementEditFragment mElementEditFragment;
    private WidgetTextFontEditFragment mFontEditFragment;
    private List<View> mViews = new ArrayList<>();
    enum EditTextType {
        ELEMENT, FONT, COLOR
    }

    public WidgetTextEditFragment(){}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_text_edit_fragment, container, false);
        mIvKeyboard = view.findViewById(R.id.iv_keyboard);
        mElement = view.findViewById(R.id.iv_element);
        mFont = view.findViewById(R.id.iv_font);
        mColor = view.findViewById(R.id.iv_color);
        mAdd = view.findViewById(R.id.iv_add);
        mConsume = view.findViewById(R.id.iv_consume);
        mIvKeyboard.setOnClickListener(this);
        mElement.setOnClickListener(this);
        mFont.setOnClickListener(this);
        mColor.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        mConsume.setOnClickListener(this);
        mViews.add(mElement);
        mViews.add(mFont);
        mViews.add(mColor);
        initAllEditFragments();
        refreshSelectedViewStatus(mElement);
        switchToOneFragment(EditTextType.ELEMENT);
        return view;
    }

    public void showInputDialog(int maxLength) {
        if(mTextSticker != null) {
            final String str = mTextSticker.getText();
            final SpannableStringBuilder content = CustomPlugUtil.getSpannableStrFromSigns(str, getActivity());
            final WidgetTextInputDialog textInputDialog = new WidgetTextInputDialog(getActivity(), maxLength);
            FragmentManager fragmentManager = getChildFragmentManager();
            textInputDialog.show(fragmentManager, "textInputDialog");
            textInputDialog.setOnFontEditListener(new WidgetTextInputDialog.OnFontEditListener() {
                @Override
                public void onShow() {
                    textInputDialog.showSoftInput();
                    final EditText mEtInput = textInputDialog.getEditText();
                    mEtInput.setText(content);
                    mEtInput.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String text = mEtInput.getText().toString().trim();
                            if (mTextSticker != null) {
                                mTextSticker.setText(text);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            });
        }

    }


    public void setWidgetEditTextCallback(WidgetEditTextCallback callback) {
        this.mWidgetEditTextCallback = callback;
    }

    public void setWidgetEditTextSticker(TextSticker textSticker) {
        this.mTextSticker = textSticker;
        if(mFontEditFragment != null) {
            mFontEditFragment.initFontSelectedPos(mTextSticker.getFontPath());
        }
    }

    @Override
    public void onClick(View view) {
        int resId = view.getId();
        if (resId == R.id.iv_keyboard) {
            showInputDialog( 100);
        } else if (resId == R.id.iv_add) {
            if (mWidgetEditTextCallback != null) {
                mWidgetEditTextCallback.onAddSticker();
            }
        }else if(resId == R.id.iv_element){
            refreshSelectedViewStatus(mElement);
            switchToOneFragment(EditTextType.ELEMENT);
        }else if(resId == R.id.iv_font){
            refreshSelectedViewStatus(mFont);
            switchToOneFragment(EditTextType.FONT);

        }else if(resId == R.id.iv_color){
//            refreshSelectedViewStatus(mColor);
            if(mTextSticker != null) {
                showColorPickDialog(mTextSticker.getTextColor());
            }
        }else if(resId == R.id.iv_consume){
            if(mWidgetEditTextCallback != null){
                mWidgetEditTextCallback.closePanel();
            }
        }
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
                if (mTextSticker != null) {
                    mTextSticker.setTextColor(hexCode);
                }
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getChildFragmentManager(),"color_dialog");
    }

    private void refreshSelectedViewStatus(View view){

        for(View views:mViews){
            views.setSelected(false);
                Log.i("test_select:","unselected");
        }
        view.setSelected(true);
    }


    private void initAllEditFragments() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        mElementEditFragment = new WidgetTextElementEditFragment();
        mFontEditFragment = new WidgetTextFontEditFragment();
//        mColorEditFragment = new WidgetTextColorEditFragment();
        transaction.add(R.id.fl_text_edit_body, mFontEditFragment);
        transaction.add(R.id.fl_text_edit_body, mElementEditFragment);
//        transaction.add(R.id.fl_text_edit_body, mColorEditFragment);
        mElementEditFragment.setWidgetEditTextElementSelectedCallback(new WidgetEditTextElementSelectedCallback() {
            @Override
            public void selectTextElement(String text, boolean isCountdownPlug) {
                if(isCountdownPlug){
                    showTimePickerDialog(text);
                }else {
                    if(mTextSticker != null) {
                        String lastText = mTextSticker.getText();
                        mTextSticker.setText(lastText + text);
                    }
                }
            }
        });
//        mFontEditFragment.initFontSelectedPos(mTextSticker.getFontPath());
        mFontEditFragment.setWidgetTextFontSeelctedCallback(new WidgetEditTextFontSelectedCallback() {
            @Override
            public void selectTextFont(String fontPath) {
                if(mTextSticker != null) {
                    mTextSticker.setFontPath(fontPath);
                }
            }
        });
        transaction.commitAllowingStateLoss();
    }


    private void showTimePickerDialog(final String text) {
        long time = CustomPlugUtil.getTimerTargetTime(text);
        TimePickerDialog timePickerDialog = new TimePickerDialog(ViewUtils.dp2px(156),
                time, new TimePickerDialog.IClickConsumeCallback() {
            @Override
            public void clickConsume(long time) {
                String result = CustomPlugUtil.changeTimerConfig(text, time);
                result = CustomPlugUtil.posAndNegSwitchTimer(result, time);
                if(mTextSticker != null){
                    Log.i("test_time:",result+"");
//                    String lastText = mTextSticker.getText();
//                    mTextSticker.setText(lastText+result);
                    mTextSticker.setText(result);
                }
            }
        }, new TimePickerDialog.IClickCancelCallback() {
            @Override
            public void onCancel() {

            }
        }
        );
        timePickerDialog.show(getChildFragmentManager(), "timePickerDialog");

    }

    private void switchToOneFragment(EditTextType editTextType) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch (editTextType) {
            case ELEMENT: {
                transaction.hide(mFontEditFragment);
//                transaction.hide(mColorEditFragment);
                transaction.show(mElementEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
            case FONT: {
                transaction.hide(mElementEditFragment);
//                transaction.hide(mColorEditFragment);
                transaction.show(mFontEditFragment);
                if(mTextSticker != null) {
                    mFontEditFragment.initFontSelectedPos(mTextSticker.getFontPath());
                }
                transaction.commitAllowingStateLoss();
                break;
            }
//            case COLOR: {
//                transaction.hide(mElementEditFragment);
//                transaction.hide(mFontEditFragment);
//                transaction.show(mColorEditFragment);
//                transaction.commitAllowingStateLoss();
//                break;
//            }

        }

    }


}
