package com.fax.showdt.fragment.widgetProgressEdit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.dialog.ios.v3.CustomDialog;
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

    private TextView mTvStyle, mTvMode, mTvForeColor, mTvBgColor;
    private CustomDialog progressStyleDialog, progressModeDialog;
    private SeekBar seekBar;
    private ProgressSticker progressSticker;
    private boolean isInitSuced = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_progress_properties_edit_fragment, container, false);
        mTvStyle = view.findViewById(R.id.tv_progress_type);
        mTvMode = view.findViewById(R.id.tv_progress_style);
        mTvForeColor = view.findViewById(R.id.tv_foreColor);
        mTvBgColor = view.findViewById(R.id.tv_bgColor);
        seekBar = view.findViewById(R.id.seekbar);
        mTvStyle.setOnClickListener(this);
        mTvMode.setOnClickListener(this);
        mTvForeColor.setOnClickListener(this);
        mTvBgColor.setOnClickListener(this);
        initSeekBar();
        isInitSuced = true;
        initActionUI();
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == mTvStyle) {
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
                progressSticker.resize(ViewUtils.dpToPx(150, AppContext.get()),progressSticker.getProgressHeight());
                initActionUI();
            }
        } else if (v.getId() == R.id.tv_circle) {
            if (progressSticker != null) {
                progressSticker.setProgressType(WidgetProgressStyle.CIRCLE);
                progressStyleDialog.doDismiss();
                progressSticker.resize(ViewUtils.dpToPx(150, AppContext.get()),ViewUtils.dpToPx(150,AppContext.get()));
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
        }
    }

    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progressSticker != null) {
                    progressSticker.setProgressHeight(ViewUtils.dpToPx(progress, getActivity()));
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
        progressStyleDialog.setAlign(CustomDialog.ALIGN.DEFAULT).setCancelable(false).show();
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
        progressModeDialog.setAlign(CustomDialog.ALIGN.DEFAULT).setCancelable(false).show();
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
                    mTvForeColor.setBackgroundColor(color);
                } else {
                    if (progressSticker != null) {
                        progressSticker.setBgColor(hexCode);
                    }
                    mTvBgColor.setBackgroundColor(color);
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
        if (progressSticker != null && isInitSuced) {
            mTvStyle.setText(progressSticker.getProgressType().equals(WidgetProgressStyle.HORIZONTAL) ? "线性" : "圆形");
            mTvMode.setText(progressSticker.getDrawType().equals(WidgetProgressMode.SOLID) ? "平滑" : "分割");
            mTvForeColor.setBackgroundColor(Color.parseColor(progressSticker.getForeColor()));
            mTvBgColor.setBackgroundColor(Color.parseColor(progressSticker.getBgColor()));
            seekBar.setProgress(ViewUtils.pxToDp(progressSticker.getProgressHeight(), getActivity()));
        }

    }

}
