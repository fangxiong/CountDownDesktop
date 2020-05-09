package com.fax.showdt.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;

import com.fax.showdt.R;
import com.fax.showdt.bean.Countdown;
import com.fax.showdt.view.timepickdialog.TimePickerView;
import com.fax.showdt.view.timepickdialog.lib.WheelView;
import com.fax.showdt.view.timepickdialog.view.WheelTime;
import com.gyf.immersionbar.ImmersionBar;

import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressLint("ValidFragment")
public class TimePickerDialog extends DialogFragment implements View.OnClickListener {

    private IClickCancelCallback mCancelCallback;
    private LinearLayout timePickerView;
    private WheelView mYear, mMonth, mDay;
    private ImageView mIvConsume;
    private ImageView mIvCancel;
    private WheelTime wheelTime;
    private Countdown countdown = new Countdown();
    private long mTargetTime;
    private IClickConsumeCallback mIClickConsumeCallback;
    private int timePickerContentHeight;

    public TimePickerDialog() {
    }

    public TimePickerDialog(long time, IClickConsumeCallback callback) {
        this.mTargetTime = time;
        this.mIClickConsumeCallback = callback;
    }

    public TimePickerDialog(int height, long time, IClickConsumeCallback callback, IClickCancelCallback cancelCallback) {
        this.timePickerContentHeight = height;
        this.mTargetTime = time;
        this.mCancelCallback = cancelCallback;
        this.mIClickConsumeCallback = callback;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        if (savedInstanceState != null) {
            this.dismiss();
        }
    }


    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_picker_dialog, container, false);
        initView(view);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                ImmersionBar.hideStatusBar(window);
            }
        }
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


    private void initView(View view) {
        timePickerView = view.findViewById(R.id.timepicker);
        mYear = timePickerView.findViewById(R.id.year);
        mMonth = timePickerView.findViewById(R.id.month);
        mDay = timePickerView.findViewById(R.id.day);
        mIvConsume = view.findViewById(R.id.iv_consume);
        mIvCancel = view.findViewById(R.id.iv_cancel);
        mIvCancel.setOnClickListener(this);
        mIvConsume.setOnClickListener(this);
        wheelTime = new WheelTime(timePickerView, TimePickerView.Type.YEAR_MONTH_DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mTargetTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mYear.setTextXOffset(40);
        mMonth.setTextXOffset(0);
        mDay.setTextXOffset(-40);
        mYear.isCenterLabel(false);
        mMonth.isCenterLabel(false);
        mDay.isCenterLabel(false);
        wheelTime.setPicker(year, month, day);
        wheelTime.setListener(new WheelTime.onTimeChangedListener() {
            @Override
            public void onTimeChanged(String date) {
                try {
                    Log.i("test_change_time:",date);
                    Date d = WheelTime.dateFormat.parse(date);
                    countdown.date.setTime(d);
                    mTargetTime = countdown.date.getTimeInMillis();
                    mIClickConsumeCallback.clickConsume(mTargetTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        if (timePickerContentHeight != 0) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) timePickerView.getLayoutParams();
            params.height = timePickerContentHeight;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            timePickerView.setLayoutParams(params);
        }


    }

    /**
     * 进度条时间选择器限制选择时间早于今天
     *
     * @param d 选择器所选日期
     * @return 限制后的日期，若选择时间大于当前时间则返回 null
     */
    private Date limitTime(Date d) {
        int finYear, finMonth, finDay;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.clear();
        calendar.setTime(d);
        int selectedYear = calendar.get(Calendar.YEAR);
        int selectedMonth = calendar.get(Calendar.MONTH);
        int selectedDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (selectedYear > year) return null;
        if (selectedYear == year && selectedMonth > month) return null;
        if (selectedYear == year && selectedMonth == month && selectedDay >= day) return null;

        finYear = selectedYear >= year ? selectedYear : year;
        finMonth = selectedMonth >= month ? selectedMonth : month;
        finDay = selectedDay > day ? selectedDay : day + 1;
        try {
            wheelTime.setPicker(finYear, finMonth, finDay);
            return new SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.CHINA
            ).parse(wheelTime.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mIvConsume) {
            if (mIClickConsumeCallback != null) {
                mIClickConsumeCallback.clickConsume(mTargetTime);
            }
            dismiss();
        } else if (v == mIvCancel) {
            dismiss();
            if (mCancelCallback != null) {
                mCancelCallback.onCancel();
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mCancelCallback != null) {
            mCancelCallback.onCancel();
        }
    }


    public interface IClickConsumeCallback {
        void clickConsume(long time);
    }

    public interface IClickCancelCallback {
        void onCancel();
    }
}
