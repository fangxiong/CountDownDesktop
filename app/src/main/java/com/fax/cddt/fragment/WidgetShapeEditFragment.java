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

public class WidgetShapeEditFragment extends Fragment implements View.OnClickListener{
    private ImageView mIvLocal,mIvColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_shape_edit_fragment,container,false);
        mIvLocal = view.findViewById(R.id.iv_local);
        mIvColor = view.findViewById(R.id.iv_color);
        mIvLocal.setOnClickListener(this);
        mIvColor.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {

    }
}
