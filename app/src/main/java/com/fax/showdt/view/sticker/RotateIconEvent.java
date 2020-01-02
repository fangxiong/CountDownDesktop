package com.fax.showdt.view.sticker;

import android.view.MotionEvent;

/**
 * Created by fax on 19-6-14.
 */
public class RotateIconEvent implements StickerIconEvent {

    @Override
    public void onActionDown(StickerView stickerView, MotionEvent event) {

    }

    @Override
    public void onActionMove(StickerView stickerView, MotionEvent event) {

    }

    @Override
    public void onActionUp(StickerView stickerView, MotionEvent event) {
        if (stickerView.getCurrentSticker() != null) {
            stickerView.invalidate();
        }

    }
}
