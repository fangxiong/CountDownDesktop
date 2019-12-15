package com.fax.showdt.callback;

import com.fax.showdt.bean.WidgetShapeBean;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-12
 * Description:
 */
public interface WidgetEditShapeCallback {
    void onAddShapeSticker(WidgetShapeBean widgetShapeBean);
    void closePanel();
}
