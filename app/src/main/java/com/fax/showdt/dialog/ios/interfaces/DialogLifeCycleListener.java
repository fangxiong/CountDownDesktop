package com.fax.showdt.dialog.ios.interfaces;

import com.fax.showdt.dialog.ios.util.BaseDialog;

public interface DialogLifeCycleListener {

    void onCreate(BaseDialog dialog);

    void onShow(BaseDialog dialog);

    void onDismiss(BaseDialog dialog);

}
