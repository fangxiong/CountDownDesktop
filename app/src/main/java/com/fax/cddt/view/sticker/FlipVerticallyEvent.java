package com.fax.cddt.view.sticker;


import com.fax.cddt.view.sticker.AbstractFlipEvent;

public class FlipVerticallyEvent extends AbstractFlipEvent {

  @Override
  @StickerView.Flip protected int getFlipDirection() {
    return StickerView.FLIP_VERTICALLY;
  }
}
