package com.fax.showdt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.showdt.R;
import com.fax.showdt.callback.WidgetEditProgressCallback;
import com.fax.showdt.fragment.widgetProgressEdit.WidgetProgressPropertiesEditFragment;
import com.fax.showdt.view.sticker.ProgressSticker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class WidgetProgressEditFragment extends Fragment implements View.OnClickListener{

    private ImageView mIvLocal,mIvAdd,mIvConsume;
    private ProgressSticker progressSticker;
    private WidgetEditProgressCallback widgetEditProgressCallback;
    private WidgetProgressPropertiesEditFragment editFragment;

    public WidgetProgressEditFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_progress_edit_fragment,container,false);
        mIvLocal = view.findViewById(R.id.iv_local);
        mIvAdd = view.findViewById(R.id.iv_add);
        mIvConsume = view.findViewById(R.id.iv_consume);
        mIvLocal.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
        mIvConsume.setOnClickListener(this);
        initFragment();
        return view;
    }


    private void initFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        editFragment = new WidgetProgressPropertiesEditFragment();
        editFragment.setProgressProgressSticker(progressSticker);
        transaction.add(R.id.fl_progress_body, editFragment);
        transaction.commitAllowingStateLoss();
    }

    public void setProgressSticker(ProgressSticker sticker){
        progressSticker = sticker;
        if(editFragment != null){
            editFragment.setProgressProgressSticker(sticker);
        }
    }

    public void setWidgetEditProgressCallback(WidgetEditProgressCallback widgetEditProgressCallback) {
        this.widgetEditProgressCallback = widgetEditProgressCallback;
    }

    @Override
    public void onClick(View view) {
        if(view == mIvAdd){
            if(widgetEditProgressCallback != null){
                widgetEditProgressCallback.onAddProgressSticker();
            }
        }else if(view == mIvConsume){
            if(widgetEditProgressCallback != null){
                widgetEditProgressCallback.closePanel();
            }
        }

    }
}
