package com.fax.showdt.view.sticker;

import android.view.MotionEvent;

/**
 * Created by fax on 19-5-6.
 */

public class BindIconEvent implements StickerIconEvent {

    @Override
    public void onActionDown(StickerView stickerView, MotionEvent event) {

    }

    @Override
    public void onActionMove(StickerView stickerView, MotionEvent event) {

    }

    @Override
    public void onActionUp(StickerView stickerView, MotionEvent event) {
        if (stickerView.getOnStickerOperationListener() != null) {
            stickerView.getOnStickerOperationListener()
                    .onClickedBindAppIcon(stickerView.getCurrentSticker());
        }
    }
}
