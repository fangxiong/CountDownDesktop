package com.fax.showdt.dialog.ios.interfaces;

import android.view.View;

import com.fax.showdt.dialog.ios.util.BaseDialog;


/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/4/8 21:09
 */
public interface OnInputDialogButtonClickListener {
    
    boolean onClick(BaseDialog baseDialog, View v, String inputStr);
}
