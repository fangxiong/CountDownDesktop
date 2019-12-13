package com.fax.showdt.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fax.showdt.R;


public class ProportionLayout extends FrameLayout {

    private boolean isBaseWidth = true;
    private float proportion = 1.0f;

    public ProportionLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public ProportionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ProportionLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProportionLayout);
            if (a != null) {
                isBaseWidth = a.getBoolean(R.styleable.ProportionLayout_isBaseWidth, isBaseWidth);
                String value = a.getString(R.styleable.ProportionLayout_proportion);
                if (value != null) {
                    if (value.contains(":")) {
                        String[] tmp = value.split(":");
                        if (tmp.length == 2) {
                            tmp[0] = tmp[0].trim();
                            tmp[1] = tmp[1].trim();
                            if (TextUtils.isDigitsOnly(tmp[0]) && TextUtils.isDigitsOnly(tmp[1])) {
                                int x = Integer.parseInt(tmp[0]);
                                int y = Integer.parseInt(tmp[1]);
                                if (x > 0 && y > 0) {
                                    proportion = x * 1.0f / y;
                                }
                            }
                        }
                    } else {
                        try {
                            proportion = Float.parseFloat(value);
                        } catch (Exception ex) {
                        }
                    }
                }
                a.recycle();
            }
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        super.addView(child, index, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (isBaseWidth) {
            heightSize = (int) (widthSize * proportion);
            heightMode = MeasureSpec.EXACTLY;
        } else {
            widthSize = (int) (heightSize * proportion);
            widthMode = MeasureSpec.EXACTLY;
        }
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(widthSize, widthMode),
                MeasureSpec.makeMeasureSpec(heightSize, heightMode)
        );
    }
}
