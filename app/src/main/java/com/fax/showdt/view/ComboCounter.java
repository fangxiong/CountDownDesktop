package com.fax.showdt.view;


import com.fax.showdt.utils.TimeUtils;

public final class ComboCounter {
    private static final int DEFAULT_MAX_INTERVAL = 500;

    private int mMaxInterval;
    private OnComboListener mOnComboListener;
    private int mComboCount = 0;
    private int mMaxComboCount = -1;
    private long mLastClickTime = 0;
    private boolean isEnable = true;

    public ComboCounter() {
        mMaxInterval = DEFAULT_MAX_INTERVAL;
    }

    //set the Max Interval between two clicks, default is 500ms
    public final ComboCounter setMaxInterval(int maxInterval) {
        mMaxInterval = maxInterval;
        return this;
    }

    public final ComboCounter setMaxComboCount(int maxComboCount) {
        mMaxComboCount = maxComboCount;
        return this;
    }

    public final boolean isEnable() {
        return isEnable;
    }

    public final void setEnable(boolean enable) {
        isEnable = enable;
    }

    public final void click() {

        if (!isEnable) {
            return;
        }

        final long thisClickTime = TimeUtils.elapsedRealTimeMillis();
        if (thisClickTime - mLastClickTime <= mMaxInterval) {
            mComboCount++;
        } else {
            mComboCount = 1;
        }
        mLastClickTime = thisClickTime;

        final OnComboListener listener = mOnComboListener;
        final int max = mMaxComboCount;

        if (listener != null) {
            listener.onCombo(this, mComboCount, max);
        }

        if (max > 0 && mComboCount >= max) {
            mComboCount = 0;
            if (listener != null) {
                listener.onComboReach(this, mComboCount, max);
            }
        }
    }

    public final ComboCounter setOnComboListener(OnComboListener onComboListener) {
        mOnComboListener = onComboListener;
        return this;
    }

    public interface OnComboListener {
        void onCombo(ComboCounter counter, int comboCount, int maxCount);

        void onComboReach(ComboCounter counter, int comboCount, int maxCount);
    }
}