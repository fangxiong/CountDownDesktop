package com.fax.showdt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fax.showdt.R;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-28
 * Description:
 */
public class WidgetClickSettingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_click_setting_fragment, container, false);

        return view;
    }
}
