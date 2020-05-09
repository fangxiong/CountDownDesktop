package com.fax.showdt.fragment.widgetProgressEdit;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.dialog.ios.v3.CustomDialog;
import com.fax.showdt.manager.widget.WidgetProgress;
import com.fax.showdt.manager.widget.WidgetProgressMode;
import com.fax.showdt.manager.widget.WidgetProgressStyle;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.ViewUtils;
import com.fax.showdt.view.colorPicker.ColorPickerDialog;
import com.fax.showdt.view.colorPicker.ColorPickerDialogListener;
import com.fax.showdt.view.sticker.ProgressSticker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class WidgetProgressPropertiesEditFragment extends Fragment implements View.OnClickListener {

    private TextView mTvProgress, mTvStyle, mTvMode, mTvForeColor, mTvBgColor;
    private CustomDialog progressdialog, progressStyleDialog, progressModeDialog;
    private SeekBar seekBar;
    private ProgressSticker progressSticker;
    private boolean isInitSuccessed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_progress_properties_edit_fragment, container, false);
        mTvProgress = view.findViewById(R.id.tv_progress);
        mTvStyle = view.findViewById(R.id.tv_progress_type);
        mTvMode = view.findViewById(R.id.tv_progress_style);
        mTvForeColor = view.findViewById(R.id.tv_foreColor);
        mTvBgColor = view.findViewById(R.id.tv_bgColor);
        seekBar = view.findViewById(R.id.seekbar);
        mTvProgress.setOnClickListener(this);
        mTvStyle.setOnClickListener(this);
        mTvMode.setOnClickListener(this);
        mTvForeColor.setOnClickListener(this);
        mTvBgColor.setOnClickListener(this);
        initSeekBar();
        isInitSuccessed = true;
        initActionUI();
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == mTvProgress) {
            showProgressDialog();
        } else if (v == mTvStyle) {
            showProgressStyleDialog();
        } else if (v == mTvMode) {
            showProgressModeDialog();
        } else if (v == mTvForeColor) {
            if (progressSticker != null) {
                showColorPickDialog(progressSticker.getForeColor(), 0);
            }
        } else if (v == mTvBgColor) {
            if (progressSticker != null) {
                showColorPickDialog(progressSticker.getBgColor(), 1);
            }
        } else if (v.getId() == R.id.tv_horizontal) {
            if (progressSticker != null) {
                progressSticker.setProgressType(WidgetProgressStyle.HORIZONTAL);
                progressStyleDialog.doDismiss();
                progressSticker.resize(ViewUtils.dpToPx(150, AppContext.get()), progressSticker.getProgressHeight());
                initActionUI();
            }
        } else if (v.getId() == R.id.tv_circle) {
            if (progressSticker != null) {
                progressSticker.setProgressType(WidgetProgressStyle.CIRCLE);
                progressStyleDialog.doDismiss();
                progressSticker.resize(ViewUtils.dpToPx(150, AppContext.get()), ViewUtils.dpToPx(150, AppContext.get()));
                initActionUI();
            }
        } else if (v.getId() == R.id.tv_solid) {
            if (progressSticker != null) {
                progressSticker.setDrawType(WidgetProgressMode.SOLID);
                progressModeDialog.doDismiss();
                initActionUI();
            }
        } else if (v.getId() == R.id.tv_degree) {
            if (progressSticker != null) {
                progressSticker.setDrawType(WidgetProgressMode.DEGREE);
                progressModeDialog.doDismiss();
                initActionUI();
            }
        } else if (v.getId() == R.id.tv_battery) {
            if (progressSticker != null) {
                progressSticker.setProgress(WidgetProgress.BATTERY);
                progressdialog.doDismiss();
                initActionUI();
            }
        } else if (v.getId() == R.id.tv_music) {
            if (progressSticker != null) {
                progressSticker.setProgress(WidgetProgress.MUSIC);
                progressdialog.doDismiss();
                initActionUI();
            }
        } else if (v.getId() == R.id.tv_month) {
            if (progressSticker != null) {
                progressSticker.setProgress(WidgetProgress.MONTH);
                progressdialog.doDismiss();
                initActionUI();
            }
        } else if (v.getId() == R.id.tv_week) {
            if (progressSticker != null) {
                progressSticker.setProgress(WidgetProgress.WEEK);
                progressdialog.doDismiss();
                initActionUI();
            }
        } else if (v.getId() == R.id.tv_day) {
            if (progressSticker != null) {
                progressSticker.setProgress(WidgetProgress.DAY);
                progressdialog.doDismiss();
                initActionUI();
            }
        } else if (v.getId() == R.id.tv_hour) {
            if (progressSticker != null) {
                progressSticker.setProgress(WidgetProgress.HOUR);
                progressdialog.doDismiss();
                initActionUI();
            }
        }
    }

    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progressSticker != null) {
                    if(progressSticker.getProgressType() .equals(WidgetProgressStyle.HORIZONTAL)) {
                        progressSticker.resize(ViewUtils.dpToPx(150, AppContext.get()), progressSticker.getProgressHeight());
                        progressSticker.setProgressHeight(ViewUtils.dpToPx(progress, getActivity()));
                    }else {
                        progressSticker.resize(ViewUtils.dpToPx(150, AppContext.get()), ViewUtils.dpToPx(150, AppContext.get()));
                        progressSticker.setProgressHeight(ViewUtils.dpToPx(progress, getActivity()));
                    }
                    Log.i("test_seek:","dp:"+progress+"");
                    Log.i("test_seek:","px"+ViewUtils.dpToPx(progress, getActivity())+"");
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

    private void showProgressDialog() {
        progressdialog = CustomDialog.build(((AppCompatActivity) getActivity()), R.layout.widget_progress_select_dialog, new CustomDialog.OnBindView() {
            @Override
            public void onBind(CustomDialog dialog, View v) {
                TextView tvBattery = v.findViewById(R.id.tv_battery);
                TextView tvMusic = v.findViewById(R.id.tv_music);
                TextView tvMonth = v.findViewById(R.id.tv_month);
                TextView tvWeek = v.findViewById(R.id.tv_week);
                TextView tvDay = v.findViewById(R.id.tv_day);
                TextView tvHour = v.findViewById(R.id.tv_hour);
                tvBattery.setText(getResources().getString(R.string.widget_progress_battery));
                tvMusic.setText(getResources().getString(R.string.widget_progress_music));
                tvMonth.setText(getResources().getString(R.string.widget_progress_month));
                tvWeek.setText(getResources().getString(R.string.widget_progress_week));
                tvDay.setText(getResources().getString(R.string.widget_progress_day));
                tvHour.setText(getResources().getString(R.string.widget_progress_hour));
                tvBattery.setOnClickListener(WidgetProgressPropertiesEditFragment.this);
                tvMusic.setOnClickListener(WidgetProgressPropertiesEditFragment.this);
                tvMonth.setOnClickListener(WidgetProgressPropertiesEditFragment.this);
                tvWeek.setOnClickListener(WidgetProgressPropertiesEditFragment.this);
                tvDay.setOnClickListener(WidgetProgressPropertiesEditFragment.this);
                tvHour.setOnClickListener(WidgetProgressPropertiesEditFragment.this);
            }
        });
        progressdialog.setAlign(CustomDialog.ALIGN.DEFAULT).setCancelable(true).show();
    }

    private void showProgressStyleDialog() {
        progressStyleDialog = CustomDialog.build(((AppCompatActivity) getActivity()), R.layout.widget_progress_style_dialog, new CustomDialog.OnBindView() {
            @Override
            public void onBind(CustomDialog dialog, View v) {
                TextView tvHorizontal = v.findViewById(R.id.tv_horizontal);
                TextView tvCircle = v.findViewById(R.id.tv_circle);
                tvHorizontal.setText(getResources().getString(R.string.widget_progress_horizontal));
                tvCircle.setText(getResources().getString(R.string.widget_progress_circle));
                tvHorizontal.setOnClickListener(WidgetProgressPropertiesEditFragment.this);
                tvCircle.setOnClickListener(WidgetProgressPropertiesEditFragment.this);
            }
        });
        progressStyleDialog.setAlign(CustomDialog.ALIGN.DEFAULT).setCancelable(true).show();
    }

    private void showProgressModeDialog() {
        progressModeDialog = CustomDialog.build(((AppCompatActivity) getActivity()), R.layout.widget_progress_mode_dialog, new CustomDialog.OnBindView() {
            @Override
            public void onBind(CustomDialog dialog, View v) {
                TextView tvSolid = v.findViewById(R.id.tv_solid);
                TextView tvDegree = v.findViewById(R.id.tv_degree);
                tvSolid.setText(getResources().getString(R.string.widget_progress_solid));
                tvDegree.setText(getResources().getString(R.string.widget_progress_degree));
                tvSolid.setOnClickListener(WidgetProgressPropertiesEditFragment.this);
                tvDegree.setOnClickListener(WidgetProgressPropertiesEditFragment.this);
            }
        });
        progressModeDialog.setAlign(CustomDialog.ALIGN.DEFAULT).setCancelable(true).show();
    }

    private void showColorPickDialog(String color, final int id) {
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
                if (id == 0) {
                    if (progressSticker != null) {
                        progressSticker.setForeColor(hexCode);
                    }
                    mTvForeColor.setBackground(new ColorDrawable(color));
                } else {
                    if (progressSticker != null) {
                        progressSticker.setBgColor(hexCode);
                    }
                    mTvBgColor.setBackground(new ColorDrawable(color));
                }
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
        dialog.show(getChildFragmentManager(), "color_dialog");
    }

    public void setProgressProgressSticker(ProgressSticker progressProgressSticker) {
        this.progressSticker = progressProgressSticker;
        initActionUI();
    }

    public void initActionUI() {
        if (progressSticker != null && isInitSuccessed) {
            switch (progressSticker.getProgress()) {
                case WidgetProgress.BATTERY: {
                    mTvProgress.setText("手机电量");
                    break;
                }
                case WidgetProgress.MUSIC: {
                    mTvProgress.setText("音乐时长");
                    break;
                }
                case WidgetProgress.MONTH: {
                    mTvProgress.setText("一个月");
                    break;
                }
                case WidgetProgress.WEEK: {
                    mTvProgress.setText("一周");
                    break;
                }
                case WidgetProgress.DAY: {
                    mTvProgress.setText("一天");
                    break;
                }
                case WidgetProgress.HOUR: {
                    mTvProgress.setText("一小时");
                    break;
                }
            }
            mTvStyle.setText(progressSticker.getProgressType().equals(WidgetProgressStyle.HORIZONTAL) ? "线性" : "圆形");
            mTvMode.setText(progressSticker.getDrawType().equals(WidgetProgressMode.SOLID) ? "平滑" : "分割");
            mTvForeColor.setBackgroundColor(Color.parseColor(progressSticker.getForeColor()));
            mTvBgColor.setBackgroundColor(Color.parseColor(progressSticker.getBgColor()));
            seekBar.setProgress(ViewUtils.pxToDp(progressSticker.getProgressHeight(), getActivity()));
            Log.i("test_seek2:","dp:"+ViewUtils.pxToDp(progressSticker.getProgressHeight(), getActivity())+"");
            Log.i("test_seek2:","px"+progressSticker.getProgressHeight()+"");
        }

    }

}
