package com.fax.cddt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.cddt.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WidgetProgressEditFragment extends Fragment implements View.OnClickListener{

    private ImageView mIvLocal,mIvAdd;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_progress_edit_fragment,container,false);
        mIvLocal = view.findViewById(R.id.iv_local);
        mIvAdd = view.findViewById(R.id.iv_add);
        mIvLocal.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {

    }
}
