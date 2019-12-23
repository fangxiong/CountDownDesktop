package com.fax.showdt.utils;

import android.content.Context;
import android.graphics.Point;

import com.fax.lib_photo.R;
import com.fax.lib_photo.photopick.MimeType;
import com.fax.lib_photo.photopick.filter.Filter;
import com.fax.lib_photo.photopick.internal.entity.IncapableCause;
import com.fax.lib_photo.photopick.internal.entity.Item;
import com.fax.lib_photo.photopick.internal.utils.PhotoMetadataUtils;

import java.util.HashSet;
import java.util.Set;


public class GifSizeFilter extends Filter {

    private int mMinWidth;
    private int mMinHeight;
    private int mMaxSize;

   public GifSizeFilter(int minWidth, int minHeight, int maxSizeInBytes) {
        mMinWidth = minWidth;
        mMinHeight = minHeight;
        mMaxSize = maxSizeInBytes;
    }

    @Override
    public Set<MimeType> constraintTypes() {
        return new HashSet<MimeType>() {{
            add(MimeType.GIF);
        }};
    }

    @Override
    public IncapableCause filter(Context context, Item item) {
        if (!needFiltering(context, item))
            return null;

        Point size = PhotoMetadataUtils.getBitmapBound(context.getContentResolver(), item.getContentUri());
        if (size.x < mMinWidth || size.y < mMinHeight || item.size > mMaxSize) {
            return new IncapableCause(IncapableCause.DIALOG, context.getResources().getString(R.string.error_gif, mMinWidth,
                    String.valueOf(PhotoMetadataUtils.getSizeInMB(mMaxSize))));
        }
        return null;
    }

}
