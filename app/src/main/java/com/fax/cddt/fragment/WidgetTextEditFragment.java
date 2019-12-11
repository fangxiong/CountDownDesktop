package com.fax.cddt.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.fax.cddt.R;
import com.fax.cddt.callback.WidgetEditTextCallback;
import com.fax.cddt.callback.WidgetEditTextElementSelectedCallback;
import com.fax.cddt.dialog.WidgetTextInputDialog;
import com.fax.cddt.fragment.widgetTextEdit.WidgetTextColorEditFragment;
import com.fax.cddt.fragment.widgetTextEdit.WidgetTextElementEditFragment;
import com.fax.cddt.fragment.widgetTextEdit.WidgetTextFontEditFragment;
import com.fax.cddt.utils.CustomPlugUtil;
import com.fax.cddt.view.sticker.TextSticker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class WidgetTextEditFragment extends Fragment implements View.OnClickListener {

    private ImageView mIvKeyboard, mElement,mFont, mColor, mAdd;
    private WidgetEditTextCallback mWidgetEditTextCallback;
    private Context mContext;
    private TextSticker mTextSticker;
    private WidgetTextElementEditFragment mElementEditFragment;
    private WidgetTextFontEditFragment mFontEditFragment;
    private WidgetTextColorEditFragment mColorEditFragment;
    private List<View> mViews = new ArrayList<>();
    enum EditTextType {
        ELEMENT, FONT, COLOR
    }

    public WidgetTextEditFragment(Context context) {
        mContext = context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_text_edit_fragment, container, false);
        mIvKeyboard = view.findViewById(R.id.iv_keyboard);
        mElement = view.findViewById(R.id.iv_element);
        mFont = view.findViewById(R.id.iv_font);
        mColor = view.findViewById(R.id.iv_color);
        mAdd = view.findViewById(R.id.iv_add);
        mIvKeyboard.setOnClickListener(this);
        mElement.setOnClickListener(this);
        mFont.setOnClickListener(this);
        mColor.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        mViews.add(mElement);
        mViews.add(mFont);
        mViews.add(mColor);
        initAllEditFragments();
        refreshSelectedViewStatus(mElement);
        return view;
    }

    public void showInputDialog(int maxLength) {
        final String str =mTextSticker.getText();
        final SpannableStringBuilder content = CustomPlugUtil.getSpannableStrFromSigns(str, mContext);
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


    public void setWidgetEditTextCallback(WidgetEditTextCallback callback) {
        this.mWidgetEditTextCallback = callback;
    }

    public void setWidgetEditTextSticker(TextSticker textSticker) {
        this.mTextSticker = textSticker;
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
            mElement.setSelected(true);
            refreshSelectedViewStatus(mElement);
            switchToOneFragment(EditTextType.ELEMENT);
        }else if(resId == R.id.iv_font){
            refreshSelectedViewStatus(mFont);
            switchToOneFragment(EditTextType.FONT);
        }else if(resId == R.id.iv_color){
            refreshSelectedViewStatus(mColor);
            switchToOneFragment(EditTextType.COLOR);
        }
    }

    private void refreshSelectedViewStatus(View view){
        for(View views:mViews){
            if(view == views ){
                view.setSelected(true);
            }else {
                view.setSelected(false);
            }
        }
    }


    private void initAllEditFragments() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        mElementEditFragment = new WidgetTextElementEditFragment(mContext);
        mFontEditFragment = new WidgetTextFontEditFragment();
        mColorEditFragment = new WidgetTextColorEditFragment();
        transaction.add(R.id.fl_text_edit_body, mElementEditFragment);
        transaction.add(R.id.fl_text_edit_body, mFontEditFragment);
        transaction.add(R.id.fl_text_edit_body, mColorEditFragment);
        mElementEditFragment.setWidgetEditTextElementSelectedCallback(new WidgetEditTextElementSelectedCallback() {
            @Override
            public void selectTextElement(String text, boolean isCountdownPlug) {
                if(isCountdownPlug){
                    showTimePickerDialog(text);
                }else {
                    String lastText = mTextSticker.getText();
                    mTextSticker.setText(lastText+text);
                }
            }
        });
        transaction.commitAllowingStateLoss();
    }


    private void showTimePickerDialog(final String text) {
        long time = CustomPlugUtil.getTimerTargetTime(text);
//
//        CountdownTimePickerPop pop = new CountdownTimePickerPop(getPanelHeight(),
//                time,
//                t -> {
//                    String result = CustomPlugUtil.changeTimerConfig(text, t);
//                    result = CustomPlugUtil.posAndNegSwitchTimer(result, t);
//                    mEditText.setText("");
//                    addTextToEditText(inputText + result);
//                },
//                () -> {
//                }, CountdownTimePickerPop.UseLocationType.DIYCOUNTDOWN);
//        pop.show(getChildFragmentManager(), "CountdownTimePickerPop");

    }

    private void switchToOneFragment(EditTextType editTextType) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch (editTextType) {
            case ELEMENT: {
                transaction.hide(mFontEditFragment);
                transaction.hide(mColorEditFragment);
                transaction.show(mElementEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
            case FONT: {
                transaction.hide(mElementEditFragment);
                transaction.hide(mColorEditFragment);
                transaction.show(mFontEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
            case COLOR: {
                transaction.hide(mElementEditFragment);
                transaction.hide(mFontEditFragment);
                transaction.show(mColorEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }

        }

    }


}