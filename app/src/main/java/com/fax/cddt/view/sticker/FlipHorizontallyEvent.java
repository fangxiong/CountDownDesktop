package com.fax.cddt.view.sticker;


import com.fax.cddt.view.sticker.AbstractFlipEvent;

public class FlipHorizontallyEvent extends AbstractFlipEvent {

  @Override
  @StickerView.Flip protected int getFlipDirection() {
    return StickerView.FLIP_HORIZONTALLY;
  }
}
