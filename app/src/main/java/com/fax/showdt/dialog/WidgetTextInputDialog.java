package com.fax.showdt.dialog;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.fax.showdt.R;
import com.fax.showdt.utils.RomUtils;
import com.fax.showdt.utils.ViewUtils;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-10
 * Description:
 */
@SuppressLint("ValidFragment")
public class WidgetTextInputDialog extends DialogFragment {

    private RelativeLayout mRelativeLayout;
    private EditText mEditText;
    private ImageView mAdd;
    private Context context;
    private OnFontEditListener mOnFontEditListener;
    private int max;

    public WidgetTextInputDialog() {
    }

    public WidgetTextInputDialog(Context context, int maxLength) {
        this.context = context;
        this.max = maxLength;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.dismiss();
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_text_input_dialog, container, false);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.rl_body);
        mEditText = (EditText) view.findViewById(R.id.et_font);
        mEditText.setFilters(new InputFilter[]{new TextLengthFilter(max)});
        if (RomUtils.isEmui()) {
            mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max / 2)});
        }
        mAdd = (ImageView) view.findViewById(R.id.tv_font_add);
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mOnFontEditListener.onShow();
                showSoftInput();
            }
        });
        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.hideInputMethod(mEditText);
                dismiss();
            }
        });
        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.hideInputMethod(mEditText);
                dismiss();
            }
        });
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        windowParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        windowParams.dimAmount = 0.0f;
        window.setAttributes(windowParams);
    }

    public EditText getEditText() {
        return mEditText;
    }


    public void showSoftInput() {
        ViewUtils.showInputMethodDelayed(mEditText, false, 50);
    }

    public void hideSoftInput() {
        ViewUtils.hideInputMethod(mEditText);
    }

    public void setOnFontEditListener(OnFontEditListener onFontEditListener) {
        this.mOnFontEditListener = onFontEditListener;
    }

    public interface OnFontEditListener {
        void onShow();
    }

    public class TextLengthFilter implements InputFilter {
        private final int mMax;

        public TextLengthFilter(int max) {
            mMax = max;
        }

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {

            if (source.toString().contentEquals("\n")) {
                return "";
            }
            int dindex = 0;
            int count = 0;
            while (count <= mMax && dindex < dest.length()) {
                char c = dest.charAt(dindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }

            if (count > mMax) {
                return dest.subSequence(0, dindex - 1);
            }

            int sindex = 0;
            while (count <= mMax && sindex < source.length()) {
                char c = source.charAt(sindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }

            if (count > mMax) {
                sindex--;
            }
            return source.subSequence(0, sindex);
        }

    }


}
