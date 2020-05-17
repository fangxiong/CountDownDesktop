package com.fax.showdt.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.fax.showdt.R;
import com.gyf.immersionbar.ImmersionBar;

/**
 * Description:     java类作用描述
 * Author:          fax
 * CreateDate:      2020-05-07 11:53
 * Email:           fxiong1995@gmail.com
 */
public class TestActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        ImmersionBar.hideStatusBar(getWindow());

    }

}
