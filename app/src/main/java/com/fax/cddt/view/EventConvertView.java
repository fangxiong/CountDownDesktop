package com.fax.cddt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-9-5
 * Description:
 */
public class EventConvertView extends RelativeLayout {

    private ViewGroup mViewGroup;

    public EventConvertView(Context context) {
        this(context, null);
    }

    public EventConvertView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventConvertView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEventConvertView(ViewGroup view) {
        mViewGroup = view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mViewGroup != null) {
            mViewGroup.onInterceptTouchEvent(ev);
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mViewGroup != null) {
            mViewGroup.onTouchEvent(event);
        }
        return true;
    }
}
