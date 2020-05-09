package com.fax.showdt.activity;

import android.graphics.Path;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.fax.showdt.R;
import com.fax.showdt.view.androipathview.PathView;

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
        PathView pathView = (PathView) findViewById(R.id.pathView);
        final Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.lineTo(150 / 4f, 0.0f);
        path.lineTo(150, 150 / 2.0f);
        path.lineTo(150 / 4f, 150);
        path.lineTo(0.0f, 150);
        path.lineTo(150 * 3f / 4f, 150 / 2f);
        path.lineTo(0.0f, 0.0f);
        path.close();
        pathView.setPath(path);
    }

}
